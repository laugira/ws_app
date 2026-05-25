package fr.dynalgo.application.mapper

import fr.dynalgo.application.dto.*
import fr.dynalgo.domain.model.Entity
import fr.dynalgo.domain.model.Relationship

object CytoscapeGraphMapper {

    private const val DEFAULT_NODE_COLOR = "#64748b"
    private const val DEFAULT_EDGE_COLOR = "#60a5fa"

    fun toResponse(entities: List<Entity>, relationships: List<Relationship>): CytoscapeGraphResponse {
        return CytoscapeGraphResponse(
            elements = CytoscapeElements(
                nodes = entities.map { toNode(it) },
                edges = relationships.mapNotNull { toEdge(it) }
            )
        )
    }

    private fun toNode(entity: Entity): CytoscapeNode = CytoscapeNode(
        data = CytoscapeNodeData(
            id = entity.id.toString(),
            label = entity.name,
            type = entity.type,
            color = entity.color ?: DEFAULT_NODE_COLOR,
            description = entity.description
        )
    )

    private fun toEdge(relationship: Relationship): CytoscapeEdge? {
        val id = relationship.id ?: return null

        return CytoscapeEdge(
            data = CytoscapeEdgeData(
                id = "e$id",
                source = relationship.source.id.toString(),
                target = relationship.target.id.toString(),
                label = relationship.type.displayLabel ?: relationship.type.name,
                color = relationship.type.color.ifBlank { DEFAULT_EDGE_COLOR },
                width = 2,
                relationshipType = relationship.type.name,
                lineStyle = relationship.type.lineStyle
            )
        )
    }
}
