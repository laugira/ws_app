package graph

import common.graph.Builder
import common.graph.Graph
import common.graph.Node
import common.math.Matrix
import common.math.Rational
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GraphTest {

    @Test
    fun `from returns outgoing neighbors`() {
        val graph = graphFromLinks(
            listOf(1L to 2L, 2L to 3L),
        )
        val n2 = graph.nodes[1]
        val successors = graph.from(n2).getOrThrow().map { it.id }
        assertEquals(listOf(3L), successors)
    }

    @Test
    fun `to returns incoming neighbors`() {
        val graph = graphFromLinks(
            listOf(1L to 2L, 2L to 3L),
        )
        val n2 = graph.nodes[1]
        val predecessors = graph.to(n2).getOrThrow().map { it.id }
        assertEquals(listOf(1L), predecessors)
    }

    @Test
    fun `weight fails for unknown node`() {
        val graph = graphFromLinks(listOf(1L to 2L))
        val result = graph.weight(Node(99L), graph.nodes[0])
        assertTrue(result.isFailure)
    }

    @Test
    fun `invoke fails when matrix size mismatches nodes`() {
        val nodes = listOf(Node<String>(1L), Node(2L))
        val matrix = Matrix(
            listOf(
                listOf(Rational(0), Rational(0)),
                listOf(Rational(0), Rational(0)),
                listOf(Rational(0), Rational(0)),
            ),
        ).getOrThrow()
        val result = Graph(nodes, matrix)
        assertTrue(result.isFailure)
    }

    private fun graphFromLinks(edges: List<Pair<Long, Long>>): Graph<String> {
        val builder = Builder<String>()
        for ((from, to) in edges) {
            builder.link(Node(from), Node(to)).getOrThrow()
        }
        return builder.build().getOrThrow()
    }
}
