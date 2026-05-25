package common.graph

import common.math.Matrix
import common.math.Rational
import common.utils.mapChain

class Builder<T> {
    private var nodes: MutableSet<Node<T>> = mutableSetOf()

    private var links: MutableSet<Link<T>> = mutableSetOf()

    private fun findNode(node: Node<T>): Node<T>? = nodes.find { it == node }
    private fun findLink(from: Node<T>, to: Node<T>): Link<T>? = links(from, to).firstOrNull()

    fun add(node: Node<T>): Result<Node<T>> {
        val existingNode = findNode(node)
        return if (existingNode != null) {
            Result.failure(IllegalArgumentException("node $existingNode already exists"))
        } else {
            nodes.add(node)
            Result.success(node)
        }
    }

    fun remove(node: Node<T>): Result<Node<T>> {
        val existingNode = findNode(node)
            ?: return Result.failure(IllegalArgumentException("node $node doesn't exist"))

        for (link in links(existingNode, null)) {
            unlink(link.nFrom, link.nTo).onFailure { return Result.failure(it) }
        }
        for (link in links(null, existingNode)) {
            unlink(link.nFrom, link.nTo).onFailure { return Result.failure(it) }
        }

        nodes.remove(existingNode)
        return Result.success(existingNode)
    }

    fun link(
        from: Node<T>, to: Node<T>, oppositeToo: Boolean = false
    ): Result<Link<T>> = link(from, to, Rational(1), oppositeToo)

    fun link(
        from: Node<T>, to: Node<T>, weight: Rational, oppositeToo: Boolean = false
    ): Result<Link<T>> {
        val nFromResult = findNode(from)?.let { Result.success(it) } ?: add(from)
        val nToResult = findNode(to)?.let { Result.success(it) } ?: add(to)
        return nFromResult.mapChain { nFrom ->
            nToResult.mapChain { nTo ->
                val existingLink = findLink(nFrom, nTo)
                val existingOppositeLink = findLink(nTo, nFrom)
                if (existingLink != null) {
                    Result.failure(IllegalArgumentException("link $existingLink already exists"))
                } else if (oppositeToo && existingOppositeLink != null) {
                    Result.failure(IllegalArgumentException("link $existingOppositeLink already exists"))
                } else {
                    val newLink = Link(nFrom, nTo, weight)
                    links.add(newLink)
                    if (oppositeToo) {
                        links.add(newLink.opposite())
                    }
                    Result.success(newLink)
                }
            }
        }
    }

    fun unlink(
        from: Node<T>, to: Node<T>, oppositeToo: Boolean = false
    ): Result<Link<T>> {
        val nFrom = findNode(from) ?: return Result.failure(IllegalArgumentException("node $from doesn't exist"))
        val nTo = findNode(to) ?: return Result.failure(IllegalArgumentException("node $to doesn't exist"))
        val existingLink = findLink(nFrom, nTo)
            ?: return Result.failure(IllegalArgumentException("link from node $nFrom to node $nTo doesn't exist"))
        links.remove(existingLink)
        if (oppositeToo) {
            findLink(nTo, nFrom)?.let { links.remove(it) }
        }
        return Result.success(existingLink)
    }

    private fun links(
        nFrom: Node<T>?,
        nTo: Node<T>?,
    ): List<Link<T>> = links.filter {
        (nFrom == null || it.nFrom == nFrom) && (nTo == null || it.nTo == nTo)
    }

    fun build(): Result<Graph<T>> {
        val nodeList = nodes.toList().sortedBy { it.id }
        val matrixData = List(nodeList.size) { i ->
            List(nodeList.size) { j ->
                findLink(nodeList[i], nodeList[j])?.weight
            }
        }
        return Matrix(matrixData).mapChain { matrix ->
            Graph(nodeList, matrix)
        }
    }
}
