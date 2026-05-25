package fr.dynalgo.infrastructure.persistence.entity.repository

import common.math.Rational
import fr.dynalgo.domain.model.Entity
import fr.dynalgo.domain.model.Relationship
import fr.dynalgo.domain.model.RelationshipType
import fr.dynalgo.domain.repository.RelationshipRepository
import fr.dynalgo.infrastructure.persistence.entity.Entities
import fr.dynalgo.infrastructure.persistence.entity.Relationships
import fr.dynalgo.infrastructure.persistence.entity.RelationshipTypes
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class RelationshipRepositoryImpl : RelationshipRepository {

    private val targetEntities = Entities.alias("target")

    private fun joinedQuery() = Relationships
        .innerJoin(Entities, { Relationships.sourceId }, { Entities.id })
        .innerJoin(targetEntities, { Relationships.targetId }, { targetEntities[Entities.id] })
        .innerJoin(RelationshipTypes, { Relationships.relationshipTypeId }, { RelationshipTypes.id })

    override suspend fun findById(id: Long): Relationship? = transaction {
        joinedQuery()
            .select { Relationships.id eq id }
            .singleOrNull()
            ?.toRelationship()
    }

    override suspend fun findAll(): List<Relationship> = transaction {
        joinedQuery()
            .selectAll()
            .map { it.toRelationship() }
    }

    override suspend fun save(relationship: Relationship): Relationship = transaction {
        val typeId = relationship.type.id
            ?: error("Relationship type id is required to save relationship")

        val existingId = relationship.id
        if (existingId != null) {
            val updated = Relationships.update({ Relationships.id eq existingId }) {
                it[sourceId] = relationship.source.id
                it[targetId] = relationship.target.id
                it[relationshipTypeId] = typeId
                it[weightNumerator] = relationship.weight.numerator
                it[weightDenominator] = relationship.weight.denominator
                it[label] = relationship.label
            }
            if (updated > 0) {
                return@transaction relationship
            }
        }

        val id = Relationships.insertAndGetId {
            it[sourceId] = relationship.source.id
            it[targetId] = relationship.target.id
            it[relationshipTypeId] = typeId
            it[weightNumerator] = relationship.weight.numerator
            it[weightDenominator] = relationship.weight.denominator
            it[label] = relationship.label
        }.value

        relationship.copy(id = id)
    }

    override suspend fun deleteById(id: Long): Boolean = transaction {
        Relationships.deleteWhere { Relationships.id eq id } > 0
    }

    override suspend fun findBySourceAndTarget(sourceId: Long, targetId: Long): List<Relationship> =
        transaction {
            joinedQuery()
                .select { (Relationships.sourceId eq sourceId) and (Relationships.targetId eq targetId) }
                .map { it.toRelationship() }
        }

    private fun ResultRow.toRelationship(): Relationship {
        val source = Entity(
            id = this[Entities.id].value,
            name = this[Entities.name],
            type = this[Entities.type],
            color = this[Entities.color],
            description = this[Entities.description]
        )

        val target = Entity(
            id = this[targetEntities[Entities.id]].value,
            name = this[targetEntities[Entities.name]],
            type = this[targetEntities[Entities.type]],
            color = this[targetEntities[Entities.color]],
            description = this[targetEntities[Entities.description]]
        )

        val relationshipType = RelationshipType(
            id = this[RelationshipTypes.id].value,
            name = this[RelationshipTypes.name],
            displayLabel = this[RelationshipTypes.displayLabel],
            color = this[RelationshipTypes.color],
            lineStyle = this[RelationshipTypes.lineStyle],
            description = this[RelationshipTypes.description]
        )

        val weight = Rational(
            this[Relationships.weightNumerator],
            this[Relationships.weightDenominator]
        ).getOrThrow()

        return Relationship(
            id = this[Relationships.id].value,
            source = source,
            target = target,
            type = relationshipType,
            weight = weight,
            label = this[Relationships.label]
        )
    }
}
