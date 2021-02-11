import kotlinx.coroutines.*

fun main() {
    GlobalScope.launch { // launch a new coroutine in background and continue
        delay(1000L) // non-blocking delay for 1 second (default time unit is ms)
        println("World!") // print after delay
    }
    println("Hello,") // main thread continues while coroutine is delayed
    // launch a new coroutine that executes in a blocking fashion.
    // Invoking runBlocking blocks until the coroutine inside runBlocking
    // completes.
    runBlocking {
        delay(1000L);
    }
}