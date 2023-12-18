package day18

import readInput

// R 6 (#70c710)
private val regex = Regex("""([UDLR]) (\d+) \(#([0-9a-f]{5})([0-3])\)""")

private enum class Direction(val dRow: Int, val dColumn: Int) {
    RIGHT(0, 1),
    DOWN(1, 0),
    LEFT(0, -1),
    UP(-1, 0);

    companion object {
        fun fromChar(char: Char) = when (char) {
            'U' -> UP
            'D' -> DOWN
            'L' -> LEFT
            'R' -> RIGHT
            else -> throw IllegalArgumentException("Unexpected direction '$char'")
        }

        // 0 means R, 1 means D, 2 means L, and 3 means U
        fun fromNumber(int: Int) = Direction.entries[int]
    }
}

private data class Instruction(
    val direction: Direction,
    val distance: Int
) {
    init {
        check(distance > 0)
    }
}

private fun parseInput1(input: List<String>) = input.map { line ->
    val (_, direction, distance, _, _) = regex.matchEntire(line)!!.groupValues

    Instruction(
        Direction.fromChar(direction[0]),
        distance.toInt()
    )
}

private fun parseInput2(input: List<String>) = input.map { line ->
    val (_, _, _, distance, direction) = regex.matchEntire(line)!!.groupValues

    Instruction(
        Direction.fromNumber(direction.toInt()),
        distance.toInt(16)
    )
}

private data class Pivot(
    val row: Int,
    val column: Int,
    val type: Char
)

private fun getJoint(d1: Direction, d2: Direction) = when {
    d1 == Direction.UP && d2 == Direction.LEFT ||
            d1 == Direction.RIGHT && d2 == Direction.DOWN -> '7'

    d1 == Direction.UP && d2 == Direction.RIGHT ||
            d1 == Direction.LEFT && d2 == Direction.DOWN -> 'F'

    d1 == Direction.DOWN && d2 == Direction.LEFT ||
            d1 == Direction.RIGHT && d2 == Direction.UP -> 'J'

    d1 == Direction.DOWN && d2 == Direction.RIGHT ||
            d1 == Direction.LEFT && d2 == Direction.UP -> 'L'

    else -> throw IllegalArgumentException("Unknown joint ($d1, $d2)")
}

private fun List<Instruction>.trace(): List<Pivot> = zipWithNext().runningFold(
    Pivot(
        row = 0,
        column = 0,
        type = getJoint(last().direction, first().direction)
    )
) { pivot, (instruction, nextInstruction) ->
    val (direction, distance) = instruction
    Pivot(
        row = pivot.row + direction.dRow * distance,
        column = pivot.column + direction.dColumn * distance,
        type = getJoint(direction, nextInstruction.direction)
    )
}

//'7', 'F', 'J', 'L', '|'
private val possibleSegmentOptions = setOf(
    'F' to 'J',
    'F' to '7',

    'L' to 'J',
    'L' to '7',

    '7' to 'F',
    '7' to 'L',
    '7' to '|',

    'J' to 'F',
    'J' to 'L',
    'J' to '|',

    '|' to 'F',
    '|' to 'L',
    '|' to '|',
)

private data class Event(
    val column: Int,
    val type: Char
)

private fun solveForRow(events: List<Event>): Long = events
    .sortedBy { it.column }
    .zipWithNext()
    .runningFold(false to 0) { (inside, _), (from, to) ->
        val (fromColumn, type1) = from
        val (toColumn, type2) = to

        if (type1 to type2 !in possibleSegmentOptions) throw IllegalStateException("Unexpected segment ($type1, $type2)")

        val newInside =
            inside xor (type1 == '|' || type1 == 'F' && type2 == 'J' || type1 == 'L' && type2 == '7')

        newInside to if (newInside || type1 == 'F' || type1 == 'L') toColumn - fromColumn - 1 else 0
    }.sumOf { it.second } + events.size.toLong()

private fun solve(instructions: List<Instruction>): Long {
    var result = 0L
    var rollingEvents = emptySet<Int>()
    var prevRowWithEvents: Int? = null

    instructions.trace()
        .groupBy({ it.row }, { Event(it.column, it.type) })
        .toList()
        .sortedBy { it.first }
        .forEach { (row, pivotsInRow) ->
            check(pivotsInRow.filter { it.type == 'F' || it.type == '7' }.all { it.column !in rollingEvents })
            check(pivotsInRow.filter { it.type == 'L' || it.type == 'J' }.all { it.column in rollingEvents })

            val resultForRowsWithoutEvents = prevRowWithEvents
                ?.let { row - it - 1 }
                ?.takeIf { rowsWithoutEvents -> rowsWithoutEvents > 0 }
                ?.let { rowsWithoutEvents ->
                    rowsWithoutEvents * solveForRow(rollingEvents.map { Event(it, '|') })
                } ?: 0

            val events = pivotsInRow + rollingEvents
                .filter { column -> pivotsInRow.none { it.column == column } }
                .map { Event(it, '|') }

            result += solveForRow(events) + resultForRowsWithoutEvents
            rollingEvents = events.filter { it.type in setOf('|', 'F', '7') }.map { it.column }.toSet()
            prevRowWithEvents = row
        }

    check(rollingEvents.isEmpty())

    return result
}

private fun part1(input: List<String>) =
    solve(parseInput1(input))

private fun part2(input: List<String>) =
    solve(parseInput2(input))

fun main() {
    val testInput = readInput("day18/test")
    val input = readInput("day18/input")

    check(part1(testInput) == 62L)
    check(part1(input) == 49897L)

    check(part2(testInput) == 952408144115)
    check(part2(input) == 194033958221830)
}
