package day03

import readInput

fun main() {
    data class Number(
        val row: Int,
        val fromColumn: Int, // including
        var toColumn: Int, // excluding
        var value: Int
    ) {
        constructor(row: Int, index: Int, digit: Char) : this(row, index, index + 1, digit - '0')

        fun extend(digit: Char) {
            value = value * 10 + (digit - '0')
            toColumn++
        }

        fun cellsAround(): List<Pair<Int, Int>> =
            listOf(row to fromColumn - 1, row to toColumn) +
                    ((fromColumn - 1)..toColumn).map { row - 1 to it } +
                    ((fromColumn - 1)..toColumn).map { row + 1 to it }
    }

    fun List<String>.parseNumbers(): List<Number> = flatMapIndexed { row, line ->
        line.withIndex()
            .filter { it.value.isDigit() }
            .fold(emptyList()) { acc, (index, digit) ->
                if (acc.isNotEmpty() && acc.last().toColumn == index) {
                    acc.last().extend(digit)
                    acc
                } else {
                    acc + Number(row, index, digit)
                }
            }
    }

    fun part1(input: List<String>): Int = input
        .parseNumbers()
        .filter { number ->
            number.cellsAround().any { (row, column) ->
                row in input.indices && column in input[row].indices && input[row][column] != '.'
            }
        }.sumOf { it.value }

    fun part2(input: List<String>): Int = input
        .parseNumbers()
        .flatMap { number ->
            number.cellsAround().filter { (row, column) ->
                row in input.indices && column in input[row].indices && input[row][column] == '*'
            }.map { it to number }
        }.groupBy({ it.first }, { it.second.value })
        .filter { (_, numbers) -> numbers.size == 2 }
        .values
        .sumOf { numbers -> numbers[0] * numbers[1] }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day03/test")
    check(part1(testInput) == 4361)
    check(part2(testInput) == 467835)

    val input = readInput("day03/input")

    println(part1(input))
    println(part2(input))
}
