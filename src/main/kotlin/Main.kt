import graph.Builder
import graph.Node

fun main(args: Array<String>) {
    val gb = Builder<String>()
    gb.add(Node(1)).getOrThrow()
    gb.add(Node(2)).getOrThrow()
    gb.link(Node(3), Node(2)).getOrThrow()
    gb.link(Node(4), Node(3)).getOrThrow()
    var g = gb.build().getOrThrow()
    println(g.matrix.toString())
    gb.remove(Node(2)).getOrThrow()
    g = gb.build().getOrThrow()
    println(g.matrix.toString())
}