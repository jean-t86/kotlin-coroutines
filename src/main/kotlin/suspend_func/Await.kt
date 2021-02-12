package suspend_func

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

fun main() = runBlocking {
    val time = measureTimeMillis {
        // Both suspending functions are called within async concurrently, i.e. without blocking or suspending.
        val one = async { doSomethingUsefulOne() }
        val two = async { doSomethingUsefulTwo() }
        // await is used to suspend while waiting for the result of the suspend function call
        println("The answer is ${one.await() + two.await()}")
    }
    println("Completed in $time ms")
}
