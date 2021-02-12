# Composing suspending functions

## Table of Contents
* [Sequential by default](#sequential-by-default)
* [Concurrent using async](#Concurrent-using-async)
* [Lazily started async](#lazily-started-async)

## Sequential by default

Suspending functions executing within a coroutine will do so sequentially by default.

```kotlin
suspend fun doSomethingUsefulOne(): Int {
    // ...
}

suspend fun doSomethingUsefulTwo(): Int {
    // ...
}

fun main() = runBlocking {
    val time = measureTimeMillis {
        val one = doSomethingUsefulOne()
        val two = doSomethingUsefulTwo()
        println("The answer is ${one + two}")
    }
    println("Completed in $time ms")
}
```

## Concurrent using async

A suspend function can be run concurrently using `async`. `async` is similar to `launch` with
the difference that it does not return a `Job` but instead a `Deferred`.

A `Deffered` object is a light-weight non-blocking future that represents a promise to
provide a result later. `.await()` can then be used to on the deffered variable to get
its eventual result.

```kotlin
val time = measureTimeMillis {
    val one = async { doSomethingUsefulOne() }
    val two = async { doSomethingUsefulTwo() }
    println("The answer is ${one.await() + two.await()}")
}
println("Completed in $time ms")
```

## Lazily started async

Optionally, async can be made lazy by setting its start parameter to CoroutineStart.LAZY. 
In this mode it only starts the coroutine when its result is required by await, or if its 
Job's start function is invoked.

```kotlin
val time = measureTimeMillis {
    val one = async(start = CoroutineStart.LAZY) { doSomethingUsefulOne() }
    val two = async(start = CoroutineStart.LAZY) { doSomethingUsefulTwo() }
    // some computation
    one.start() // start the first one
    two.start() // start the second one
    println("The answer is ${one.await() + two.await()}")
}
println("Completed in $time ms")
```
