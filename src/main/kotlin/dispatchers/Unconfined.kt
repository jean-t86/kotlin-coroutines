package dispatchers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking<Unit> {
    println("runBlocking     : I'm working in thread ${Thread.currentThread().name}")

    // Dispatchers.Unconfined will start the coroutine in the same thread as runBlocking; in this case the main thread.
    launch(Dispatchers.Unconfined) { // not confined -- will work with main thread
        println("Unconfined      : I'm working in thread ${Thread.currentThread().name}")
        // Once it comes back from the first suspending function, the thread will be that of the one used within the
        // suspending function. In that case delay uses DefaultExecutor.
        delay(50000)
        println("Unconfined      : After delay in thread ${Thread.currentThread().name}")
    }

    // When not specified, a child coroutine inherits the CoroutineContext from its parent. In this case, that means
    // that the launched coroutine below will run on the main thread used by its containing runBlocking coroutine.
    launch { // context of the parent, main runBlocking coroutine
        println("main runBlocking: I'm working in thread ${Thread.currentThread().name}")
        // Even after coming back from delay, this coroutine will continue executing on the main thread.
        delay(1000)
        println("main runBlocking: After delay in thread ${Thread.currentThread().name}")
    }
}
