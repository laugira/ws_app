package fr.dynalgo.domain.model

/**
 * Defines one type of relationship in the multigraph.
 * Each RelationshipType corresponds to its own independent Graph<Entity>.
 */
data class RelationshipType(
    val id: Long? = null,
    val name: String,           // e.g. "WORKS_WITH", "IS_FRIEND_OF", "FINANCES"
    val displayLabel: String? = null,
    val color: String,
    val lineStyle: String = "solid",
    val description: String? = null
)