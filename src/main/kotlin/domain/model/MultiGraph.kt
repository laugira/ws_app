package fr.dynalgo.domain.model

import common.graph.Graph
import common.graph.Node
import common.math.Matrix
import common.math.Rational

/**
 * Represents the complete multigraph of the application.
 * It contains one Graph<Entity> per RelationshipType.
 */
data class MultiGraph(
    val entities: List<Entity> = emptyList(),

    /**
     * Key   = RelationshipType
     * Value = The technical graph containing all links of that type
     */
    val graphs: Map<RelationshipType, Graph<Entity>> = emptyMap()
) {

    /**
     * Returns the graph associated with a specific relationship type.
     */
    fun getGraph(type: RelationshipType): Graph<Entity>? = graphs[type]

    /**
     * Returns all relationship types present in the multigraph.
     */
    fun getRelationshipTypes(): Set<RelationshipType> = graphs.keys

    /**
     * Adds a new relationship type with an empty graph.
     */
    fun addRelationshipType(type: RelationshipType): MultiGraph {
        val emptyMatrix = Matrix(emptyList<List<Rational?>>()).getOrThrow()
        val emptyGraph = Graph(emptyList<Node<Entity>>(), emptyMatrix).getOrThrow()

        return copy(
            graphs = graphs + (type to emptyGraph)
        )
    }
}