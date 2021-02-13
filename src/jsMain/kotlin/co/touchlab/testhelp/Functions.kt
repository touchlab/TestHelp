package co.touchlab.testhelp

actual fun <T> T.freeze(): T = this
actual val isNative: Boolean = false
actual val isMultithreaded: Boolean = false

actual val <T> T.isFrozen: Boolean
    get() = false
