
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
//Hello
//World 1
//World 2
//Done

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
