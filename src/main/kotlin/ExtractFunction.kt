import kotlinx.coroutines.*

fun main() = runBlocking {
    launch {
        doWorld()
    }
    println("Hello,")
}

suspend fun doWorld() {
    delay(1000L)
    println("World")
}