package fr.dynalgo.application.dto

data class EntityRequest(
    val name: String,
    val type: String,
    val color: String? = null,
    val description: String? = null
)
