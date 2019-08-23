package co.touchlab.testhelp

expect fun Throwable.printStackTrace()

expect fun <T> T.freeze(): T

expect val multithreaded:Boolean