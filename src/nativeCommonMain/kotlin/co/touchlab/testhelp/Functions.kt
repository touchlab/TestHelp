package co.touchlab.testhelp

import kotlin.native.concurrent.freeze

actual fun Throwable.printStackTrace(){
    printStackTrace()
}

actual fun <T> T.freeze(): T = this.freeze()