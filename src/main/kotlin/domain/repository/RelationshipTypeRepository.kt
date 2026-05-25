package fr.dynalgo.domain.repository

import fr.dynalgo.domain.model.RelationshipType

interface RelationshipTypeRepository {

    suspend fun findById(id: Long): RelationshipType?
    suspend fun findByName(name: String): RelationshipType?
    suspend fun findAll(): List<RelationshipType>
    suspend fun save(relationshipType: RelationshipType): RelationshipType
    suspend fun deleteById(id: Long): Boolean
}