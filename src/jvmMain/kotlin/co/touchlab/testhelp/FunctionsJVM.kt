package co.touchlab.testhelp

actual fun Throwable.printStackTrace(){
    printStackTrace()
}

actual fun <T> T.freeze(): T = this

actual val isNative: Boolean = false
actual val isMultithreaded: Boolean = true

actual val <T> T.isFrozen: Boolean
    get() = false