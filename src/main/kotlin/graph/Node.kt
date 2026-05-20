package graph

class Node<T>(
    val id: Int, val data: T? = null, val label: String = id.toString()
) {
    override fun toString(): String {
        return "GraphNode(id=$id, data=$data, label=$label)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Node<*>

        return id == other.id
    }

    override fun hashCode(): Int {
        return id
    }
}