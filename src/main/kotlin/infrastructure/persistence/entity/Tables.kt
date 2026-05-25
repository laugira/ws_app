package fr.dynalgo.infrastructure.persistence.entity

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object Entities : LongIdTable("entities") {
    val name = varchar("name", 255)
    val type = varchar("type", 100)
    val color = varchar("color", 50).nullable()
    val description = text("description").nullable()
}

object RelationshipTypes : LongIdTable("relationship_types") {
    val name = varchar("name", 100)
    val displayLabel = varchar("display_label", 255).nullable()
    val color = varchar("color", 50)
    val lineStyle = varchar("line_style", 20).default("solid")
    val description = text("description").nullable()
}

object Relationships : LongIdTable("relationships") {
    val sourceId = long("source_id").references(Entities.id, onDelete = ReferenceOption.CASCADE)
    val targetId = long("target_id").references(Entities.id, onDelete = ReferenceOption.CASCADE)
    val relationshipTypeId = long("relationship_type_id").references(RelationshipTypes.id, onDelete = ReferenceOption.CASCADE)
    val weightNumerator = integer("weight_numerator").default(1)
    val weightDenominator = integer("weight_denominator").default(1)
    val label = varchar("label", 255).nullable()
}