package basics

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    launch {
        // Because this coroutine executes within runBlocking, execution will stop here for
        // 200ms, and will then output the message to the console.
        delay(200L)
        println("Task from runBlocking")
    }

    coroutineScope {
        launch {
            // Because coroutineScope allows coroutines to suspend, the thread will be
            // freed to continue execution to the next delay call, i.e. delay(100L).
            delay(500L)
            println("Task from nested launch")
        }

        // This code will execute while the nested coroutine delays for 500ms.
        delay(100L)
        println("Task from coroutineScope")
    }

    // This line will not execute until all coroutines have completed, i.e. runBlocking.launch
    // and runBlocking.coroutineScope.launch
    println("Coroutine scope is over")
}
