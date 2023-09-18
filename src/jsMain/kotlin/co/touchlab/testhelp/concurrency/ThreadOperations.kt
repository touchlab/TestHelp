package co.touchlab.testhelp.concurrency

import kotlin.js.Date

actual fun currentTimeMillis(): Long = Date().getTime().toLong()
