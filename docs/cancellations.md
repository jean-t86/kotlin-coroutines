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

For example, a coroutine cannot be cancelled if a `while` loop is executing. It can, however,
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

## Closing resources with finally

Cancellable suspending functions throw `CancellationException`. This exception can be caught in
the usual way using `try{ } catch{ } finally{ }`.

```kotlin
fun main() = runBlocking {
    val job = launch {
        try {
            repeat(3) { i-> 
                println("job: I'm sleeping $i...")
                delay(500L)
            }
        } finally {
            println("job: I'm running finally.")
        }
    }
    delay(1300L)
    println("main: I'm tired of waiting!")
    job.cancelAndJoin() // cancels the job and waits for its completion
    println("main: Now I can quit.")
}
```
The output will be:
```
job: I'm sleeping 0...
job: I'm sleeping 1...
job: I'm sleeping 2...
main: I'm tired of waiting!
job: I'm running finally.
main: Now I can quit.
```

## Run non-cancellable block

If a coroutine block is cancelled, and a suspend function is called in the `finally` close
of an `try{} catch{} finally{}` expression, another `CancellationException` will be thrown.

This is usually not a problem since there is rarely a need to call a suspend function
in a `finally` close. However, if needed, we can wrap the suspend function call in 
`withContext(NonCancellable) {...}`

## Timeout

It is possible to cancel the execution of a coroutine based on a timeout. For example, if
a coroutine takes too long to execute, we may want to cancel the operation and move on.

This is achieved by using `withTimeout() {...}`

```kotlin
fun main() = runBlocking {
    try {
        withTimeout(1300L) {
            repeat(1000) { i ->
                println("I'm sleeping $i...")
                delay(500L)
            }
        }
    } catch (e: TimeoutCancellationException) {
        println(e.message)
    }
}
```

A `try{} catch{}` needs to be used because withTimeout is used right inside the main function.
If called within a launched coroutine code block, the timeout error will be quiet. 

This is because `TimeoutCancellationException` is a subclass of `CancellationException`. 
`CancellationException` is considered to be a normal reason for coroutine completion.

`withTimeoutOrNull` can be used if we do not want an exception to be thrown on a timeout.

## Asynchronous timeouts and resources

The timeout event in withTimeout is asynchronous with respect to the code running in its
block, i.e. it can happen at any time in the code block.

Therefore, if a resource that needs to be closed is acquired in the code block, it can be 
released in the `finally` close of a `try{} catch{}`.
