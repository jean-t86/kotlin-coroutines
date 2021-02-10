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

## Example

```kotlin
main() {
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