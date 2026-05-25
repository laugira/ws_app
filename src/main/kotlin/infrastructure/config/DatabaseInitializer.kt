package fr.dynalgo.infrastructure.config

import fr.dynalgo.infrastructure.persistence.entity.Entities
import fr.dynalgo.infrastructure.persistence.entity.RelationshipTypes
import fr.dynalgo.infrastructure.persistence.entity.Relationships
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

object DatabaseInitializer {

    private val logger = LoggerFactory.getLogger(DatabaseInitializer::class.java)

    fun initSchema() {
        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                Entities,
                RelationshipTypes,
                Relationships
            )
        }
        logger.info("✅ Database schema initialized successfully")
    }
}
