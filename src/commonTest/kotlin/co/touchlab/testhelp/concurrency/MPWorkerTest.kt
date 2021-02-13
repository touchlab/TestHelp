package co.touchlab.testhelp.concurrency

import kotlin.test.Test
import kotlin.test.assertFails

class MPWorkerTest {
    @Test
    fun exceptionsThrowOnConsume() {
        val worker = MPWorker()
        val future: MPFuture<Unit> = worker.runBackground {
            throw NullPointerException("Just a fake one")
        }

        assertFails { future.consume() }
    }

    fun exceptionsThrowFromOpsExe() {
        val ops = ThreadOperations {}
        ops.exe {
            throw NullPointerException("Just a fake one two")
        }

        assertFails { ops.run(2) }
    }
}
