package fr.dynalgo.infrastructure.web.routes

import fr.dynalgo.application.dto.EntityRequest
import fr.dynalgo.application.mapper.EntityMapper
import fr.dynalgo.domain.service.EntityService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.entityRoutes(entityService: EntityService) {
    route("/entities") {

        get {
            val entities = entityService.findAll().map(EntityMapper::toResponse)
            call.respond(entities)
        }

        post {
            val request = call.receive<EntityRequest>()
            val saved = entityService.create(
                name = request.name,
                type = request.type,
                color = request.color,
                description = request.description
            )
            call.respond(EntityMapper.toResponse(saved))
        }

        put("{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@put call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)

            val request = call.receive<EntityRequest>()
            val updated = entityService.update(
                id = id,
                name = request.name,
                type = request.type,
                color = request.color,
                description = request.description
            )
            call.respond(EntityMapper.toResponse(updated))
        }

        delete("{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@delete call.respondText("Invalid ID", status = HttpStatusCode.BadRequest)

            val deleted = entityService.deleteById(id)
            if (deleted) {
                call.respondText("Entity deleted", status = HttpStatusCode.OK)
            } else {
                call.respondText("Entity not found", status = HttpStatusCode.NotFound)
            }
        }
    }
}
