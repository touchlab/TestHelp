package co.touchlab.testhelp.concurrency

actual class MPWorker actual constructor() {

    actual fun <T> runBackground(backJob: () -> T): MPFuture<T> {
        return MPFuture(backJob)
    }

    actual fun requestTermination() {
    }
}

actual class MPFuture<T>(job: () -> T) {
    private val result: Result<T> = runCatching(job)

    actual fun consume(): T = result.getOrThrow()
    actual val done: Boolean
        get() = result.isFailure || result.isSuccess
}

actual fun sleep(time: Long) {
    throw Throwable()
}