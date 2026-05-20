package graph

import math.Rational

class Link<T>(
    val nFrom: Node<T>, val nTo: Node<T>, val weight: Rational, val label: String = weight.toString()
) {
    fun opposite(): Link<T> = Link(nTo, nFrom, weight, label)

    override fun toString(): String = "GraphLink(nFrom=$nFrom, nTo=$nTo, weight=$weight, label='$label')"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Link<*>

        if (nFrom != other.nFrom) return false
        if (nTo != other.nTo) return false

        return true
    }

    override fun hashCode(): Int {
        var result = nFrom.hashCode()
        result = 31 * result + nTo.hashCode()
        return result
    }
}
