import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/$name.txt").readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)

fun <T> List<T>.splitBy(separator: T): List<List<T>> {
    val result = ArrayList<List<T>>()

    var current = ArrayList<T>()
    forEach {
        if (it == separator) {
            if (current.isNotEmpty()) {
                result.add(current)
                current = ArrayList()
            }
        } else {
            current.add(it)
        }
    }

    if (current.isNotEmpty()) {
        result.add(current)
        current = ArrayList()
    }

    return result
}

fun <T> timed(work: (Unit) -> T): Pair<T, Long> {
    val start = System.currentTimeMillis()

    val result = work.invoke(Unit)

    val timeTaken = System.currentTimeMillis() - start

    return result to timeTaken
}

fun benchmark(times: Int, work: (Unit) -> Any): List<Long> =
    List(times) {
        timed(work).second
    }
