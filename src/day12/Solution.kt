package day12

import readInput

private fun parseInput(name: String) = readInput(name).map { line ->
    val (a, b) = line.split(' ')
    a to b.split(',').map { it.toInt() }
}

private fun Char.blockOrUnknown() = this == '#' || this == '?'
private fun Char.emptyOrUnknown() = this == '.' || this == '?'

private fun part1(input: List<Pair<String, List<Int>>>) = input
    .sumOf { (pictorialRepresentation, blocks) ->
        // dp[block][endsAt] = number of arrangements of blocks with indexes <= {block}
        // in pictorialRepresentation prefix ending at {endsAt}
        val dp = Array(blocks.size) { LongArray(pictorialRepresentation.length) }

        fun blockCanBePlaced(start: Int, end: Int): Boolean {
            return start >= 0 &&
                    (start..end).all { pictorialRepresentation[it].blockOrUnknown() } &&
                    (start == 0 || pictorialRepresentation[start - 1].emptyOrUnknown()) &&
                    (end == pictorialRepresentation.lastIndex || pictorialRepresentation[end + 1].emptyOrUnknown())
        }

        for (at in pictorialRepresentation.indices) {
            blocks.forEachIndexed { blockIndex, blockSize ->
                dp[blockIndex][at] = if (at > 0 && pictorialRepresentation[at].emptyOrUnknown())
                    dp[blockIndex][at - 1]
                else
                    0

                val start = at - blockSize + 1

                if (blockCanBePlaced(start = start, end = at)) {
                    dp[blockIndex][at] += when {
                        blockIndex == 0 -> if ((0 until start).all { pictorialRepresentation[it].emptyOrUnknown() }) 1 else 0
                        start >= 2 -> dp[blockIndex - 1][start - 2]
                        else -> 0
                    }
                }
            }
        }

        dp[blocks.lastIndex][pictorialRepresentation.lastIndex]
    }

private fun part2(input: List<Pair<String, List<Int>>>): Long = part1(input.map { (str, blocks) ->
    "$str?$str?$str?$str?$str" to blocks + blocks + blocks + blocks + blocks
})

fun main() {
    val testInput = parseInput("day12/test")
    val input = parseInput("day12/input")

    check(part1(testInput) == 21L)
    println(part1(input))

    check(part2(testInput) == 525152L)
    println(part2(input))
}
