package day08

import readInput

fun <T> Sequence<T>.repeat(): Sequence<T> =
    generateSequence { this }.flatten()

fun gcd(a: Long, b: Long): Long = if (b == 0L) a else gcd(b, a % b)

fun main() {
    val regex = Regex("""([A-Z0-9]{3}) = \(([A-Z0-9]{3}), ([A-Z0-9]{3})\)""")

    fun parseLine(line: String): Pair<String, Pair<String, String>> {
        val (_, node, left, right) = regex.matchEntire(line)!!.groupValues
        return node to (left to right)
    }

    fun countSteps(
        direction: String,
        map: Map<String, Pair<String, String>>,
        start: String,
        stopCondition: (String) -> Boolean
    ) = direction.asSequence()
        .repeat()
        .runningFold(start) { place, turn ->
            val fork = map.getValue(place)
            when (turn) {
                'L' -> fork.first
                'R' -> fork.second
                else -> throw IllegalArgumentException("Unexpected turn command '$turn'")
            }
        }
        .takeWhile { !stopCondition(it) }
        .count()

    fun part1(input: List<String>): Int {
        val direction = input.first()
        val map = input.drop(2).map { line -> parseLine(line) }.associate { it }

        return countSteps(direction, map, "AAA") { it == "ZZZ" }
    }

    fun part2(input: List<String>): Long {
        val direction = input.first()
        val map = input.drop(2).map { line -> parseLine(line) }.associate { it }

        return map.keys.filter {
            it.endsWith("A")
        }.map { place ->
            countSteps(direction, map, place) { it.endsWith("Z") }.toLong()
        }.reduce { acc, it ->
            acc * it / gcd(acc, it)
        }
    }

    val testInput = readInput("day08/test")
    val testInput2 = readInput("day08/test_2")
    val input = readInput("day08/input")

    check(part1(testInput) == 2)
    check(part1(testInput2) == 6)
    println(part1(input))

    val testInput3 = readInput("day08/test_3")
    check(part2(testInput3) == 6L)
    println(part2(input))
}
