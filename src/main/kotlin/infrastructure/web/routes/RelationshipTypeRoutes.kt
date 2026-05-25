package fr.dynalgo.infrastructure.web.routes

import fr.dynalgo.domain.model.RelationshipType
import fr.dynalgo.domain.service.RelationshipTypeService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.relationshipTypeRoutes(relationshipTypeService: RelationshipTypeService) {
    route("/relationship-types") {

        get {
            call.respond(relationshipTypeService.findAll())
        }

        post {
            val type = call.receive<RelationshipType>()
            val saved = relationshipTypeService.save(type)
            call.respond(saved)
        }
    }
}