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

## Cooperation cancellation

All suspending functions in package `kotlinx.coroutines` are cancellable. 

They check for cancellation of coroutine and throw `CancellationException` when cancelled.

However, if a coroutine is working in a computation and does not check for cancellation, then
it cannot be cancelled.

For example, a coroutine cannot be cancelled if a while loop is executing. It can, however,
check for cancellation and prematurely exit the loop.

```kotlin
fun main() = runBlocking<Unit> {
    val startTime = System.currentTimeMillis()
    val job = launch(Dispatchers.Default) {
        var nextPrimeTime = startTime
        var i = 0
        while (i < 5) {
            // isActive is used as a cancellation guard for the coroutine
            if (!isActive) {
                break
            }
            
            if (System.currentTimeMillis() >= nextPrimeTime) {
                println("job: I'm sleeping $i")
                i++
                nextPrimeTime += 500L
            }
        }
    }

    delay(1300L)
    println("main: I'm tired of waiting!")
    job.cancelAndJoin()
    println("main: Now I can quit.")
}
```
Another way to check for cancellation is to use the method `yield()`, which checks whether
the coroutine has been cancelled, and terminates the execution if so.
