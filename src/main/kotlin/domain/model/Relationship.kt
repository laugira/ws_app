package fr.dynalgo.domain.model

import common.graph.Link
import common.math.Rational

/**
 * Business domain object representing a relationship between two entities.
 * This class provides a clean domain layer on top of the technical Link.
 */
data class Relationship(
    val id: Long? = null,
    val source: Entity,
    val target: Entity,
    val type: RelationshipType,
    val weight: Rational = Rational(1),
    val label: String? = null
) {

    /**
     * Converts this domain Relationship into a technical Link<Entity>.
     */
    fun toLink(): Link<Entity> = Link(
        nFrom = source.toNode(),
        nTo = target.toNode(),
        weight = weight,
        label = label ?: type.name
    )

    companion object {
        /**
         * Creates a Relationship from a technical Link<Entity>.
         */
        fun fromLink(link: Link<Entity>, type: RelationshipType): Relationship {
            val source = link.nFrom.data
                ?: error("Source entity missing in link $link")
            val target = link.nTo.data
                ?: error("Target entity missing in link $link")

            return Relationship(
                source = source,
                target = target,
                type = type,
                weight = link.weight,
                label = link.label
            )
        }
    }
}