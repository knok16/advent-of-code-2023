package day21

import readInput

private typealias Coordinates = Pair<Int, Int>

private class Field(input: List<String>) {
    val data = input.map { it.toCharArray() }
    val rows = data.size
    val columns = data[0].size

    val start: Coordinates = data.withIndex().firstNotNullOf { (index, value) ->
        value.indexOf('S').takeIf { it >= 0 }?.let { index to it }
    }

    operator fun get(c: Coordinates): Char =
        data[c.first][c.second]

    operator fun contains(c: Coordinates): Boolean =
        c.first in 0 until rows && c.second in 0 until columns

    fun neighbours(c: Coordinates): List<Coordinates> = listOf(
        c.first - 1 to c.second,
        c.first + 1 to c.second,
        c.first to c.second - 1,
        c.first to c.second + 1
    ).filter { it in this && this[it] != '#' }
}

private fun parseInput(name: String) = Field(readInput(name))

private fun countReachable(field: Field, start: Coordinates, steps: Int): Int =
    (1..steps).fold(setOf(start)) { acc, _ ->
        val result = acc.flatMap { field.neighbours(it) }.toSet()

        result
    }.size

private fun part1(field: Field, steps: Int): Int =
    countReachable(field, field.start, steps)

private fun part2(field: Field, steps: Int): Long {
    val someBigNumber = 4
    check(field.rows == field.columns)
    val fieldSize = field.rows
    check(fieldSize == 131)
    val last = fieldSize - 1
    check(field.rows % 2 == 1)
    val middle = last / 2

    check(steps == 26501365)
    check((0..last).none { field[it to 0] == '#' })
    check((0..last).none { field[it to last] == '#' })
    check((0..last).none { field[0 to it] == '#' })
    check((0..last).none { field[last to it] == '#' })
    check((0..last).none { field[it to field.start.second] == '#' })
    check((0..last).none { field[field.start.first to it] == '#' })
    check(steps > fieldSize * someBigNumber)
    check(field.start.first == middle)
    check(field.start.second == middle)

    check((steps - middle) % fieldSize == 0)

    val n = ((steps - middle) / fieldSize).toLong()

    println(n)

    val a = (0 until fieldSize).flatMap { row -> (0 until fieldSize).map { column -> row to column } }
        .filter { field[it] != '#' }
        .partition { (row, column) -> (row + column) % 2 == 0 }

    return (4L * 101150 * 101151 - 101150) * a.second.size +
            (4L * 101149 * 101150) * a.first.size +
            (n - 1) * (
            countReachable(field, 0 to 0, 2 * fieldSize - 1 - middle) +
                    countReachable(field, 0 to last, 2 * fieldSize - 1 - middle) +
                    countReachable(field, last to 0, 2 * fieldSize - 1 - middle) +
                    countReachable(field, last to last, 2 * fieldSize - 1 - middle)
            ) +
            countReachable(field, 0 to middle, fieldSize - 1) + // bottom vertex
            countReachable(field, last to middle, fieldSize - 1) + // top vertex
            countReachable(field, middle to 0, fieldSize - 1) + // right vertex
            countReachable(field, middle to last, fieldSize - 1) + // left vertex
            a.second.size // middle square

}

fun main() {
    val testInput = parseInput("day21/test")
    val input = parseInput("day21/input")

//    (1..100).forEach { step ->
//        val onInput = input
//        val expected = part1(onInput, step)
//        val actual = part1InputAware(onInput, step)
//        if (expected != actual)
//            println("Step $step, expected: $expected, actual: $actual")
////        check(expected == actual)
//    }

    check(part1(testInput, 0) == 1)
    check(part1(testInput, 1) == 2)
    check(part1(testInput, 2) == 4)
    check(part1(testInput, 3) == 6)
    check(part1(testInput, 6) == 16)
    check(part1(input, 64) == 3689)

//    check(part2(testInput) == 167409079868000)
    println(part2(input, 26501365))

    // Low 609870977796218
    // Wrong 610323418213426
    // Wrong 610323418213427
    // High 610323414673176
    // High 610443929024018
}
