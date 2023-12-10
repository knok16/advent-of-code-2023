package day10

import readInput

private typealias Coordinates = Pair<Int, Int>

private fun Coordinates.northNeighbour() = first - 1 to second
private fun Coordinates.southNeighbour() = first + 1 to second
private fun Coordinates.westNeighbour() = first to second - 1
private fun Coordinates.eastNeighbour() = first to second + 1

private class Field(input: List<String>) {
    val data = input.map { it.toCharArray() }
    val rows = data.size
    val columns = data[0].size

    val start: Coordinates = data.withIndex().firstNotNullOf { (index, value) ->
        value.indexOf('S').takeIf { it >= 0 }?.let { index to it }
    }

    init {
        val pipesSymbols = setOf('|', '-', 'L', 'J', '7', 'F')
        val desired =
            listOf(start.northNeighbour(), start.southNeighbour(), start.westNeighbour(), start.eastNeighbour())
                .filter { it in this }
                .filter { this[it] in pipesSymbols }
                .filter { start in neighbours(it) }

        for (startSymbolReplacement in pipesSymbols) {
            data[start.first][start.second] = startSymbolReplacement
            if (neighbours(start) == desired) break
        }
    }

    operator fun get(c: Coordinates): Char =
        data[c.first][c.second]

    operator fun contains(c: Coordinates): Boolean =
        c.first in 0 until rows && c.second in 0 until columns

    fun neighbours(c: Coordinates): List<Coordinates> = when (val symbol = this[c]) {
        '|' -> listOf(c.northNeighbour(), c.southNeighbour())
        '-' -> listOf(c.eastNeighbour(), c.westNeighbour())
        'L' -> listOf(c.northNeighbour(), c.eastNeighbour())
        'J' -> listOf(c.northNeighbour(), c.westNeighbour())
        '7' -> listOf(c.southNeighbour(), c.westNeighbour())
        'F' -> listOf(c.southNeighbour(), c.eastNeighbour())
        else -> throw IllegalStateException("Unexpected symbol '$symbol'")
    }.filter { it in this }
}

private fun parseInput(name: String) = Field(readInput(name))

private fun Field.loop() =
    sequenceOf(
        sequenceOf(start),
        generateSequence(start to neighbours(start).first()) { (from, v) ->
            v to neighbours(v).first { it != from }
        }.map { (_, v) ->
            v
        }.takeWhile {
            it != start
        }
    ).flatten()

private fun part1(field: Field) =
    field.loop().count().let { length -> length / 2 }

private fun part2(field: Field): Int {
    val columnsWithPipesByRow = field.loop().groupBy({ it.first }, { it.second })

    return field.data.mapIndexed { row, line ->
        val columnsWithPipes = columnsWithPipesByRow[row]?.toSet() ?: emptySet()

        var result = 0
        var i = 0
        var isInside = false

        while (i < line.size) {
            if (i in columnsWithPipes) {
                val char = line[i++]
                isInside = isInside xor when (char) {
                    'F', 'L' -> {
                        while (line[i] == '-') i++

                        when (val endingChar = line[i++]) {
                            '7' -> char == 'L'
                            'J' -> char == 'F'
                            else -> throw IllegalStateException("Unexpected character ''$endingChar")
                        }
                    }

                    '|' -> true
                    else -> false
                }
            } else {
                if (isInside) {
                    result++
                }
                i++
            }
        }

        result
    }.sum()
}

fun main() {
    val testInput = parseInput("day10/test")
    val testInput2 = parseInput("day10/test_2")
    val input = parseInput("day10/input")

    check(part1(testInput) == 4)
    check(part1(testInput2) == 8)
    println(part1(input))

    val testInput3 = parseInput("day10/test_3")
    val testInput4 = parseInput("day10/test_4")
    val testInput5 = parseInput("day10/test_5")
    val testInput6 = parseInput("day10/test_6")
    check(part2(testInput) == 1)
    check(part2(testInput2) == 1)
    check(part2(testInput3) == 4)
    check(part2(testInput4) == 4)
    check(part2(testInput5) == 8)
    check(part2(testInput6) == 10)
    println(part2(input))
}
