package day13

import readInput
import splitBy

private fun parseInput(name: String) = readInput(name)
    .splitBy("")
    .map { field ->
        field.map { line ->
            BooleanArray(line.length) { line[it] == '#' }
        }
    }

private fun findVerticalSymmetries(lines: List<BooleanArray>) = (1 until lines.size).filter { symmetryAtRow ->
    var i = symmetryAtRow - 1
    var j = symmetryAtRow
    while (i in lines.indices && j in lines.indices && lines[i].contentEquals(lines[j])) {
        i--
        j++
    }
    i !in lines.indices || j !in lines.indices
}

private fun List<BooleanArray>.transpose(): List<BooleanArray> =
    (0 until (this.minOfOrNull { it.size } ?: 0))
        .map { index -> BooleanArray(this.size) { this[it][index] } }

private fun part1(input: List<List<BooleanArray>>) = input.sumOf {
    findVerticalSymmetries(it.transpose()).singleOrNull() ?: (findVerticalSymmetries(it).single() * 100)
}

private fun solve(field: List<BooleanArray>): List<Int> {
    val originalSymmetries = findVerticalSymmetries(field)

    return field.indices.flatMap { row ->
        field[0].indices.flatMap { column ->
            val prev = field[row][column]
            field[row][column] = !prev

            val result = findVerticalSymmetries(field)

            field[row][column] = prev

            result
        }
    }.filter { it !in originalSymmetries }.distinct()
}

private fun part2(input: List<List<BooleanArray>>): Int = input.sumOf {
    solve(it.transpose()).singleOrNull() ?: (solve(it).single() * 100)
}

fun main() {
    val testInput = parseInput("day13/test")
    val input = parseInput("day13/input")

    check(part1(testInput) == 405)
    println(part1(input))

    check(part2(testInput) == 400)
    println(part2(input))
}
