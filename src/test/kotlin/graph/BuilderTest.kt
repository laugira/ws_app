package graph

import common.graph.Builder
import common.graph.Node
import common.math.Rational
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

class BuilderTest {

    @Test
    fun `add succeeds for new node`() {
        val builder = Builder<String>()
        val result = builder.add(Node(1, data = "a"))
        assertTrue(result.isSuccess)
        assertEquals(1L, result.getOrThrow().id)
    }

    @Test
    fun `add fails when node id already exists`() {
        val builder = Builder<String>()
        builder.add(Node(1L)).getOrThrow()
        val result = builder.add(Node(1, data = "other"))
        assertTrue(result.isFailure)
    }

    @Test
    fun `link reuses canonical node instance from set`() {
        val builder = Builder<String>()
        val node2 = builder.add(Node(2, data = "two")).getOrThrow()
        val link = builder.link(Node(3L), Node(2, data = "ignored")).getOrThrow()
        assertSame(node2, link.nTo)
        assertNotSame(link.nFrom, link.nTo)
    }

    @Test
    fun `link fails when edge already exists`() {
        val builder = Builder<String>()
        builder.link(Node(1L), Node(2L)).getOrThrow()
        val result = builder.link(Node(1L), Node(2L))
        assertTrue(result.isFailure)
    }

    @Test
    fun `build sorts nodes by id`() {
        val builder = Builder<String>()
        builder.add(Node(3L)).getOrThrow()
        builder.add(Node(1L)).getOrThrow()
        builder.add(Node(2L)).getOrThrow()
        val graph = builder.build().getOrThrow()
        assertEquals(listOf(1L, 2L, 3L), graph.nodes.map { it.id })
    }

    @Test
    fun `build matrix reflects links`() {
        val builder = Builder<String>()
        builder.link(Node(1L), Node(2L)).getOrThrow()
        builder.link(Node(2L), Node(3L)).getOrThrow()
        val graph = builder.build().getOrThrow()
        val n1 = graph.nodes[0]
        val n2 = graph.nodes[1]
        val n3 = graph.nodes[2]
        assertEquals(Rational(1), graph.weight(n1, n2).getOrThrow())
        assertEquals(Rational(1), graph.weight(n2, n3).getOrThrow())
        assertEquals(Rational(0), graph.weight(n1, n3).getOrThrow())
    }

    @Test
    fun `remove deletes node and incident links`() {
        val builder = Builder<String>()
        builder.link(Node(1L), Node(2L)).getOrThrow()
        builder.link(Node(2L), Node(3L)).getOrThrow()
        builder.remove(Node(2L)).getOrThrow()
        val graph = builder.build().getOrThrow()
        assertEquals(listOf(1L, 3L), graph.nodes.map { it.id })
        val n1 = graph.nodes[0]
        val n3 = graph.nodes[1]
        assertEquals(Rational(0), graph.weight(n1, n3).getOrThrow())
    }

    @Test
    fun `remove fails when node does not exist`() {
        val builder = Builder<String>()
        val result = builder.remove(Node(99L))
        assertTrue(result.isFailure)
    }

    @Test
    fun `link auto-adds missing endpoints`() {
        val builder = Builder<String>()
        builder.link(Node(1L), Node(2L)).getOrThrow()
        val graph = builder.build().getOrThrow()
        assertEquals(listOf(1L, 2L), graph.nodes.map { it.id })
    }

    @Test
    fun `link with custom weight is stored in matrix`() {
        val builder = Builder<String>()
        builder.link(Node(1L), Node(2L), weight = Rational(3, 2).getOrThrow()).getOrThrow()
        val graph = builder.build().getOrThrow()
        assertEquals(Rational(3, 2).getOrThrow(), graph.weight(graph.nodes[0], graph.nodes[1]).getOrThrow())
    }

    @Test
    fun `link with oppositeToo creates reverse edge`() {
        val builder = Builder<String>()
        builder.link(Node(1L), Node(2L), oppositeToo = true).getOrThrow()
        val graph = builder.build().getOrThrow()
        val n1 = graph.nodes[0]
        val n2 = graph.nodes[1]
        assertEquals(Rational(1), graph.weight(n1, n2).getOrThrow())
        assertEquals(Rational(1), graph.weight(n2, n1).getOrThrow())
    }

    @Test
    fun `link with oppositeToo fails when reverse edge already exists`() {
        val builder = Builder<String>()
        builder.link(Node(2L), Node(1L)).getOrThrow()
        val result = builder.link(Node(1L), Node(2L), oppositeToo = true)
        assertTrue(result.isFailure)
    }

    @Test
    fun `unlink removes edge from graph`() {
        val builder = Builder<String>()
        builder.link(Node(1L), Node(2L)).getOrThrow()
        builder.unlink(Node(1L), Node(2L)).getOrThrow()
        val graph = builder.build().getOrThrow()
        assertEquals(Rational(0), graph.weight(graph.nodes[0], graph.nodes[1]).getOrThrow())
    }

    @Test
    fun `unlink with oppositeToo removes both directions`() {
        val builder = Builder<String>()
        builder.link(Node(1L), Node(2L), oppositeToo = true).getOrThrow()
        builder.unlink(Node(1L), Node(2L), oppositeToo = true).getOrThrow()
        val graph = builder.build().getOrThrow()
        val n1 = graph.nodes[0]
        val n2 = graph.nodes[1]
        assertEquals(Rational(0), graph.weight(n1, n2).getOrThrow())
        assertEquals(Rational(0), graph.weight(n2, n1).getOrThrow())
    }

    @Test
    fun `unlink fails when edge does not exist`() {
        val builder = Builder<String>()
        builder.add(Node(1L)).getOrThrow()
        builder.add(Node(2L)).getOrThrow()
        val result = builder.unlink(Node(1L), Node(2L))
        assertTrue(result.isFailure)
    }

    @Test
    fun `unlink fails when node does not exist`() {
        val builder = Builder<String>()
        val result = builder.unlink(Node(1L), Node(2L))
        assertTrue(result.isFailure)
    }

    @Test
    fun `build succeeds for empty builder`() {
        val graph = Builder<String>().build().getOrThrow()
        assertEquals(0, graph.nodes.size)
        assertEquals(0, graph.matrix.rowCount())
    }
}
