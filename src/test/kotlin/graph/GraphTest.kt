package graph

import math.Matrix
import math.Rational
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GraphTest {

    @Test
    fun `from returns outgoing neighbors`() {
        val graph = graphFromLinks(
            listOf(1 to 2, 2 to 3),
        )
        val n2 = graph.nodes[1]
        val successors = graph.from(n2).getOrThrow().map { it.id }
        assertEquals(listOf(3), successors)
    }

    @Test
    fun `to returns incoming neighbors`() {
        val graph = graphFromLinks(
            listOf(1 to 2, 2 to 3),
        )
        val n2 = graph.nodes[1]
        val predecessors = graph.to(n2).getOrThrow().map { it.id }
        assertEquals(listOf(1), predecessors)
    }

    @Test
    fun `weight fails for unknown node`() {
        val graph = graphFromLinks(listOf(1 to 2))
        val result = graph.weight(Node(99), graph.nodes[0])
        assertTrue(result.isFailure)
    }

    @Test
    fun `invoke fails when matrix size mismatches nodes`() {
        val nodes = listOf(Node<String>(1), Node(2))
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

    private fun graphFromLinks(edges: List<Pair<Int, Int>>): Graph<String> {
        val builder = Builder<String>()
        for ((from, to) in edges) {
            builder.link(Node(from), Node(to)).getOrThrow()
        }
        return builder.build().getOrThrow()
    }
}
