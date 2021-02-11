package cancellations

import kotlinx.coroutines.*

fun main() = runBlocking<Unit> {
    val startTime = System.currentTimeMillis()
    val job = launch(Dispatchers.Default) {
        var nextPrimeTime = startTime
        var i = 0
        while (i < 5) {
            yield()

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
