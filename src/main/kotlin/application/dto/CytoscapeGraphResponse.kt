package fr.dynalgo.application.dto

data class CytoscapeGraphResponse(
    val elements: CytoscapeElements
)

data class CytoscapeElements(
    val nodes: List<CytoscapeNode>,
    val edges: List<CytoscapeEdge>
)

data class CytoscapeNode(
    val data: CytoscapeNodeData
)

data class CytoscapeNodeData(
    val id: String,
    val label: String,
    val type: String,
    val color: String,
    val description: String? = null
)

data class CytoscapeEdge(
    val data: CytoscapeEdgeData
)

data class CytoscapeEdgeData(
    val id: String,
    val source: String,
    val target: String,
    val label: String,
    val color: String,
    val width: Int = 2,
    val relationshipType: String,
    val lineStyle: String = "solid"
)
