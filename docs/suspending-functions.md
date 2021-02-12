# Composing suspending functions

## Table of Contents
* [Sequential by default](#sequential-by-default)
* [Concurrent using async](#Concurrent-using-async)

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
