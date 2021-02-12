# Composing suspending functions

## Table of Contents
* [Sequential by default](#sequential-by-default)
* [Concurrent using async](#Concurrent-using-async)
* [Lazily started async](#lazily-started-async)
* [Async-style functions](#async-style-functions)
* [Structured concurrency with async](#structured-concurrency-with-async)

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

## Async-style functions

A suspending function can have its code block wrapped in a `GlobalScope.async` method call
in order to make the whole function async. In that case, await NEEDS to be used to obtain
the result of the function call.

```kotlin
// The result type of somethingUsefulOneAsync is Deferred<Int>
fun somethingUsefulOneAsync() = GlobalScope.async {
    doSomethingUsefulOne()
}

// The result type of somethingUsefulTwoAsync is Deferred<Int>
fun somethingUsefulTwoAsync() = GlobalScope.async {
    doSomethingUsefulTwo()
}

// note that we don't have `runBlocking` to the right of `main` in this example
fun main() {
    val time = measureTimeMillis {
        // we can initiate async actions outside of a coroutine
        val one = somethingUsefulOneAsync()
        val two = somethingUsefulTwoAsync()
        // but waiting for a result must involve either suspending or blocking.
        // here we use `runBlocking { ... }` to block the main thread while waiting for the result
        runBlocking {
            println("The answer is ${one.await() + two.await()}")
        }
    }
    println("Completed in $time ms")
}
```

> Note that these xxxAsync functions are not suspending functions. 
> They can be used from anywhere. However, their use always implies 
> asynchronous (here meaning concurrent) execution of their action 
> with the invoking code.
> `await`, however, is a suspending function.

## Structured concurrency with async

When using `GlobalScope.async`, we run the suspending functions in the global scope. This
becomes problematic if, for example, the calling coroutine throws an error while the 
suspending function is running in the background.

In such a case, the coroutine, and all its children coroutines running in its scope, will
can cancelled, and the program execution will move on. However, the suspending function in
the background will continue execution because it is in the global scope.

We can solve this by using `coroutineScope` to inherit the scope from the calling coroutine.

```kotlin
suspend fun concurrentSum(): Int = coroutineScope {
    val one = async { doSomethingUsefulOne() }
    val two = async { doSomethingUsefulTwo() }
    one.await() + two.await()
}

val time = measureTimeMillis {
    println("The answer is ${concurrentSum()}")
}
println("Completed in $time ms")
```
