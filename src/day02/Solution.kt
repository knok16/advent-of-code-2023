package day02

import readInput

fun main() {
    data class Game(
        val red: Int,
        val green: Int,
        val blue: Int
    )

    fun List<String>.parseInput(): List<Game> = map { line ->
        line.substringAfter(':')
            .split(',', ';')
            .map {
                val (n, color) = it.trim().split(' ')

                color to n.toInt()
            }.groupBy({ it.first }, { it.second })
            .mapValues { (_, values) -> values.max() }
    }.map {
        Game(
            red = it["red"] ?: 0,
            green = it["green"] ?: 0,
            blue = it["blue"] ?: 0
        )
    }

    fun part1(input: List<String>): Int = input
        .parseInput()
        .withIndex()
        .filter { (_, game) ->
            game.red <= 12 && game.green <= 13 && game.blue <= 14
        }.sumOf { it.index + 1 }

    fun part2(input: List<String>): Int = input
        .parseInput()
        .sumOf { game ->
            game.red * game.green * game.blue
        }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day02/test")
    check(part1(testInput) == 8)
    check(part2(testInput) == 2286)

    val input = readInput("day02/input")

    println(part1(input))
    println(part2(input))
}
