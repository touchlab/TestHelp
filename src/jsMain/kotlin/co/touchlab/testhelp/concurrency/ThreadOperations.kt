package co.touchlab.testhelp.concurrency

import kotlin.js.Date

actual class MPWorker actual constructor(){

    actual fun <T> runBackground(backJob: () -> T): MPFuture<T> {
        return MPFuture()
    }

    actual fun requestTermination() {

    }
}

actual class MPFuture<T>() {
    actual fun consume():T{
        throw Throwable()
    }
}

actual fun sleep(time: Long) {
    throw Throwable()
}

actual fun currentTimeMillis():Long = Date().getTime().toLong()