package co.touchlab.testhelp.concurrency

// import platform.posix.usleep

actual fun sleep(time: Long) {
    val start = currentTimeMillis()

    while ((currentTimeMillis() - start) < time) {
    }
    // usleep((time * 1000).toUInt()) // this takes microseconds, so multiply by 1000 to use miliseconds.
}
