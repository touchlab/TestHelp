package co.touchlab.testhelp

import kotlin.native.concurrent.freeze
import kotlin.native.concurrent.isFrozen

actual fun Throwable.printStackTrace() {
    printStackTrace()
}

actual fun <T> T.freeze(): T = this.freeze()
actual val isNative: Boolean = true
actual val isMultithreaded: Boolean = true
actual val <T> T.isFrozen: Boolean
    get() = this.isFrozen
