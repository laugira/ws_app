package fr.dynalgo.application.dto

data class EntityResponse(
    val id: Long,
    val name: String,
    val type: String,
    val color: String?,
    val description: String?
)
