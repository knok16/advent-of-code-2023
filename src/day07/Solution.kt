package day07

import readInput

private enum class Card {
    JOKER, C2, C3, C4, C5, C6, C7, C8, C9, T, J, Q, K, A
}

private enum class Combination : Comparable<Combination> {
    HIGH_CARD,
    ONE_PAIR,
    TWO_PAIR,
    THREE_OF_KIND,
    FULL_HOUSE,
    FOUR_OF_KIND,
    FIVE_OF_KIND;

    companion object {
        fun from(hand: List<Card>): Combination {
            check(hand.size == 5) { "Hand should contain 5 cards, but was '$hand'" }

            val jokers = hand.count { it == Card.JOKER }
            val counts = hand.filter { it != Card.JOKER }.groupingBy { it }.eachCount().values.sortedDescending()
            val max = counts.firstOrNull() ?: 0

            return when {
                max + jokers == 5 -> FIVE_OF_KIND
                max + jokers == 4 -> FOUR_OF_KIND
                counts.size == 2 -> FULL_HOUSE
                max + jokers == 3 -> THREE_OF_KIND
                counts.count { it == 2 } == 2 -> TWO_PAIR
                max + jokers == 2 -> ONE_PAIR
                else -> HIGH_CARD
            }
        }
    }
}

private data class Hand(
    val cards: List<Card>,
    val bet: Int
) : Comparable<Hand> {
    init {
        check(cards.size == 5) { "Hand should contain 5 cards, but was '$cards'" }
    }

    private val combination = Combination.from(cards)

    override fun compareTo(other: Hand): Int =
        compareValues(combination, other.combination).takeIf { it != 0 }
            ?: cards.zip(other.cards)
                .map { (c1, c2) -> compareValues(c1, c2) }
                .firstOrNull { it != 0 }
            ?: 0
}

fun main() {
    fun List<String>.parseInput(cardParser: (Char) -> Card) = map { line ->
        val (hand, bet) = line.split(' ')
        Hand(hand.map(cardParser), bet.toInt())
    }

    fun solve(input: List<Hand>) = input
        .sorted()
        .mapIndexed { index, (_, bet) ->
            (index + 1) * bet
        }.sum()

    fun part1(input: List<String>) =
        solve(input.parseInput { char ->
            when (char) {
                in '2'..'9' -> Card.valueOf("C$char")
                else -> Card.valueOf(char.toString())
            }
        })

    fun part2(input: List<String>): Int =
        solve(input.parseInput { char ->
            when (char) {
                'J' -> Card.JOKER
                in '2'..'9' -> Card.valueOf("C$char")
                else -> Card.valueOf(char.toString())
            }
        })

    val testInput = readInput("day07/test")
    val input = readInput("day07/input")

    check(part1(testInput) == 6440)
    println(part1(input))

    check(part2(testInput) == 5905)
    println(part2(input))
}
