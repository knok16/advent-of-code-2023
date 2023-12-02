package day01

import readInput

fun main() {
    fun part1(input: List<String>): Int = input.sumOf { line ->
        (line.first { it.isDigit() } - '0') * 10 + (line.last { it.isDigit() } - '0')
    }

    val numbers = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")

    fun part2(input: List<String>): Int = input.sumOf { line ->
        val digits = line.mapIndexedNotNull { index, c ->
            if (c.isDigit()) c - '0'
            else (numbers.indexOfFirst { line.startsWith(it, index) } + 1).takeIf { it > 0 }
        }

        digits.first() * 10 + digits.last()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day01/test")
    check(part1(testInput) == 142)
    val testInput2 = readInput("day01/test_2")
    check(part2(testInput2) == 281)

    val input = readInput("day01/input")

    println(part1(input))
    println(part2(input))
}
