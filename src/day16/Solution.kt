package day16

import readInput

private enum class Direction(val dRow: Int, val dColumn: Int) {
    DOWN(1, 0),
    UP(-1, 0),
    LEFT(0, -1),
    RIGHT(0, 1)
}

private data class State(
    val row: Int,
    val column: Int,
    val incomingDirection: Direction
) {
    fun change(direction: Direction) = State(row + direction.dRow, column + direction.dColumn, direction)
}

private fun solve(input: List<String>, start: State): Int {
    val rows = input.indices
    val columns = input[0].indices
    val visited = HashSet<State>()

    val dfs = DeepRecursiveFunction<State, Unit> { state ->
        val (row, column, incomingDirection) = state
        if (row in rows && column in columns && visited.add(state)) {
            val symbol = input[row][column]
            when (incomingDirection) {
                Direction.DOWN -> when (symbol) {
                    '.', '|' -> listOf(Direction.DOWN)
                    '\\' -> listOf(Direction.RIGHT)
                    '/' -> listOf(Direction.LEFT)
                    '-' -> listOf(Direction.LEFT, Direction.RIGHT)
                    else -> throw IllegalArgumentException("Unexpected field symbol $symbol")
                }

                Direction.UP -> when (symbol) {
                    '.', '|' -> listOf(Direction.UP)
                    '\\' -> listOf(Direction.LEFT)
                    '/' -> listOf(Direction.RIGHT)
                    '-' -> listOf(Direction.LEFT, Direction.RIGHT)
                    else -> throw IllegalArgumentException("Unexpected field symbol $symbol")
                }

                Direction.LEFT -> when (symbol) {
                    '.', '-' -> listOf(Direction.LEFT)
                    '\\' -> listOf(Direction.UP)
                    '/' -> listOf(Direction.DOWN)
                    '|' -> listOf(Direction.UP, Direction.DOWN)
                    else -> throw IllegalArgumentException("Unexpected field symbol $symbol")
                }

                Direction.RIGHT -> when (symbol) {
                    '.', '-' -> listOf(Direction.RIGHT)
                    '\\' -> listOf(Direction.DOWN)
                    '/' -> listOf(Direction.UP)
                    '|' -> listOf(Direction.UP, Direction.DOWN)
                    else -> throw IllegalArgumentException("Unexpected field symbol $symbol")
                }
            }.forEach { direction ->
                callRecursive(state.change(direction))
            }
        }
    }

    dfs(start)

    return rows.sumOf { row ->
        columns.count { column ->
            Direction.entries.any { direction ->
                State(row, column, direction) in visited
            }
        }
    }
}

private fun part1(input: List<String>): Int =
    solve(input, State(0, 0, Direction.RIGHT))

private fun part2(input: List<String>): Int = (
        input.indices.map { row -> State(row, 0, Direction.RIGHT) } +
                input.indices.map { row -> State(row, input[0].lastIndex, Direction.LEFT) } +
                input[0].indices.map { column -> State(0, column, Direction.DOWN) } +
                input[0].indices.map { column -> State(input.lastIndex, column, Direction.UP) }
        ).maxOf { solve(input, it) }

fun main() {
    val testInput = readInput("day16/test")
    val input = readInput("day16/input")

    check(part1(testInput) == 46)
    println(part1(input))

    check(part2(testInput) == 51)
    println(part2(input))
}
