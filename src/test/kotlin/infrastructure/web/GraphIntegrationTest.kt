package fr.dynalgo.infrastructure.web

import fr.dynalgo.module
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GraphIntegrationTest {

    @Test
    fun healthEndpointReturnsOk() = testApplication {
        application { module() }

        val response = client.get("/health")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("OK", response.bodyAsText())
    }

    @Test
    fun postEntityThenGetGraphReturnsCytoscapeFormat() = testApplication {
        application { module() }

        val createResponse = client.post("/entities") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Integration Test User","type":"Person","color":"#ff0000"}""")
        }
        assertEquals(HttpStatusCode.OK, createResponse.status)

        val graphResponse = client.get("/graph")
        assertEquals(HttpStatusCode.OK, graphResponse.status)

        val body = graphResponse.bodyAsText()
        assertTrue(body.contains("\"elements\""))
        assertTrue(body.contains("Integration Test User"))
        assertTrue(body.contains("\"nodes\""))
    }

    @Test
    fun postRelationshipBetweenEntitiesReturnsCytoscapeEdge() = testApplication {
        application { module() }

        val sourceResponse = client.post("/entities") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Source Node","type":"Person","color":"#111111"}""")
        }
        val targetResponse = client.post("/entities") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Target Node","type":"Person","color":"#222222"}""")
        }

        val sourceId = extractId(sourceResponse.bodyAsText())
        val targetId = extractId(targetResponse.bodyAsText())

        val relationshipResponse = client.post("/relationships") {
            contentType(ContentType.Application.Json)
            setBody(
                """{"sourceId":$sourceId,"targetId":$targetId,"typeName":"TEST_LINK","color":"#abcdef","label":"test link"}"""
            )
        }
        assertEquals(HttpStatusCode.OK, relationshipResponse.status)

        val graphResponse = client.get("/graph")
        val graphBody = graphResponse.bodyAsText()
        assertTrue(graphBody.contains("\"relationshipType\":\"TEST_LINK\""))
        assertTrue(graphBody.contains("\"label\":\"test link\""))
        assertTrue(graphBody.contains("\"source\":\"$sourceId\""))
        assertTrue(graphBody.contains("\"target\":\"$targetId\""))
    }

    @Test
    fun postRelationshipWithMissingEntityReturnsNotFound() = testApplication {
        application { module() }

        val response = client.post("/relationships") {
            contentType(ContentType.Application.Json)
            setBody("""{"sourceId":999999,"targetId":999998,"typeName":"MISSING"}""")
        }
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun postEntityWithBlankNameReturnsBadRequest() = testApplication {
        application { module() }

        val response = client.post("/entities") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"   ","type":"Person"}""")
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun putEntityUpdatesGraph() = testApplication {
        application { module() }

        val createResponse = client.post("/entities") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Before Update","type":"Person","color":"#111111"}""")
        }
        val entityId = extractId(createResponse.bodyAsText())

        val updateResponse = client.put("/entities/$entityId") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"After Update","type":"Company","color":"#222222","description":"Updated"}""")
        }
        assertEquals(HttpStatusCode.OK, updateResponse.status)
        assertTrue(updateResponse.bodyAsText().contains("After Update"))

        val graphResponse = client.get("/graph")
        assertTrue(graphResponse.bodyAsText().contains("After Update"))
    }

    @Test
    fun putRelationshipUpdatesGraph() = testApplication {
        application { module() }

        val sourceId = extractId(client.post("/entities") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Rel Source","type":"Person","color":"#111111"}""")
        }.bodyAsText())

        val targetId = extractId(client.post("/entities") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Rel Target","type":"Person","color":"#222222"}""")
        }.bodyAsText())

        val relId = extractId(client.post("/relationships") {
            contentType(ContentType.Application.Json)
            setBody("""{"sourceId":$sourceId,"targetId":$targetId,"typeName":"OLD_TYPE","label":"old label"}""")
        }.bodyAsText())

        client.post("/relationship-types") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"NEW_TYPE","displayLabel":"new label","color":"#aabbcc","lineStyle":"dashed"}""")
        }

        val updateResponse = client.put("/relationships/$relId") {
            contentType(ContentType.Application.Json)
            setBody(
                """{"sourceId":$sourceId,"targetId":$targetId,"typeName":"NEW_TYPE"}"""
            )
        }
        assertEquals(HttpStatusCode.OK, updateResponse.status)

        val graphBody = client.get("/graph").bodyAsText()
        assertTrue(graphBody.contains("\"relationshipType\":\"NEW_TYPE\""))
        assertTrue(graphBody.contains("\"label\":\"new label\""))
    }

    @Test
    fun deleteGraphClearsAllData() = testApplication {
        application { module() }

        val sourceId = extractId(client.post("/entities") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Reset Source","type":"Person","color":"#111111"}""")
        }.bodyAsText())

        val targetId = extractId(client.post("/entities") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Reset Target","type":"Person","color":"#222222"}""")
        }.bodyAsText())

        client.post("/relationships") {
            contentType(ContentType.Application.Json)
            setBody("""{"sourceId":$sourceId,"targetId":$targetId,"typeName":"RESET_TYPE","label":"reset link"}""")
        }

        val deleteResponse = client.delete("/graph")
        assertEquals(HttpStatusCode.NoContent, deleteResponse.status)

        val graphBody = client.get("/graph").bodyAsText()
        assertTrue(graphBody.contains("\"nodes\":[]") || graphBody.contains("\"nodes\" : []"))
        assertTrue(graphBody.contains("\"edges\":[]") || graphBody.contains("\"edges\" : []"))
        assertEquals("[]", client.get("/relationship-types").bodyAsText())
    }

    private fun extractId(json: String): Long {
        val match = Regex(""""id"\s*:\s*(\d+)""").find(json)
            ?: error("Could not extract id from response: $json")
        return match.groupValues[1].toLong()
    }
}
