package dispatchers

import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")

@ObsoleteCoroutinesApi
fun main() {
    // newSingleThreadContext is now marked as obsolete in the coroutine API
    newSingleThreadContext("Ctx1").use { ctx1 ->
        // The method use is a way to release resources that require a clean up after being used. Another example
        // would be to close a file once done with it.
        newSingleThreadContext("Ctx2").use { ctx2 ->
            // runBlocking can be passed an explicit context
            runBlocking(ctx1) {
                log("Started in ctx1")
                // With context changes the context of the coroutine while still staying within the same coroutine.
                withContext(ctx2) {
                    log("Working in ctx2")
                }
                log("Back to ctx1")
            }
        }
    }
}
