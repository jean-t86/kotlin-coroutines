import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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

fun idiomaticUseCase() = runBlocking<Unit> {
    // runBlocking here is used as an adapter to wrap the function's code in a coroutine.
    // This is helpful to use with main() or for writing unit tests, e.g.
    /**
     * class MyTest {
     *  @Test
     *  fun testMySuspendingFunction() = runBlocking {
     *      ...
     *  }
     * }
     */
}
