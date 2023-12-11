package day11

import readInput
import kotlin.math.max
import kotlin.math.min

private val IntRange.length: Int
    get() = (last - first) / step

private fun solve(input: List<String>, expandingRate: Long): Long {
    val rows = input.indices
    val columns = input[0].indices

    val emptyRows = rows.filter { row ->
        columns.all { column -> input[row][column] == '.' }
    }

    val emptyColumns = columns.filter { column ->
        rows.all { row -> input[row][column] == '.' }
    }

    val stars = rows
        .flatMap { row -> columns.map { column -> row to column } }
        .filter { (row, column) -> input[row][column] == '#' }

    val starsPairs = stars.withIndex().flatMap { (index, value) ->
        stars.drop(index + 1).map { value to it }
    }

    fun range(a: Int, b: Int) = min(a, b)..max(a, b)

    return starsPairs.sumOf { (star1, star2) ->
        val spannedRows = range(star1.first, star2.first)
        val spannedColumns = range(star1.second, star2.second)

        spannedRows.length +
                spannedColumns.length +
                emptyRows.count { it in spannedRows } * expandingRate +
                emptyColumns.count { it in spannedColumns } * expandingRate
    }
}

fun main() {
    val testInput = readInput("day11/test")
    val input = readInput("day11/input")

    check(solve(testInput, 1) == 374L)
    check(solve(testInput, 10 - 1) == 1030L)
    check(solve(testInput, 100 - 1) == 8410L)

    println(solve(input, 1))
    println(solve(input, 1000000 - 1))
}
