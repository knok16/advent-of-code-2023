package day14

import readInput

private fun shiftLine(n: Int, get: (Int) -> Char, set: (Int, Char) -> Unit) {
    var availableSpace = 0

    repeat(n) { index ->
        when (val char = get(index)) {
            '#' -> availableSpace = index + 1
            '.' -> {}
            'O' -> {
                set(index, '.')
                set(availableSpace, 'O')
                availableSpace++
            }

            else -> throw IllegalArgumentException("Unexpected char '$char'")
        }
    }
}

private fun shiftNorth(input: List<CharArray>) = input[0].indices.forEach { column ->
    shiftLine(
        input.size,
        { index -> input[index][column] },
        { index, char -> input[index][column] = char }
    )
}

private fun shiftSouth(input: List<CharArray>) = input[0].indices.forEach { column ->
    shiftLine(
        input.size,
        { index -> input[input.size - 1 - index][column] },
        { index, char -> input[input.size - 1 - index][column] = char }
    )
}

private fun shiftWest(input: List<CharArray>) = input.indices.forEach { row ->
    shiftLine(
        input[row].size,
        input[row]::get,
        input[row]::set
    )
}

private fun shiftEast(input: List<CharArray>) = input.indices.forEach { row ->
    shiftLine(
        input[row].size,
        { index -> input[row][input[row].size - 1 - index] },
        { index, char -> input[row][input[row].size - 1 - index] = char }
    )
}

fun calculateStress(input: List<CharArray>) = input[0].indices.sumOf { column ->
    input.indices.filter { index -> input[index][column] == 'O' }.sumOf { index -> input.size - index }
}

private fun part1(input: List<CharArray>): Int {
    shiftNorth(input)
    return calculateStress(input)
}

private fun part2(input: List<CharArray>): Int {
    val cache = HashMap<String, Int>() // At which cycle we meet such arrangement last

    var cycleIndex = 1
    while (cycleIndex <= 1000000000) {
        shiftNorth(input)
        shiftWest(input)
        shiftSouth(input)
        shiftEast(input)
        val next = input.joinToString(separator = "\n") { String(it) }

        if (next in cache) {
            val cyclesLeft = 1000000000 - cycleIndex
            val l = cycleIndex - cache.getValue(next)
            if (cyclesLeft % l == 0) break
        }

        cache[next] = cycleIndex++
    }

    return calculateStress(input)
}

fun main() {
    val testInput = readInput("day14/test")
    val input = readInput("day14/input")

    check(part1(testInput.map { it.toCharArray() }) == 136)
    println(part1(input.map { it.toCharArray() }))

    check(part2(testInput.map { it.toCharArray() }) == 64)
    println(part2(input.map { it.toCharArray() }))
}
