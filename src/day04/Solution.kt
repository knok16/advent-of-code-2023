package day04

import readInput

fun main() {
    data class Card(
        val winningNumbers: Set<Int>,
        val numbers: Set<Int>
    ) {
        val numberOfMatches: Int
            get() = numbers.count { it in winningNumbers }
    }

    fun List<String>.parseCards(): List<Card> = map { line ->
        val (winningNumbers, numbers) = line
            .split(':', '|')
            .drop(1)
            .map {
                it.split(' ').filter { it.isNotBlank() }.map { number -> number.toInt() }.toSet()
            }
        Card(winningNumbers, numbers)
    }

    fun part1(input: List<String>): Int = input
        .parseCards()
        .sumOf { card ->
            (1 shl card.numberOfMatches) shr 1
        }

    fun part2(input: List<String>): Int = input
        .parseCards()
        .let { cards ->
            val cardsTaken = IntArray(cards.size) { 1 }

            cards.forEachIndexed { index, card ->
                repeat(card.numberOfMatches) { j ->
                    cardsTaken[index + 1 + j] += cardsTaken[index]
                }
            }

            cardsTaken.sum()
        }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day04/test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 30)

    val input = readInput("day04/input")

    println(part1(input))
    println(part2(input))
}
