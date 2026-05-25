package fr.dynalgo.domain.model

import common.graph.Node

/**
 * Represents any node in the multigraph.
 * It wraps the technical Node from the common.graph library.
 */
data class Entity(
    val id: Long,
    val name: String,
    val type: String,
    val color: String? = null,
    val description: String? = null
) {

    /**
     * Converts this Entity into a technical Node<Entity> from the library.
     */
    fun toNode(): Node<Entity> = Node(
        id = id,
        data = this,
        label = name
    )
}