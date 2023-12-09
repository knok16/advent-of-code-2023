package day09

import readInput

private fun parseInput(name: String) =
    readInput(name).map { line -> line.split(' ').map { it.toInt() } }

private fun next(seq: List<Int>): Int =
    if (seq.all { it == 0 }) 0
    else seq.last() + next(seq.zipWithNext { a, b -> b - a })

private fun part1(input: List<List<Int>>) =
    input.sumOf { next(it) }

private fun part2(input: List<List<Int>>) =
    part1(input.map { it.reversed() })

fun main() {
    val testInput = parseInput("day09/test")
    val input = parseInput("day09/input")

    check(part1(testInput) == 114)
    println(part1(input))

    check(part2(testInput) == 2)
    println(part2(input))
}
