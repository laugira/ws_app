package fr.dynalgo.infrastructure.config

import org.jetbrains.exposed.sql.Database
import org.slf4j.LoggerFactory

object DatabaseConfig {

    private val logger = LoggerFactory.getLogger(DatabaseConfig::class.java)

    /**
     * Initializes an embedded H2 database (for development)
     * Later we will switch to PostgreSQL
     */
    fun init() {
        Database.connect(
            url = "jdbc:h2:mem:ws_app;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
            driver = "org.h2.Driver",
            user = "sa",
            password = ""
        )

        logger.info("✅ Connected to embedded H2 database (in-memory)")
    }
}