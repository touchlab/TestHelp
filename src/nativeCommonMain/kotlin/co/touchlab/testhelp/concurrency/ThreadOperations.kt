package co.touchlab.testhelp.concurrency

import co.touchlab.testhelp.freeze
import kotlin.native.concurrent.AtomicReference
import kotlin.native.concurrent.Future
import kotlin.native.concurrent.FutureState
import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker
import kotlin.system.getTimeMillis

actual class MPWorker actual constructor() {
    val worker = Worker.start()
    actual fun <T> runBackground(backJob: () -> T): MPFuture<T> {
        val ar = AtomicReference<Throwable?>(null)
        val wrappedJob: () -> T = {
            try {
                backJob()
            } catch (e: Throwable) {
                ar.value = e.freeze()
                throw e
            }
        }
        return MPFuture(
            worker.execute(TransferMode.SAFE, { wrappedJob.freeze() }) {
                it()
            },
            ar
        )
    }

    actual fun requestTermination() {
        worker.requestTermination().result
    }
}

actual class MPFuture<T>(private val future: Future<T>, val thrown: AtomicReference<Throwable?>) {

    actual fun consume(): T = try {
        future.result
    } catch (e: Exception) {
        val th = thrown.value
        if (th != null) {
            throw th
        } else {
            throw e
        }
    } finally {
        thrown.value = null // Memory leaks...
    }

    actual val done: Boolean
        get() = (future.state == FutureState.THROWN || future.state == FutureState.CANCELLED || future.state == FutureState.INVALID || future.state == FutureState.COMPUTED)
}

actual fun currentTimeMillis(): Long = getTimeMillis()
