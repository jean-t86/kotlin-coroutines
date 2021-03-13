# Coroutine context and dispatchers

## Table of Contents
* [Dispatchers and threads](#dispatchers-and-threads)
* [Unconfined vs confined dispatcher](#unconfined-vs-confined-dispatcher)

Coroutines always execute in some context represented by a value of the `CoroutineContext` type.
It is made up of various elements, mainly Job and a dispatcher.

The dispatcher is covered in this section.

## Dispatchers and threads

The coroutine context includes a coroutine dispatcher that determines what thread or threads 
the corresponding coroutine uses for its execution.

The coroutine dispatcher can confine coroutine execution to a specific thread, dispatch it to 
a thread pool, or let it run unconfined.

All coroutine builders like launch and async accept an optional CoroutineContext parameter 
that can be used to explicitly specify the dispatcher for the new coroutine and other context 
elements.

```kotlin
launch { // context of the parent, main runBlocking coroutine
    println("main runBlocking      : I'm working in thread ${Thread.currentThread().name}")
}
launch(Dispatchers.Unconfined) { // not confined -- will work with main thread
    println("Unconfined            : I'm working in thread ${Thread.currentThread().name}")
}
launch(Dispatchers.Default) { // will get dispatched to DefaultDispatcher 
    println("Default               : I'm working in thread ${Thread.currentThread().name}")
}
launch(newSingleThreadContext("MyOwnThread")) { // will get its own new thread
    println("newSingleThreadContext: I'm working in thread ${Thread.currentThread().name}")
}
```
The output of this program will be
```
Unconfined            : I'm working in thread main
Default               : I'm working in thread DefaultDispatcher-worker-1
newSingleThreadContext: I'm working in thread MyOwnThread
main runBlocking      : I'm working in thread main
```
<br />

> The default dispatcher that is used when coroutines are launched in GlobalScope is 
> represented by Dispatchers.Default and uses a shared background pool of threads, so 
> launch(Dispatchers.Default) { ... } uses the same dispatcher as GlobalScope.launch { ... }.

> newSingleThreadContext creates a thread for the coroutine to run. A dedicated thread is a 
> very expensive resource. In a real application it must be either released, when no longer 
> needed, using the close function, or stored in a top-level variable and reused throughout 
> the application.

## Unconfined vs confined dispatcher

The `Dispatchers.Unconfined` coroutine dispatcher starts a coroutine in the caller thread, but
only until the first suspension point, i.e. until a suspend method is encountered. 

After suspension, it resumes the coroutine in the thread that is fully determined by the suspending 
function that was invoked, i.e. if the suspend function switches to a different thread, when the 
coroutine resumes after executing the suspend function, the coroutine will execute in the same thread
as the called suspend function. In that sense, the dispatcher's execution is `unconfined`.

The unconfined dispatcher is appropriate for coroutines which neither consume CPU time not update
any shared (like UI) confined to a specific thread.

> On the other side, the dispatcher is inherited from the outer CoroutineScope by default, i.e. if 
> unspecified. 

> The default dispatcher from the `runBlocking` coroutine, in particular, is confined to the invoker
> thread, so inheriting it has the effect of confining execution to this thread with predictable FIFO
> scheduling. In simple words, using runBlocking on the main thread will use main within the coroutine
> for example.

```kotlin
fun main() = runBlocking<Unit> {
    launch(Dispatchers.Unconfined) { // not confined -- will work with main thread
        println("Unconfined      : I'm working in thread ${Thread.currentThread().name}")
        delay(500)
        println("Unconfined      : After delay in thread ${Thread.currentThread().name}")
    }
    launch { // context of the parent, main runBlocking coroutine
        println("main runBlocking: I'm working in thread ${Thread.currentThread().name}")
        delay(1000)
        println("main runBlocking: After delay in thread ${Thread.currentThread().name}")
    }    
}
```
The output of this program will be
```
runBlocking     : I'm working in thread main @coroutine#1
Unconfined      : I'm working in thread main @coroutine#2
main runBlocking: I'm working in thread main @coroutine#3
Unconfined      : After delay in thread kotlinx.coroutines.DefaultExecutor @coroutine#2
main runBlocking: After delay in thread main @coroutine#3
```

So, the coroutine with the context inherited from `runBlocking {...}` continues to execute in the
`main` thread, while the unconfined one resumes in the default executor thread that the `delay` 
function is using.
