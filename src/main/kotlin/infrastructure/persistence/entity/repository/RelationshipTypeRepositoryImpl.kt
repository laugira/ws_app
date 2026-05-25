package fr.dynalgo.infrastructure.persistence.entity.repository

import fr.dynalgo.domain.model.RelationshipType
import fr.dynalgo.domain.repository.RelationshipTypeRepository
import fr.dynalgo.infrastructure.persistence.entity.RelationshipTypes
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

class RelationshipTypeRepositoryImpl : RelationshipTypeRepository {

    private val logger = LoggerFactory.getLogger(RelationshipTypeRepositoryImpl::class.java)

    override suspend fun findById(id: Long): RelationshipType? = transaction {
        RelationshipTypes.select { RelationshipTypes.id eq id }
            .singleOrNull()
            ?.toRelationshipType()
    }

    override suspend fun findByName(name: String): RelationshipType? = transaction {
        RelationshipTypes.select { RelationshipTypes.name eq name }
            .singleOrNull()
            ?.toRelationshipType()
    }

    override suspend fun findAll(): List<RelationshipType> = transaction {
        RelationshipTypes.selectAll().map { it.toRelationshipType() }
    }

    override suspend fun save(relationshipType: RelationshipType): RelationshipType = transaction {
        val existingId = relationshipType.id
        if (existingId != null) {
            val updated = RelationshipTypes.update({ RelationshipTypes.id eq existingId }) {
                it[name] = relationshipType.name
                it[displayLabel] = relationshipType.displayLabel
                it[color] = relationshipType.color
                it[lineStyle] = relationshipType.lineStyle
                it[description] = relationshipType.description
            }
            if (updated > 0) {
                return@transaction relationshipType
            }
        }

        val id = RelationshipTypes.insertAndGetId {
            it[name] = relationshipType.name
            it[displayLabel] = relationshipType.displayLabel
            it[color] = relationshipType.color
            it[lineStyle] = relationshipType.lineStyle
            it[description] = relationshipType.description
        }.value

        relationshipType.copy(id = id)
    }

    override suspend fun deleteById(id: Long): Boolean = transaction {
        val deleted = RelationshipTypes.deleteWhere { RelationshipTypes.id eq id }
        deleted > 0
    }

    private fun ResultRow.toRelationshipType(): RelationshipType = RelationshipType(
        id = this[RelationshipTypes.id].value,
        name = this[RelationshipTypes.name],
        displayLabel = this[RelationshipTypes.displayLabel],
        color = this[RelationshipTypes.color],
        lineStyle = this[RelationshipTypes.lineStyle],
        description = this[RelationshipTypes.description]
    )
}
