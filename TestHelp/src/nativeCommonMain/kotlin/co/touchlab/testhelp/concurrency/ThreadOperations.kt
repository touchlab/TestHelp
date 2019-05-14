package co.touchlab.testhelp.concurrency

import platform.posix.usleep
import kotlin.native.concurrent.Future
import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker
import kotlin.native.concurrent.freeze
import kotlin.system.getTimeMillis

actual class MPWorker actual constructor(){
    val worker = Worker.start()
    actual fun <T> runBackground(backJob: () -> T): MPFuture<T> {
        return MPFuture(worker.execute(TransferMode.SAFE, {backJob.freeze()}){
            it()
        })
    }

    actual fun requestTermination() {
        worker.requestTermination().result
    }
}

actual class MPFuture<T>(private val future: Future<T>) {
    actual fun consume():T = future.result
}

actual fun sleep(time: Long) {
    usleep((time * 1000).toUInt()) // this takes microseconds, so multiply by 1000 to use miliseconds.
}

actual fun currentTimeMillis(): Long = getTimeMillis()