import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val job = GlobalScope.launch { // launch a new coroutine in background and continue
        delay(1000L) // non-blocking delay for 1 second (default time unit is ms)
        println("World!") // print after delay
    }
    println("Hello,") // main thread continues while coroutine is delayed
    // The .join() method is a suspending function that waits for the child
    // coroutine to complete before continuing. This is much better than
    // delay-ing for an arbitrary amount of time hoping that the child
    // coroutine completed its execution.
    job.join()
    println("Completed!")
}
