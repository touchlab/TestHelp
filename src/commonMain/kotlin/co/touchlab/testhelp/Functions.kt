package co.touchlab.testhelp

expect fun Throwable.printStackTrace()

expect fun <T> T.freeze(): T

/**
 * Are we in on a native platform?
 */
expect val isNative: Boolean

expect val isMultithreaded: Boolean

/**
 * Determine if object is frozen. Will return true on non-native platforms, which for logic is generally the outcome
 * you want.
 */
val <T> T.isNativeFrozen: Boolean
    get() = !isNative || isFrozen

/**
 * Determine if object is frozen. Will return false on non-native platforms.
 */
expect val <T> T.isFrozen: Boolean
