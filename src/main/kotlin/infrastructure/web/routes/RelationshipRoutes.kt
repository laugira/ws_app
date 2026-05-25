package fr.dynalgo.infrastructure.web.routes

import fr.dynalgo.application.dto.RelationshipRequest
import fr.dynalgo.application.mapper.RelationshipMapper
import fr.dynalgo.domain.service.RelationshipService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.relationshipRoutes(relationshipService: RelationshipService) {
    route("/relationships") {

        get {
            val relationships = relationshipService.findAll().map(RelationshipMapper::toResponse)
            call.respond(relationships)
        }

        post {
            val request = call.receive<RelationshipRequest>()
            val saved = relationshipService.create(
                sourceId = request.sourceId,
                targetId = request.targetId,
                typeName = request.typeName,
                color = request.color ?: "#60a5fa",
                label = request.label,
                lineStyle = request.lineStyle
            )
            call.respond(RelationshipMapper.toResponse(saved))
        }

        put("{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@put call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)

            val request = call.receive<RelationshipRequest>()
            val updated = relationshipService.update(
                id = id,
                sourceId = request.sourceId,
                targetId = request.targetId,
                typeName = request.typeName
            )
            call.respond(RelationshipMapper.toResponse(updated))
        }

        delete("{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@delete call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)

            val deleted = relationshipService.deleteById(id)
            if (deleted) {
                call.respondText("Relationship deleted", status = HttpStatusCode.OK)
            } else {
                call.respondText("Relationship not found", status = HttpStatusCode.NotFound)
            }
        }
    }
}
