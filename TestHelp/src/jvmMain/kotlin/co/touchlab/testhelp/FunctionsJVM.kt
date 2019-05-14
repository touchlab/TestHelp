package co.touchlab.testhelp

actual fun Throwable.printStackTrace(){
    printStackTrace()
}

actual fun <T> T.freeze(): T = this