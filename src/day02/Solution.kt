package day02

import readInput

fun main() {
    fun List<String>.parseInput(): List<Map<String, Int>> = map { line ->
        line.substringAfter(':')
            .split(',', ';')
            .map {
                val (n, color) = it.trim().split(' ')

                color to n.toInt()
            }.groupBy { it.first }
            .mapValues { (_, values) -> values.maxOf { it.second } }
    }

    fun part1(input: List<String>): Int = input
        .parseInput()
        .withIndex()
        .filter { (_, value) ->
            value.getValue("red") <= 12 &&
                    value.getValue("green") <= 13 &&
                    value.getValue("blue") <= 14
        }.sumOf { it.index + 1 }

    fun part2(input: List<String>): Int = input
        .parseInput()
        .sumOf {
            it.values.reduce { acc, i -> acc * i }
        }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day02/test")
    check(part1(testInput) == 8)
    check(part2(testInput) == 2286)

    val input = readInput("day02/input")

    println(part1(input))
    println(part2(input))
}
