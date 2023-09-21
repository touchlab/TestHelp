package co.touchlab.testhelp.concurrency

import co.touchlab.testhelp.freeze
import kotlin.time.TimeSource

expect class MPWorker() {
    fun <T> runBackground(backJob: () -> T): MPFuture<T>
    fun requestTermination()
}

expect class MPFuture<T> {
    val done: Boolean
    fun consume(): T
}

fun createWorker(): MPWorker = MPWorker()

expect fun sleep(time: Long)

class ThreadOperations<T>(val producer: () -> T) {
    private val exes = mutableListOf<(T) -> Unit>()
    private val tests = mutableListOf<(T) -> Unit>()
    var lastRunTime = 0L

    fun exe(proc: (T) -> Unit) {
        exes.add(proc)
    }

    fun test(proc: (T) -> Unit) {
        tests.add(proc)
    }

    fun run(
        threads: Int,
        randomize: Boolean = false,
        timeout: Long = 0,
        onTimeout: () -> Unit = {}
    ): T {
        if (randomize) {
            exes.shuffle()
            tests.shuffle()
        }

        exes.freeze()

        val target = producer()
        val start = TimeSource.Monotonic.markNow()

        val workers = Array(threads) { MPWorker() }
        val futures = exes.mapIndexed { index, function ->
            workers[index % workers.size]
                .runBackground {
                    function(target)
                }
        }

        if (timeout == 0L) {
            futures.forEach { it.consume() }
        } else {
            while (futures.any { !it.done }) {
                val measured = TimeSource.Monotonic.markNow().minus(start).inWholeMilliseconds
                if (measured > timeout) {
                    onTimeout()
                    throw TestTimeoutException()
                }
                sleep(50)
            }
        }
        workers.forEach { it.requestTermination() }

        tests.forEach { it(target) }

        lastRunTime = TimeSource.Monotonic.markNow().minus(start).inWholeMilliseconds

        return target
    }
}

class TestTimeoutException : Exception("ThreadOperations run timed out")