package fr.dynalgo.infrastructure.web.routes

import fr.dynalgo.application.mapper.CytoscapeGraphMapper
import fr.dynalgo.domain.service.EntityService
import fr.dynalgo.domain.service.RelationshipService
import fr.dynalgo.domain.service.RelationshipTypeService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.graphRoutes(
    entityService: EntityService,
    relationshipService: RelationshipService,
    relationshipTypeService: RelationshipTypeService
) {
    route("/graph") {
        get {
            val entities = entityService.findAll()
            val relationships = relationshipService.findAll()
            call.respond(CytoscapeGraphMapper.toResponse(entities, relationships))
        }

        delete {
            entityService.findAll().forEach { entityService.deleteById(it.id) }
            relationshipTypeService.findAll().forEach { type ->
                type.id?.let { relationshipTypeService.deleteById(it) }
            }
            call.respond(HttpStatusCode.NoContent)
        }
    }
}
