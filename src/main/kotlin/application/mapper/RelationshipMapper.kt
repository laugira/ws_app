package fr.dynalgo.application.mapper

import fr.dynalgo.application.dto.RelationshipResponse
import fr.dynalgo.application.dto.RelationshipTypeResponse
import fr.dynalgo.domain.model.Relationship
import fr.dynalgo.domain.model.RelationshipType

object RelationshipMapper {

    fun toResponse(relationship: Relationship): RelationshipResponse {
        val id = relationship.id
            ?: error("Relationship id is required for API response")

        return RelationshipResponse(
            id = id,
            source = EntityMapper.toResponse(relationship.source),
            target = EntityMapper.toResponse(relationship.target),
            type = toTypeResponse(relationship.type),
            label = relationship.label,
            weight = relationship.weight.toString()
        )
    }

    fun toTypeResponse(type: RelationshipType): RelationshipTypeResponse = RelationshipTypeResponse(
        id = type.id,
        name = type.name,
        displayLabel = type.displayLabel,
        color = type.color,
        lineStyle = type.lineStyle,
        description = type.description
    )
}
