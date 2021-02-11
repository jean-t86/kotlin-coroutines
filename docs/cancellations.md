# Cancellations and Timeouts

## Cancelling coroutine execution

The `launch` function returns a `Job` object that can be used to cancel the execution of a 
coroutine.

For example, this would be useful if a user closes a view that launched a coroutine on
start-up. Since the user is navigating away from that view, the result of the coroutine might
not be needed anymore.

Instead of allowing the coroutine to complete its full execution, `.cancel()` can be used
to cancel it.

```kotlin
fun main() = runBlocking {
    val job = launch {
        repeat(1000) { i -> 
            println("job: I'm sleeping $i...")
            delay(500L)
        }
    }
    
    delay(3000L)
    println("main: I'm tired of waiting!")
    // Instead of the next two lines, we can use the extension function cancelAndJoin()
    job.cancel()
    job.join()
    println("main: Now I can quit.")
}
```

The code above will produce the following output:
```
job: I'm sleeping 0 ...
job: I'm sleeping 1 ...
job: I'm sleeping 2 ...
main: I'm tired of waiting!
main: Now I can quit.
```
