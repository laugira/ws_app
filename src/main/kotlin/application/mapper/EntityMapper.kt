package fr.dynalgo.application.mapper

import fr.dynalgo.application.dto.EntityRequest
import fr.dynalgo.application.dto.EntityResponse
import fr.dynalgo.domain.model.Entity

object EntityMapper {

    fun toDomain(request: EntityRequest): Entity = Entity(
        id = 0,
        name = request.name,
        type = request.type,
        color = request.color,
        description = request.description
    )

    fun toResponse(entity: Entity): EntityResponse = EntityResponse(
        id = entity.id,
        name = entity.name,
        type = entity.type,
        color = entity.color,
        description = entity.description
    )
}
