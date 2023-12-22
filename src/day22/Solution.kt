package day22

import readInput
import kotlin.math.min

private data class Point(
    val x: Int,
    val y: Int,
    val z: Int
)

private class Brick(
    val from: Point,
    val to: Point
) {
    init {
        check(listOf(from.x == to.x, from.y == to.y, from.z == to.z).count { it } >= 2)
    }

    fun parts() =
        (from.x..to.x).flatMap { x ->
            (from.y..to.y).flatMap { y ->
                (from.z..to.z).map { z ->
                    Point(x, y, z)
                }
            }
        }

    fun lower(n: Int) =
        Brick(
            from.copy(z = from.z - n),
            to.copy(z = to.z - n),
        )
}

private fun parseInput(name: String) = readInput(name).map { line ->
    val coordinates = line.split('~', ',').map { it.toInt() }
    Brick(
        Point(coordinates[0], coordinates[1], coordinates[2]),
        Point(coordinates[3], coordinates[4], coordinates[5])
    )
}

// Return list of pair where <BlockA, /*supported by*/, BlockB>
private fun process(bricks: List<Brick>): ArrayList<Pair<Int, Int>> {
    val taken = HashMap<Point, Int>()
    val supportedBy = ArrayList<Pair<Int, Int>>()

    bricks.sortedBy { min(it.from.z, it.to.z) }.forEachIndexed { index, brick ->
        var b = brick
        while (true) {
            val lowered = b.lower(1)
            val parts = lowered.parts()
            if (parts.any { it.z < 1 }) break

            val interfereWith = parts.mapNotNull { taken[it] }.distinct()

            if (interfereWith.isNotEmpty()) {
                supportedBy.addAll(interfereWith.map { index to it })
                break
            }

            b = lowered
        }
        b.parts().forEach {
            taken[it] = index
        }
    }

    return supportedBy
}

private fun part1(bricks: List<Brick>): Int =
    bricks.size - process(bricks).groupBy({ it.first }, { it.second })
        .filter { (_, supportingBricks) -> supportingBricks.size == 1 }
        .values
        .flatten()
        .distinct()
        .size

private fun part2(bricks: List<Brick>): Int {
    // <BlockA, /*supported by*/, BlockB>
    val supportedBy = process(bricks)
    val numberOfSupports = supportedBy.groupingBy { it.first }.eachCount()
    val supports = supportedBy.groupBy({ it.second }, { it.first })

    return supports.keys.sumOf { brickIndex ->
        val supportsLeft = numberOfSupports.toMutableMap()

        fun dfs(v: Int): Int =
            supports[v]?.sumOf { supportedBrickIndex ->
                val newSupportCount = supportsLeft.getValue(supportedBrickIndex) - 1
                check(newSupportCount >= 0)
                supportsLeft[supportedBrickIndex] = newSupportCount
                if (newSupportCount == 0) 1 + dfs(supportedBrickIndex) else 0
            } ?: 0

        dfs(brickIndex)
    }
}

fun main() {
    val testInput = parseInput("day22/test")
    val input = parseInput("day22/input")

    check(part1(testInput) == 5)
    check(part1(input) == 490)

    check(part2(testInput) == 7)
    check(part2(input) == 96356)
}
