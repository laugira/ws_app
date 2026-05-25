package fr.dynalgo

import fr.dynalgo.domain.exception.BadRequestException
import fr.dynalgo.domain.exception.NotFoundException
import fr.dynalgo.domain.repository.EntityRepository
import fr.dynalgo.domain.repository.RelationshipRepository
import fr.dynalgo.domain.repository.RelationshipTypeRepository
import fr.dynalgo.domain.service.EntityService
import fr.dynalgo.domain.service.RelationshipTypeService
import fr.dynalgo.domain.service.RelationshipService
import fr.dynalgo.infrastructure.config.DatabaseConfig
import fr.dynalgo.infrastructure.config.DatabaseInitializer
import fr.dynalgo.infrastructure.persistence.entity.repository.EntityRepositoryImpl
import fr.dynalgo.infrastructure.persistence.entity.repository.RelationshipRepositoryImpl
import fr.dynalgo.infrastructure.persistence.entity.repository.RelationshipTypeRepositoryImpl
import fr.dynalgo.infrastructure.web.routes.entityRoutes
import fr.dynalgo.infrastructure.web.routes.graphRoutes
import fr.dynalgo.infrastructure.web.routes.relationshipRoutes
import fr.dynalgo.infrastructure.web.routes.relationshipTypeRoutes
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.http.content.staticResources
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.serialization.jackson.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        module()
    }.start(wait = true)
}

/**
 * Point d'entrée et configuration principale de MultiGraph Lab (Ktor).
 */
fun Application.module() {

    DatabaseConfig.init()
    DatabaseInitializer.initSchema()

    install(CallLogging)
    install(StatusPages) {
        exception<NotFoundException> { call, cause ->
            call.respondText(
                text = cause.message ?: "Not found",
                status = HttpStatusCode.NotFound
            )
        }
        exception<BadRequestException> { call, cause ->
            call.respondText(
                text = cause.message ?: "Bad request",
                status = HttpStatusCode.BadRequest
            )
        }
        exception<IllegalArgumentException> { call, cause ->
            call.respondText(
                text = cause.message ?: "Invalid request",
                status = HttpStatusCode.BadRequest
            )
        }
        exception<Throwable> { call, cause ->
            call.respondText(
                text = "Server error: ${cause.message}",
                status = HttpStatusCode.InternalServerError
            )
        }
    }
    install(ContentNegotiation) {
        jackson { }
    }

    val entityRepository: EntityRepository = EntityRepositoryImpl()
    val relationshipTypeRepository: RelationshipTypeRepository = RelationshipTypeRepositoryImpl()
    val relationshipRepository: RelationshipRepository = RelationshipRepositoryImpl()

    val entityService = EntityService(entityRepository)
    val relationshipTypeService = RelationshipTypeService(relationshipTypeRepository)
    val relationshipService = RelationshipService(
        relationshipRepository,
        entityRepository,
        relationshipTypeRepository
    )

    routing {
        get("/health") {
            call.respondText("OK")
        }

        get("/favicon.ico") {
            call.respondRedirect("/favicon.svg")
        }

        entityRoutes(entityService)
        relationshipRoutes(relationshipService)
        relationshipTypeRoutes(relationshipTypeService)
        graphRoutes(entityService, relationshipService, relationshipTypeService)

        staticResources("/", "static", index = "index.html")
    }
}
