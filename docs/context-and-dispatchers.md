# Coroutine context and dispatchers

## Table of Contents
* [Dispatchers and threads](#dispatchers-and-threads)

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

> The default dispatcher that is used when coroutines are launched in GlobalScope is 
> represented by Dispatchers.Default and uses a shared background pool of threads, so 
> launch(Dispatchers.Default) { ... } uses the same dispatcher as GlobalScope.launch { ... }.

> newSingleThreadContext creates a thread for the coroutine to run. A dedicated thread is a 
> very expensive resource. In a real application it must be either released, when no longer 
> needed, using the close function, or stored in a top-level variable and reused throughout 
> the application.

## Unconfined vs confined dispatcher
