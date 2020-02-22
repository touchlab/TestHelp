package co.touchlab.testhelp.concurrency

import kotlin.test.Test
import kotlin.test.assertEquals

class HelperTest {
    @Test
    fun backgroundTest(){
        val result = background {
            SomeData("hey")
        }
        assertEquals(result, SomeData("hey"))
    }
}

data class SomeData(val s:String)