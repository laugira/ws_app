package fr.dynalgo.infrastructure.persistence.entity.repository

import fr.dynalgo.domain.model.Entity
import fr.dynalgo.domain.repository.EntityRepository
import fr.dynalgo.infrastructure.persistence.entity.Entities
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

class EntityRepositoryImpl : EntityRepository {

    private val logger = LoggerFactory.getLogger(EntityRepositoryImpl::class.java)

    override suspend fun findById(id: Long): Entity? = transaction {
        Entities.select { Entities.id eq id }
            .singleOrNull()
            ?.let { row ->
                Entity(
                    id = row[Entities.id].value,
                    name = row[Entities.name],
                    type = row[Entities.type],
                    color = row[Entities.color],
                    description = row[Entities.description]
                )
            }
    }

    override suspend fun findAll(): List<Entity> = transaction {
        Entities.selectAll().map { row ->
            Entity(
                id = row[Entities.id].value,
                name = row[Entities.name],
                type = row[Entities.type],
                color = row[Entities.color],
                description = row[Entities.description]
            )
        }
    }

    override suspend fun save(entity: Entity): Entity = transaction {
        if (entity.id > 0) {
            val updated = Entities.update({ Entities.id eq entity.id }) {
                it[name] = entity.name
                it[type] = entity.type
                it[color] = entity.color
                it[description] = entity.description
            }
            if (updated > 0) {
                return@transaction entity
            }
        }

        val id = Entities.insertAndGetId {
            it[name] = entity.name
            it[type] = entity.type
            it[color] = entity.color
            it[description] = entity.description
        }.value

        entity.copy(id = id)
    }

    override suspend fun deleteById(id: Long): Boolean = transaction {
        val deleted = Entities.deleteWhere { Entities.id eq id }
        deleted > 0
    }
}