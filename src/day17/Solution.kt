package day17

import readInput
import java.util.*
import kotlin.math.abs

private fun parseInput(name: String) = readInput(name).map { line -> line.map { symbol -> symbol - '0' } }

private enum class Direction(val dRow: Int, val dColumn: Int) {
    NORTH(-1, 0),
    WEST(0, -1),
    SOUTH(1, 0),
    EAST(0, 1)
}

private fun Direction.rotateLeft(): Direction = when (this) {
    Direction.SOUTH -> Direction.EAST
    Direction.NORTH -> Direction.WEST
    Direction.WEST -> Direction.SOUTH
    Direction.EAST -> Direction.NORTH
}

private fun Direction.rotateRight(): Direction = when (this) {
    Direction.SOUTH -> Direction.WEST
    Direction.NORTH -> Direction.EAST
    Direction.WEST -> Direction.NORTH
    Direction.EAST -> Direction.SOUTH
}

private fun aStar(
    input: List<List<Int>>,
    start: Pair<Int, Int>,
    finish: Pair<Int, Int>,
    a: Int,
    b: Int
): Int {
    val rows = input.indices
    val columns = input[0].indices

    data class State(
        val row: Int,
        val column: Int,
        val headingDirection: Direction,
        val numberOfForwardMoves: Int,
        val g: Int
    ) {
        val h: Int
            get() = abs(finish.first - row) + abs(finish.second - column)

        val f: Int
            get() = g + h
    }

    val queue = PriorityQueue<State>(compareBy { it.f })
    val visited = HashSet<List<Int>>()

    queue.offer(State(start.first, start.second, Direction.EAST, 0, 0))

    fun makeMove(from: State, direction: Direction) {
        val nextRow = from.row + direction.dRow
        val nextColumn = from.column + direction.dColumn
        if (nextRow in rows && nextColumn in columns) {
            val state = State(
                row = nextRow,
                column = nextColumn,
                headingDirection = direction,
                numberOfForwardMoves = if (from.headingDirection == direction) from.numberOfForwardMoves + 1 else 1,
                g = from.g + input[nextRow][nextColumn]
            )
            queue.offer(state)
        }
    }

    while (queue.isNotEmpty()) {
        val next = queue.remove()

        if (next.row == finish.first && next.column == finish.second && next.numberOfForwardMoves >= a) return next.g

        if (visited.add(listOf(next.row, next.column, next.headingDirection.ordinal, next.numberOfForwardMoves))) {
            if (next.numberOfForwardMoves >= a) {
                makeMove(next, next.headingDirection.rotateLeft())
                makeMove(next, next.headingDirection.rotateRight())
            }
            if (next.numberOfForwardMoves < b) {
                makeMove(next, next.headingDirection)
            }
        }
    }

    throw IllegalArgumentException("Cannot find path")
}

private fun part1(input: List<List<Int>>): Int =
    aStar(input, 0 to 0, input.lastIndex to input[0].lastIndex, 0, 3)

private fun part2(input: List<List<Int>>): Int =
    aStar(input, 0 to 0, input.lastIndex to input[0].lastIndex, 4, 10)

fun main() {
    val testInput = parseInput("day17/test")
    val input = parseInput("day17/input")

    check(part1(testInput) == 102)
    println(part1(input))

    val testInput2 = parseInput("day17/test_2")
    check(part2(testInput) == 94)
    check(part2(testInput2) == 71)
    println(part2(input))
}
