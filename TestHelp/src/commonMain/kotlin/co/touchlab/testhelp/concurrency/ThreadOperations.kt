package co.touchlab.testhelp.concurrency

import co.touchlab.testhelp.freeze

expect class MPWorker(){
    fun <T> runBackground(backJob:()->T):MPFuture<T>
    fun requestTermination()
}

expect class MPFuture<T>{
    fun consume():T
}

fun createWorker():MPWorker = MPWorker()

expect fun sleep(time:Long)

class ThreadOperations<T>(val producer:()->T){
    private val exes = mutableListOf<(T)->Unit>()
    private val tests = mutableListOf<(T)->Unit>()
    var lastRunTime = 0L

    fun exe(proc:(T)->Unit){
        exes.add(proc)
    }

    fun test(proc:(T)->Unit){
        tests.add(proc)
    }

    fun run(threads:Int, randomize:Boolean = false):T{

        if(randomize){
            exes.shuffle()
            tests.shuffle()
        }

        exes.freeze()

        val target = producer()
        val start = currentTimeMillis()

        val workers= Array(threads){MPWorker()}
        for(i in 0 until exes.size){
            val ex = exes[i]
            workers[i % workers.size]
                .runBackground {
                    ex(target)
                }
        }
        workers.forEach { it.requestTermination() }

        tests.forEach { it(target) }

        lastRunTime = currentTimeMillis() - start

        return target
    }
}

expect fun currentTimeMillis(): Long