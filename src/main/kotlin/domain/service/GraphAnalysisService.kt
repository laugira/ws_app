package fr.dynalgo.domain.service

import fr.dynalgo.domain.model.Entity
import fr.dynalgo.domain.model.MultiGraph
import fr.dynalgo.domain.model.Relationship
import fr.dynalgo.domain.model.RelationshipType
import common.graph.Builder

/**
 * Service responsible for all graph analysis and multigraph operations.
 * This is where we use your custom graph library (Node, Link, Graph, Builder).
 */
class GraphAnalysisService {

    /**
     * Builds a MultiGraph from a list of entities and relationships.
     */
    fun buildMultiGraph(entities: List<Entity>, relationships: List<Relationship>): MultiGraph {
        val builderMap = mutableMapOf<String, Builder<Entity>>()
        val typeByName = mutableMapOf<String, RelationshipType>()

        relationships.forEach { rel ->
            typeByName.putIfAbsent(rel.type.name, rel.type)
            val builder = builderMap.getOrPut(rel.type.name) { Builder() }
            val sourceNode = rel.source.toNode()
            val targetNode = rel.target.toNode()
            builder.add(sourceNode).getOrThrow()
            builder.add(targetNode).getOrThrow()
            builder.link(sourceNode, targetNode, rel.weight).getOrThrow()
        }

        val graphs = builderMap.mapKeys { (name, _) ->
            typeByName.getValue(name)
        }.mapValues { (_, builder) ->
            builder.build().getOrThrow()
        }

        return MultiGraph(
            entities = entities,
            graphs = graphs
        )
    }
}
