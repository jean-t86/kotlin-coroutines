# Composing suspending functions

## Table of Contents
* [Sequential by default](#sequential-by-default)

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
