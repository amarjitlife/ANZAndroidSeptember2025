
package coroutines.concepts

import kotlinx.coroutines.*

//______________________________________________________________________________________
//What is Concurrency?
//    Multiple Tasks Overlapping
//    Need Not Be At Same Time
//    How You Manage More Than Task?
//    Design Model

// What is Parallism?
//    Simenltanously
//    Performing?
//    Execution Model
/*
fun main() = runBlocking { // this: CoroutineScope
    launch { // launch a new coroutine and continue
        delay(1000L) // non-blocking delay for 1 second (default time unit is ms)
        println("World!") // print after delay
    }
    println("Hello") // main coroutine continues while a previous one is delayed
}
*/

//Hello
//World!

//______________________________________________________________________________________

/*
fun main() = runBlocking { // this: CoroutineScope :: MC: Parent Coroutine
//fun main() { // this: CoroutineScope
//    GlobalScope.launch { // C1 : Child Coroutine
    launch { // C1: Child
        doWorld()
    }
    println("Hello")
}

// this is your first suspending function
suspend fun doWorld() {
    delay(1000L)
    println("World!")
}
*/

//______________________________________________________________________________________
/*
// Sequentially executes doWorld followed by "Done"
fun main() = runBlocking { // MC : Parent
    doWorld()
    println("Done")
}
// Concurrently executes both sections
suspend fun doWorld() = coroutineScope { // this: CoroutineScope
    launch { // C1 : Child
        delay(2000L)
        println("World 2")
    }
    launch { // C2: Child
        delay(1000L)
        println("World 1")
    }
    println("Hello")
}
*/
//Hello
//World 1
//World 2
//Done

// [delay] is a special suspending function that does not block a thread,
// but suspends coroutine and it can be only used from a coroutine.

// main thread invoking runBlocking blocks until the coroutine inside runBlocking completes.

// Here runBlocking<Unit> { ... } works as an adaptor that is used to start the top-level
// main coroutine.

// We explicitly specify its Unit return type, because a well-formed main function in
// Kotlin has to return Unit .

// When we use GlobalScope.launch , we create a top-level coroutine.
// Even though it is light-weight, it still  consumes some memory resources while it runs.
//
// If we forget to keep a reference to the newly launched coroutine it still runs.
// What if the code in the coroutine hangs (for example, we erroneously delay for too long),
// what if we launched too many coroutines and ran out of memory?

// Having to manually keep references to all the launched coroutines and
// [join][Job.join] them is error-prone.
//
// We can use structured concurrency in our code.  Instead of launching coroutines
// in the [GlobalScope], just like we usually do with threads (threads are always global)

// we have main function that is turned into a coroutine using [runBlocking] coroutine builder.

// Every coroutine builder, adds an instance of [CoroutineScope] to the scope of its code block.
// including runBlocking, launch etc.

//We can launch coroutines in this scope without having to join them explicitly, because
//an outer coroutine ( runBlocking in our example) does not complete until all the coroutines launched
//in its scope complete.

// In addition to the coroutine scope provided by different builders,
// Tt is possible to declare your own scope using [coroutineScope] builder.
//
//>>> It creates a coroutine scope and does not complete until
//>>> all launched children complete.
//
// The main difference between [runBlocking]
// and [coroutineScope] is that the latter does not block the current thread
// while waiting for all children to complete.

//______________________________________________________________________________________
/*
fun main() = runBlocking { // MC
    launch { // C1
        delay( 200L )
        println("Task From Run Blcoking")
    }

    coroutineScope {
        launch { // C2
            delay( 300L )
            println("First Coroutine")
        }

        launch { // C3
            delay( 500L )
            println("Second Coroutine")
        }

        delay( 100L )
        println("Exiting Coroutine Scope")
    }

    println("Exiting RunBlocking")
}
*/
//Exiting Coroutine Scope
//Task From RunBlcoking
//First Coroutine
//Second Coroutine
//Exiting RunBlocking

//______________________________________________________________________________________
/*
fun main() = runBlocking {
    repeat( 100_000 ) {
        launch {
            delay( 100L )
            print(".")
        }
    }
    println("main Coroutine Exits")
}
*/

//______________________________________________________________________________________
/*
fun main() = runBlocking {
    GlobalScope.launch {
            repeat( 100_000 ) {
                delay( 500L )
                print(".")
        }
    }
    delay( 1300L )
    println("main Coroutine Exits")
}
*/
// Active coroutines that were launched in [GlobalScope] do not keep the process alive.
// They are like daemon threads.

//______________________________________________________________________________________
/*
fun main() = runBlocking {
    val job = launch {
        repeat(1000) { i ->
            println("Job: I'm Sleeping: $i...")
            delay( 500L)
        }
    }

    delay(1300L)
    println("I Am Tired Of Waiting...")
    job.cancel()
    job.join()
    println("Main Exiting...")
}
*/
///**
// * Returns `true` if this job was cancelled for any reason,
// either by explicit invocation of [cancel] or
// * because it had failed or  parent was cancelled.

// >>>> its child

// * In the general case, it does not imply that the
// * job has already [completed][isCompleted],
// because it may still be finishing whatever it was doing and
// * waiting for its [children] to complete.
// *
// * See [Job] documentation for more details on cancellation and failures.
// */
// public val isCancelled: Boolean

//______________________________________________________________________________________
/*
fun main() = runBlocking {
    val startTime = System.currentTimeMillis()
    val job = launch( Dispatchers.Default ) {
        var nextPrintTime = startTime
        var i = 0
        while ( i < 5 ) { // GOVERN BY POLICY
            // IMPORTANT WORK
            if ( System.currentTimeMillis() >= nextPrintTime ) {
                i++
                println("Job: Going To Sleep... ${i}")
                nextPrintTime += 500L
            }
        }
    }
    delay( 1300L )
    println("Getting Ready... Exiting Main...")
    job.cancelAndJoin()
    println("Exiting Main..." )
}
*/

//______________________________________________________________________________________

fun main() = runBlocking {
    val startTime = System.currentTimeMillis()
    val job = launch( Dispatchers.Default ) {
        var nextPrintTime = startTime
        var i = 0
        while ( isActive ) { // Important Wonk Done!
            if ( System.currentTimeMillis() >= nextPrintTime ) {
                i++
                println("Job: Going To Sleep... ${i}")
                nextPrintTime += 500L
            }
        }
    }
    delay( 1300L )
    println("Getting Ready... Exiting Main...")
    job.cancelAndJoin()
    println("Exiting Main..." )
}

//______________________________________________________________________________________
//______________________________________________________________________________________
//______________________________________________________________________________________
//______________________________________________________________________________________

/*
fun main() {
    println("\nFunction: ")
//    println("\nFunction: ")
//    println("\nFunction: ")
//    println("\nFunction: ")
//    println("\nFunction: ")
//    println("\nFunction: ")
//    println("\nFunction: ")
//    println("\nFunction: ")
//    println("\nFunction: ")
}

*/


//______________________________________________________________________________________

// fun main() {
//      f1()
//      f2()
// }
//
// fun f1() {
//      f11()
//      f12()
// }
//
// fun f2() {
//      f21()
//      f22()
//      f23()
// }

//______________________________________________________________________________________

// { // L, C0
//    { // L1, C1
//        { // L11, C11
//
//        }
//
//        { // L12, C12
//
//        }
//    }
//
//    { // L2, C2
//        { // L21, C21
//
//        }
//        { // L22, C22
//
//        }
//        { // L23, C23
//
//        }
//    }
//}

//______________________________________________________________________________________


