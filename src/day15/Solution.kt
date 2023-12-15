package day15

import readInput

private fun parseInput(name: String) = readInput(name).flatMap { it.split(',') }

private fun hashCode(str: String): Int = str.fold(0) { acc, c ->
    (acc + c.code) * 17 % 256
}

private fun part1(input: List<String>) = input.sumOf { hashCode(it) }

data class Lens(
    val label: String,
    val focalLength: Int
)

private fun part2(input: List<String>): Int {
    val boxes = Array(256) { ArrayList<Lens>() }

    input.forEach { command ->
        val label = command.takeWhile { it.isLetter() }
        val box = boxes[hashCode(label)]
        when (val char = command.last()) {
            '-' -> box.removeIf { it.label == label }
            in '1'..'9' -> {
                val lens = Lens(label, char - '0')

                val oldIndex = box.indexOfFirst { it.label == label }.takeIf { it >= 0 }

                if (oldIndex != null) {
                    box[oldIndex] = lens
                } else {
                    box.add(lens)
                }
            }

            else -> throw IllegalArgumentException("Unexpected character '$char'")
        }
    }

    return boxes.withIndex().sumOf { (boxIndex, lenses) ->
        (boxIndex + 1) * lenses.withIndex().sumOf { (lensIndex, lens) -> (lensIndex + 1) * lens.focalLength }
    }
}

fun main() {
    val testInput = parseInput("day15/test")
    val input = parseInput("day15/input")

    check(part1(testInput) == 1320)
    println(part1(input))

    check(part2(testInput) == 145)
    println(part2(input))
}
