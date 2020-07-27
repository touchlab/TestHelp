package co.touchlab.testhelp.concurrency

private val globalWorker = createWorker()

fun <R> background(block: () -> R): R {
    val future = globalWorker.runBackground(block)
    return future.consume()
}
