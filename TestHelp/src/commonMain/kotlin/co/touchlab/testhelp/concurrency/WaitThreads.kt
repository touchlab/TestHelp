package co.touchlab.testhelp.concurrency

import co.touchlab.stately.concurrency.AtomicInt
import co.touchlab.stately.concurrency.value
import co.touchlab.stately.freeze
import co.touchlab.testhelp.printStackTrace

/**
 * Busy loops thread till all are done or timeout. Would be better to suspend, but
 * this is simpler for now.
 */
class WaitThreads(private val threadCount: Int, private val timeout:Long, private val exBlock:(Throwable)->Unit = {it.printStackTrace()}){
    private val finishedCount = AtomicInt(0)

    init {
        freeze()
    }

    fun <R> wait(block:()->R):R{
        val result = try {
            block()
        } catch (t:Throwable){
            exBlock(t)
            throw t
        } finally {
            finishedCount.incrementAndGet()
        }

        val start = currentTimeMillis()
        val waitTill = start + timeout

        while (threadCount > finishedCount.value){
            if(currentTimeMillis() >= waitTill){
                throw IllegalStateException("Thread wait timeout")
            }
            sleep(100)
        }

        return result
    }
}