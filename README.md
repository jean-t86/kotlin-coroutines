# Kotlin Coroutines

Below are notes from the official [reference documentation for Kotlin Coroutines](https://kotlinlang.org/docs/reference/coroutines/basics.html)

Coroutines are essentially light-weight threads.

They execute within a CoroutineScope. For example `GlobalScope.launch` will 
launch the execution of a coroutine within the GlobalScope.

`launch` is a coroutine builder, and `GlobalScope`is a CoroutineScope.

## Suspending functions

A suspending function is a function that does not block a thread while
executing, i.e. it is non-blocking.

Instead, it suspends the coroutine to allow subsequent code 
outside the coroutine to continue executing on the thread while it
is suspended.

Suspending functions can only be called from a coroutine or another 
suspending function.

### GlobalScope.launch

GlobalScope.launch is a coroutine builder, i.e. it provides a method that accepts a block of code
that represents the coroutine. Within that coroutine, suspending functions can be called.

```kotlin
fun main() {
    // Launches the execution of a Coroutine in the GlobalScope.
    // The code block is the actual coroutine, and suspending functions can be
    // called within the block, e.g. delay(1000L)
    GlobalScope.launch {
        // A suspending function. When called, the coroutine block will suspend
        // it's execution and allow the thread it is running on to continue 
        // executing code outside of the coroutine block, i.e. println("Hello,")
        delay(1000l)
        println("World")
    }
    // The line below will execute as soon as the coroutine suspends, i.e. when
    // delay(1000L) is reached.
    println("Hello,")
    // When this line is reached, delay(1000L) from the coroutine block had 
    // time to complete and will therefore continue executing the next line, 
    // i.e. println("World")
    Thread.sleep(2000L)
}
```
The output of this program will be 
```
Hello,
World
```

### runBlocking

runBlocking is another coroutine builder. It's idiomatic use case is to act as an adapter to
wrap either a `main()` or unit test function's code as a coroutine.

Code within a runBlocking coroutine will block the thread on which it is executing, i.e. suspending
functions will block instead of suspend the coroutine's execution.

```kotlin
fun main() = runBlocking<Unit> {
    // A coroutine code block
}

class MyTest {
    fun myUnitTest() = runBlocking<Unit> {
        // A coroutine code block 
    }
}
```

### job.join()

The GlobalScope.launch method returns an object of type `Job`. This object can be used to 
manage the execution of the coroutine. 

In the example below, `.join()` is used to wait for the coroutine to complete before 
continuing execution, i.e. it joins the background execution flow to that of the main 
execution flow.

```kotlin
fun main() = runBlocking {
    val job = GlobalScope.launch {
        delay(1000L)
        println("World")
    }
    println("Hello,")
    job.join() // main execution flow will block until child coroutine completes
}
```

### Coroutine scope

Every coroutine builder, including `runBlocking`, adds an instance of `CoroutineScope` to the 
scope of its code block.

We can launch coroutines within that scope. The advantage is that we do not have to use
`job.join()` in order to wait for a child coroutine to complete. This is because an outer coroutine
(runBlocking for example) does NOT complete until all the coroutines launched in its scope 
completes.

In other words, a parent coroutine waits for all its children coroutines to complete before
completing. This is an example of structured concurrency where:
- there are clear entry and exit points (the code block), and
- all spawned threads are ensured to complete before exiting/ continuing.

```kotlin
fun main() = runBlocking<Unit> {
    launch {
        delay(1000L)
        println("World")
    }
    println("Hello")
    // There is no need to delay the main coroutine since it will wait for its child coroutine
    // launched in its scope to complete.
}
```