package day06

import readInput
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt

fun main() {
    fun parseLine(line: String) = line.substringAfter(':')
        .split(' ')
        .filter { it.isNotEmpty() }
        .map { it.toLong() }

    fun solve(time: Long, distance: Long): Long {
        // v * (time - v) > distance
        // v * (time - v) - distance > 0
        // -v^2 + time * v - distance > 0

        val d2 = time * time - 4 * distance
        check(d2 > 0)

        val d = sqrt(d2.toDouble())
        val (l, r) = listOf(
            (time + d) / 2,
            (time - d) / 2
        ).sorted()

        return (ceil(r - 1) - floor(l + 1)).toLong() + 1
    }

    fun part1(input: List<String>): Long {
        val (times, distances) = input.map { parseLine(it) }

        return times.zip(distances)
            .map { (time, distance) ->
                solve(time, distance)
            }
            .reduce { a, b -> a * b }
    }

    fun parseLine2(line: String) = line.filter { it.isDigit() }.toLong()

    fun part2(input: List<String>) = solve(
        parseLine2(input[0]),
        parseLine2(input[1]),
    )

    val testInput = readInput("day06/test")
    val input = readInput("day06/input")

    check(part1(testInput) == 288L)
    println(part1(input))

    check(part2(testInput) == 71503L)
    println(part2(input))
}
