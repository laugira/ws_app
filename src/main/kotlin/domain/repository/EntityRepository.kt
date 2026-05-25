package fr.dynalgo.domain.repository

import fr.dynalgo.domain.model.Entity

/**
 * Repository interface for Entity operations.
 * All methods are suspend because we will use them with coroutines later.
 */
interface EntityRepository {

    suspend fun findById(id: Long): Entity?
    suspend fun findAll(): List<Entity>
    suspend fun save(entity: Entity): Entity
    suspend fun deleteById(id: Long): Boolean
}