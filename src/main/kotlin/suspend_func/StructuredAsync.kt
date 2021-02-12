package suspend_func

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main() = runBlocking<Unit> {
    try {
        failedConcurrentSum()
    } catch (e: ArithmeticException) {
        println("Computation failed with ArithmeticException")
    }
}

// using coroutineScope in this way will cause this suspending function to cancel if the calling coroutine is cancelled.
suspend fun failedConcurrentSum(): Int = coroutineScope {
    // This coroutine will get cancelled when the second nested coroutine throws
    val one = async<Int> {
        try {
            delay(Long.MAX_VALUE) // Emulates very long computation
            42
        } finally {
            println("First child was cancelled")
        }
    }
    val two = async<Int> {
        println("Second child throws an exception")
        // this nested coroutine will throw and get cancelled
        throw ArithmeticException()
    }
    one.await() + two.await()
}
