package fr.dynalgo.domain.repository

import fr.dynalgo.domain.model.Relationship

interface RelationshipRepository {

    suspend fun findById(id: Long): Relationship?
    suspend fun findAll(): List<Relationship>
    suspend fun save(relationship: Relationship): Relationship
    suspend fun deleteById(id: Long): Boolean

    /**
     * Find all relationships of a specific type between two entities.
     */
    suspend fun findBySourceAndTarget(sourceId: Long, targetId: Long): List<Relationship>
}