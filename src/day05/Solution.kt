package day05

import readInput
import splitBy

fun main() {
    data class Range(
        val destinationStart: Long,
        val sourceStart: Long,
        val length: Long
    ) {
        private val sourceRange = sourceStart until (sourceStart + length)

        operator fun contains(value: Long) = value in sourceRange

        operator fun contains(value: LongRange) = contains(value.first) && contains(value.last)

        fun map(value: Long): Long {
            check(value in this)
            return value - sourceStart + destinationStart
        }

        fun map(range: LongRange): LongRange =
            map(range.first)..map(range.last)
    }

    data class Mapper(
        val from: String,
        val to: String,
        val ranges: List<Range>
    ) {
        fun map(range: LongRange): List<LongRange> {
            val from = range.first
            val to = range.last + 1 // exclusive

            return (ranges
                .flatMap { listOf(it.sourceStart, it.sourceStart + it.length) }
                .filter { it in range } +
                    listOf(from, to))
                .distinct()
                .sorted()
                .zipWithNext { a, b -> a until b }
                .map { smallerRangeToMap ->
                    ranges.find { smallerRangeToMap in it }?.map(smallerRangeToMap) ?: smallerRangeToMap
                }
        }
    }

    val mapperNameParser = Regex("""([a-z]+)-to-([a-z]+) map:""")

    fun List<String>.parseInput(): Pair<List<Long>, List<Mapper>> {
        val blocks = splitBy("")

        val seeds = blocks[0][0].split(' ').drop(1).map { it.toLong() }
        val mappers = blocks.drop(1)
            .map { block ->
                val header = block[0]
                val (_, from, to) = mapperNameParser.matchEntire(header)!!.groupValues
                val ranges = block.drop(1)
                    .map { range ->
                        val (destinationStart, sourceStart, length) = range.split(' ').map { it.toLong() }
                        Range(destinationStart, sourceStart, length)
                    }

                Mapper(from, to, ranges)
            }

        check(mappers.zipWithNext().all { (a, b) -> a.to == b.from })

        return seeds to mappers
    }

    fun solve(seedRanges: List<LongRange>, mappers: List<Mapper>): Long =
        mappers.fold(seedRanges) { acc, mapper ->
            acc.flatMap { range ->
                mapper.map(range)
            }.distinct()
        }.minOf { seed ->
            seed.first
        }

    fun part1(input: List<String>): Long = input
        .parseInput()
        .let { (seeds, mappers) ->
            solve(seeds.map { it..it }, mappers)
        }

    fun part2(input: List<String>): Long = input
        .parseInput()
        .let { (seeds, mappers) ->
            val initialRanges = seeds
                .chunked(2)
                .map { (start, length) -> start until (start + length) }

            solve(initialRanges, mappers)
        }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("day05/test")

    check(part1(testInput) == 35L)
    check(part2(testInput) == 46L)

    val input = readInput("day05/input")

    println(part1(input))
    println(part2(input))
}
