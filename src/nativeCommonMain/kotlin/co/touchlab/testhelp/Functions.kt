@file:OptIn(FreezingIsDeprecated::class)

package co.touchlab.testhelp

import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.concurrent.freeze
import kotlin.native.concurrent.isFrozen

@OptIn(ExperimentalNativeApi::class)
actual fun <T> T.freeze(): T = if (Platform.memoryModel == MemoryModel.STRICT) {
    this.freeze()
} else {
    this
}

actual val isNative: Boolean = true
actual val isMultithreaded: Boolean = true
actual val <T> T.isFrozen: Boolean
    get() = this.isFrozen
