package fr.dynalgo.domain.service

import fr.dynalgo.domain.exception.BadRequestException
import fr.dynalgo.domain.exception.NotFoundException
import fr.dynalgo.domain.model.Relationship
import fr.dynalgo.domain.model.RelationshipType
import fr.dynalgo.domain.repository.EntityRepository
import fr.dynalgo.domain.repository.RelationshipRepository
import fr.dynalgo.domain.repository.RelationshipTypeRepository

/**
 * Business service for Relationship operations.
 * Contains business rules related to relationships between entities.
 */
class RelationshipService(
    private val relationshipRepository: RelationshipRepository,
    private val entityRepository: EntityRepository,
    private val relationshipTypeRepository: RelationshipTypeRepository
) {

    suspend fun findById(id: Long): Relationship? {
        return relationshipRepository.findById(id)
    }

    suspend fun findAll(): List<Relationship> {
        return relationshipRepository.findAll()
    }

    suspend fun create(
        sourceId: Long,
        targetId: Long,
        typeName: String,
        color: String = "#60a5fa",
        label: String? = null,
        lineStyle: String = "solid"
    ): Relationship {
        if (typeName.isBlank()) {
            throw BadRequestException("Relationship type name is required")
        }
        if (sourceId == targetId) {
            throw BadRequestException("Source and target entities must be different")
        }

        val source = entityRepository.findById(sourceId)
            ?: throw NotFoundException("Source entity not found")
        val target = entityRepository.findById(targetId)
            ?: throw NotFoundException("Target entity not found")

        val relationshipType = findOrCreateRelationshipType(typeName, color, lineStyle, label)

        return relationshipRepository.save(
            Relationship(
                source = source,
                target = target,
                type = relationshipType,
                label = null
            )
        )
    }

    suspend fun update(
        id: Long,
        sourceId: Long,
        targetId: Long,
        typeName: String
    ): Relationship {
        if (typeName.isBlank()) {
            throw BadRequestException("Relationship type name is required")
        }
        if (sourceId == targetId) {
            throw BadRequestException("Source and target entities must be different")
        }

        relationshipRepository.findById(id) ?: throw NotFoundException("Relationship not found")

        val source = entityRepository.findById(sourceId)
            ?: throw NotFoundException("Source entity not found")
        val target = entityRepository.findById(targetId)
            ?: throw NotFoundException("Target entity not found")

        val relationshipType = relationshipTypeRepository.findByName(typeName)
            ?: throw NotFoundException("Relationship type '$typeName' not found")

        return relationshipRepository.save(
            Relationship(
                id = id,
                source = source,
                target = target,
                type = relationshipType,
                label = null
            )
        )
    }

    private suspend fun findOrCreateRelationshipType(
        typeName: String,
        color: String,
        lineStyle: String,
        displayLabel: String?
    ): RelationshipType {
        relationshipTypeRepository.findByName(typeName)?.let { return it }

        return relationshipTypeRepository.save(
            RelationshipType(
                name = typeName,
                displayLabel = displayLabel?.takeIf { it.isNotBlank() },
                color = color,
                lineStyle = lineStyle
            )
        )
    }

    suspend fun save(relationship: Relationship): Relationship {
        return relationshipRepository.save(relationship)
    }

    suspend fun deleteById(id: Long): Boolean {
        return relationshipRepository.deleteById(id)
    }

    suspend fun findBySourceAndTarget(sourceId: Long, targetId: Long): List<Relationship> {
        return relationshipRepository.findBySourceAndTarget(sourceId, targetId)
    }
}
