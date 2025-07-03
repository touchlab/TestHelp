package co.touchlab.testhelp

import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.concurrent.freeze
import kotlin.native.concurrent.isFrozen

@OptIn(ExperimentalNativeApi::class)
actual fun <T> T.freeze(): T = this

actual val isNative: Boolean = true
actual val isMultithreaded: Boolean = true
actual val <T> T.isFrozen: Boolean get() = false
