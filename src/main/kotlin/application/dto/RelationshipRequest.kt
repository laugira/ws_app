package fr.dynalgo.application.dto

data class RelationshipRequest(
    val sourceId: Long,
    val targetId: Long,
    val typeName: String,
    val color: String? = "#60a5fa",
    val label: String? = null,
    val lineStyle: String = "solid"
)
