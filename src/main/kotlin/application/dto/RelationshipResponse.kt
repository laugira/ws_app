package fr.dynalgo.application.dto

data class RelationshipResponse(
    val id: Long,
    val source: EntityResponse,
    val target: EntityResponse,
    val type: RelationshipTypeResponse,
    val label: String?,
    val weight: String
)

data class RelationshipTypeResponse(
    val id: Long?,
    val name: String,
    val displayLabel: String?,
    val color: String,
    val lineStyle: String,
    val description: String?
)
