package day20

import readInput
import java.util.*

private enum class Pulse {
    LOW,
    HIGH
}

private data class Signal(
    val fromModule: String,
    val toModule: String,
    val pulse: Pulse
)

private sealed interface ModuleLogic {
    fun process(signal: Signal): Pulse?
}

private data object RelayLogic : ModuleLogic {
    override fun process(signal: Signal): Pulse =
        signal.pulse
}

private class FlipFlopLogic : ModuleLogic {
    private var on = false

    override fun process(signal: Signal): Pulse? =
        when (signal.pulse) {
            Pulse.HIGH -> null
            Pulse.LOW -> {
                on = !on
                if (on) Pulse.HIGH else Pulse.LOW
            }
        }
}

private class ConjunctionLogic(sourceModules: List<String>) : ModuleLogic {
    private val state = sourceModules.associateWith { Pulse.LOW }.toMutableMap()

    override fun process(signal: Signal): Pulse {
        state[signal.fromModule] = signal.pulse
        return if (state.all { (_, value) -> value == Pulse.HIGH }) Pulse.LOW
        else Pulse.HIGH
    }
}

private class Module(
    val destinationModules: List<String>,
    val moduleLogic: ModuleLogic
)

private data class ModuleSchema(
    val nameAndType: String,
    val sourceModules: List<String>,
    val destinationModules: List<String>,
) {
    val name: String
        get() = nameAndType.dropWhile { !it.isLetter() }

    fun instantiate() = Module(
        destinationModules = destinationModules,
        moduleLogic = when (nameAndType.first()) {
            '%' -> FlipFlopLogic()
            '&' -> ConjunctionLogic(sourceModules)
            else -> RelayLogic
        }
    )
}

private fun parseInput(fileName: String): List<ModuleSchema> {
    val modulesWithoutSources = readInput(fileName).map { line ->
        val (nameAndType, destinationsList) = line.split("->").map { it.trim() }

        ModuleSchema(
            nameAndType = nameAndType,
            sourceModules = emptyList(),
            destinationModules = destinationsList.split(',').map { it.trim() },
        )
    }

    val sources = modulesWithoutSources
        .flatMap { module -> module.destinationModules.map { module.name to it } }
        .groupBy({ it.second }, { it.first })

    return modulesWithoutSources.map {
        it.copy(sourceModules = sources[it.name] ?: emptyList())
    }
}

private fun part1(modules: List<ModuleSchema>): Long {
    val modulesByName = modules.associate { it.name to it.instantiate() }
    val result = LongArray(2)

    repeat(1000) {
        val queue: Queue<Signal> = LinkedList()
        queue.offer(Signal("button", "broadcaster", Pulse.LOW))

        while (queue.isNotEmpty()) {
            val signal = queue.remove()

            result[if (signal.pulse == Pulse.LOW) 0 else 1]++

            modulesByName[signal.toModule]?.let { module ->
                module.moduleLogic.process(signal)?.let { pulse ->
                    module.destinationModules.forEach { dest ->
                        queue.offer(Signal(signal.toModule, dest, pulse))
                    }
                }
            }
        }
    }

    return result[0] * result[1]
}

private fun part2(modules: List<ModuleSchema>): Long {
    val modulesByName = modules.associate { it.name to it.instantiate() }
    var result = 0L
    var i = 0L

    val moduleBeforeRx = modules.single { "rx" in it.destinationModules }.name

    while (true) {
        val queue: Queue<Signal> = LinkedList()
        queue.offer(Signal("button", "broadcaster", Pulse.LOW))
        result++

        while (queue.isNotEmpty()) {
            i++
            val signal = queue.remove()

            if (signal.toModule == moduleBeforeRx && signal.pulse == Pulse.HIGH) {
                println("Got ${signal.pulse} signal from '${signal.fromModule}' on $result signal propagation")
            }

            if (signal.toModule == "rx" && signal.pulse == Pulse.LOW) return result

            modulesByName[signal.toModule]?.let { module ->
                module.moduleLogic.process(signal)?.let { pulse ->
                    module.destinationModules.forEach { dest ->
                        queue.offer(Signal(signal.toModule, dest, pulse))
                    }
                }
            }
        }
    }
}

fun main() {
    val testInput = parseInput("day20/test")
    val testInput2 = parseInput("day20/test_2")
    val input = parseInput("day20/input")

    check(part1(testInput) == 32000000L)
    check(part1(testInput2) == 11687500L)
    check(part1(input) == 886701120L)
    println(part1(input))

    check(part2(input) == 228134431501037)
}
