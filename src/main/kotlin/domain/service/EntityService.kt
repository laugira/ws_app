package fr.dynalgo.domain.service

import fr.dynalgo.domain.exception.BadRequestException
import fr.dynalgo.domain.exception.NotFoundException
import fr.dynalgo.domain.model.Entity
import fr.dynalgo.domain.repository.EntityRepository

/**
 * Business service for Entity operations.
 * Contains all business rules related to entities.
 */
class EntityService(
    private val entityRepository: EntityRepository
) {

    suspend fun findById(id: Long): Entity? {
        return entityRepository.findById(id)
    }

    suspend fun findAll(): List<Entity> {
        return entityRepository.findAll()
    }

    suspend fun create(name: String, type: String, color: String?, description: String? = null): Entity {
        if (name.isBlank()) {
            throw BadRequestException("Entity name is required")
        }
        if (type.isBlank()) {
            throw BadRequestException("Entity type is required")
        }
        return entityRepository.save(
            Entity(
                id = 0,
                name = name,
                type = type,
                color = color,
                description = description
            )
        )
    }

    suspend fun update(
        id: Long,
        name: String,
        type: String,
        color: String?,
        description: String? = null
    ): Entity {
        if (name.isBlank()) {
            throw BadRequestException("Entity name is required")
        }
        if (type.isBlank()) {
            throw BadRequestException("Entity type is required")
        }
        entityRepository.findById(id) ?: throw NotFoundException("Entity not found")

        return entityRepository.save(
            Entity(
                id = id,
                name = name,
                type = type,
                color = color,
                description = description
            )
        )
    }

    suspend fun save(entity: Entity): Entity {
        return entityRepository.save(entity)
    }

    suspend fun deleteById(id: Long): Boolean {
        return entityRepository.deleteById(id)
    }
}
