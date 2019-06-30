package co.touchlab.testhelp.karmok

import co.touchlab.testhelp.freeze
import kotlin.reflect.KProperty

class MockRecorder<RT> {
    val emptyList = emptyList<Any?>().freeze()

    private var invokedCount = AtomicInt(0)
    private var invokedParametersList = AtomicReference<List<Any?>>(emptyList)
    private var stubbedError = AtomicReference<Throwable?>(null)
    private var stubbedResult = AtomicReference<RT?>(null)

    val called : Boolean
        get() = invokedCount.value > 0

    val calledCount : Int
        get() = invokedCount.value

    fun throwOnCall(t:Throwable){
        stubbedError.value = t.freeze()
    }

    fun returnOnCall(rt:RT){
        stubbedResult.value = rt.freeze()
    }

    fun invokeUnit(args:List<Any?>){
        invokedCount.incrementAndGet()

        //Threads are tough
        while (true){
            val lastList = invokedParametersList.value
            val newList = ArrayList(lastList)
            newList.add(args)
            if(invokedParametersList.compareAndSet(lastList, newList.freeze()))
                break
        }

        if(stubbedError.value != null)
            throw stubbedError.value!!
    }

    fun invoke(args:List<Any?>):RT{
        invokeUnit(args)
        if(stubbedResult.value != null)
            return stubbedResult.value!!

        throw NullPointerException()
    }

    fun invoke():RT = invoke(emptyList)
}

class MockDelegate<RT> {
    private val stubbedResult = AtomicReference<RT?>(null)

    operator fun getValue(thisRef: Any?, property: KProperty<*>): RT {
        return stubbedResult.value!!
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: RT) {
        stubbedResult.value = value.freeze()
    }
}

expect class AtomicInt(initialValue: Int) {
    fun get(): Int
    fun set(newValue: Int)
    fun incrementAndGet(): Int
    fun decrementAndGet(): Int

    fun addAndGet(delta: Int): Int
    fun compareAndSet(expected: Int, new: Int): Boolean
}

internal var AtomicInt.value
    get() = get()
    set(value) {
        set(value)
    }

expect class AtomicReference<V>(initialValue: V) {
    fun get(): V
    fun set(value_: V)

    /**
     * Compare current value with expected and set to new if they're the same. Note, 'compare' is checking
     * the actual object id, not 'equals'.
     */
    fun compareAndSet(expected: V, new: V): Boolean
}

internal var <T> AtomicReference<T>.value: T
    get() = get()
    set(value) {
        set(value)
    }
