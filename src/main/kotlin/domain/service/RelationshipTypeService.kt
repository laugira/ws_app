package fr.dynalgo.domain.service

import fr.dynalgo.domain.model.RelationshipType
import fr.dynalgo.domain.repository.RelationshipTypeRepository

class RelationshipTypeService(
    private val relationshipTypeRepository: RelationshipTypeRepository
) {

    suspend fun findById(id: Long): RelationshipType? =
        relationshipTypeRepository.findById(id)

    suspend fun findAll(): List<RelationshipType> =
        relationshipTypeRepository.findAll()

    suspend fun save(relationshipType: RelationshipType): RelationshipType =
        relationshipTypeRepository.save(relationshipType)

    suspend fun deleteById(id: Long): Boolean =
        relationshipTypeRepository.deleteById(id)
}
