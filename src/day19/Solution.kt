package day19

import readInput
import splitBy

private typealias Part = Map<String, Int>

private data class Rule(
    val fieldName: String,
    val comparator: Char,
    val value: Int,
    val outcome: String
) {
    init {
        fieldName in setOf("x", "m", "a", "s")
        comparator in setOf('<', '>')
    }

    fun accept(part: Part): Boolean {
        val partValue = part.getValue(fieldName)
        return when (comparator) {
            '<' -> partValue < value
            '>' -> partValue > value
            else -> throw IllegalStateException("Unexpected comparator: '$comparator'")
        }
    }
}

private data class Workflow(
    val name: String,
    val stages: List<Rule>,
    val outcome: String
) {
    fun process(part: Map<String, Int>): String =
        stages.firstOrNull { it.accept(part) }?.outcome ?: outcome
}

private fun MatchNamedGroupCollection.getValue(name: String) =
    get(name)?.value ?: throw NoSuchElementException("'$name' is missing in the matching result.")

// a<2006:qkq
private val ruleRegex = Regex("(?<fieldName>[xmas])(?<comparator>[><])(?<value>[0-9]+):(?<outcome>[a-zA-Z]+)")

private fun parseRule(line: String): Rule {
    val matchResult = (ruleRegex.matchEntire(line)!!.groups as MatchNamedGroupCollection)

    return Rule(
        matchResult.getValue("fieldName"),
        matchResult.getValue("comparator")[0],
        matchResult.getValue("value").toInt(),
        matchResult.getValue("outcome")
    )
}

// px{a<2006:qkq,m>2090:A,rfg}
private val workflowRegex =
    Regex("""(?<wfName>[a-zA-Z]+)\{(?<rules>(?:${ruleRegex.pattern},)*)(?<finalOutcome>[a-zA-Z]+)\}""")

private fun parseInput(name: String): Pair<List<Part>, Map<String, Workflow>> {
    val (workflows, parts) = readInput(name).splitBy("")

    return parts.map { line ->
        line
            .split('{', '}', ',')
            .filter { it.isNotBlank() }
            .associate {
                val (key, value) = it.trim().split('=')
                key to value.toInt()
            }
    } to workflows.map { line ->
        val matchResult = workflowRegex.matchEntire(line)!!.groups as MatchNamedGroupCollection

        Workflow(
            name = matchResult.getValue("wfName"),
            stages = matchResult.getValue("rules").split(',').filter { it.isNotBlank() }.map { parseRule(it) },
            outcome = matchResult.getValue("finalOutcome")
        )
    }.associateBy { it.name }
}

private fun part1(input: Pair<List<Part>, Map<String, Workflow>>): Int {
    val (parts, workflows) = input

    return parts.sumOf { part ->
        var w = "in"
        while (w != "R" && w != "A") {
            w = workflows.getValue(w).process(part)
        }
        if (w == "A") part.values.sum() else 0
    }
}

private typealias SearchSpace = Map<String, IntRange>

private val IntRange.length: Int
    get() = last - first + 1

private fun IntRange.partition(rule: Rule): Pair<IntRange, IntRange> =
    when (rule.comparator) {
        '<' -> first until rule.value to rule.value..last
        '>' -> rule.value + 1..last to first..rule.value
        else -> throw IllegalStateException("Unexpected comparator: '${rule.comparator}'")
    }

private val SearchSpace.spaceSize: Long
    get() = values.fold(1L) { acc, range -> acc * range.length }

private fun SearchSpace.partition(rule: Rule): Pair<SearchSpace, SearchSpace> =
    getValue(rule.fieldName).partition(rule).let { (t, f) ->
        (this + (rule.fieldName to t)) to (this + (rule.fieldName to f))
    }

private fun part2(input: Pair<List<Part>, Map<String, Workflow>>): Long {
    val (_, workflows) = input

    fun dfs(space: SearchSpace, workflowName: String): Long = when (workflowName) {
        "A" -> space.spaceSize
        "R" -> 0L
        else -> {
            with(workflows.getValue(workflowName)) {
                val (result, s) = stages.fold(0L to space) { (acc, s), rule ->
                    val (accepted, notAccepted) = s.partition(rule)
                    acc + dfs(accepted, rule.outcome) to notAccepted
                }
                result + dfs(s, outcome)
            }
        }
    }

    return dfs(
        space = mapOf(
            "x" to 1..4000,
            "m" to 1..4000,
            "a" to 1..4000,
            "s" to 1..4000
        ),
        workflowName = "in"
    )
}

fun main() {
    val testInput = parseInput("day19/test")
    val input = parseInput("day19/input")

    check(part1(testInput) == 19114)
    check(part1(input) == 280909)

    check(part2(testInput) == 167409079868000)
    check(part2(input) == 116138474394508)
}
