package common.graph

import common.math.Matrix
import common.math.Rational
import common.utils.mapChain

class Graph<T> private constructor(
    val nodes: List<Node<T>>,
    val matrix: Matrix,
) {
    private fun nodeIndex(node: Node<T>): Result<Int> =
        nodes.indexOf(node).takeIf { it >= 0 }?.let { Result.success(it) }
            ?: Result.failure(IllegalArgumentException("node $node doesn't exist"))

    fun weight(nFrom: Node<T>, nTo: Node<T>): Result<Rational> = nodeIndex(nFrom).mapChain { from ->
        nodeIndex(nTo).mapChain { to ->
            matrix[from, to]
        }
    }

    fun from(node: Node<T>): Result<List<Node<T>>> = nodeIndex(node).mapChain { idx ->
        matrix.rowElements(idx).map { rowElements ->
            rowElements.mapIndexed { colIdx, w -> if (w != matrix.defaultValue) colIdx else -1 }.filter { it >= 0 }
                .map { nodes[it] }
        }
    }

    fun to(node: Node<T>): Result<List<Node<T>>> = nodeIndex(node).mapChain { idx ->
        matrix.colElements(idx).map { rowElements ->
            rowElements.mapIndexed { rowIdx, w -> if (w != matrix.defaultValue) rowIdx else -1 }.filter { it >= 0 }
                .map { nodes[it] }
        }
    }

    companion object {
        operator fun <T> invoke(nodes: List<Node<T>>, matrix: Matrix): Result<Graph<T>> {
            return if (nodes.size == matrix.rowCount() && matrix.isSquared()) {
                Result.success(Graph(nodes, matrix))
            } else {
                Result.failure(IllegalArgumentException("nodes size (${nodes.size}) is different from matrix rowcount (${matrix.rowCount()}), or matrix rowcount is different from matrix colcount (${matrix.colCount()})"))
            }
        }

    }
}
