
package coroutines.concepts

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlin.system.measureTimeMillis
import kotlinx.coroutines.channels.*

//______________________________________________________________________________________
//What is Concurrency?
//    Design Model

// What is Parallelism?
//    Execution Model
//______________________________________________________________________________________

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
/*
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
*/
//Cancellation is cooperative
//Coroutine cancellation is cooperative.
//
// A coroutine code has to cooperate to be cancellable.
// All the suspending functions in kotlinx.coroutines are cancellable.
//
// They check for cancellation of coroutine and throw [CancellationException]
// when cancelled. However, if a coroutine is working in
//a computation and does not check for cancellation, then it cannot be cancelled,

//______________________________________________________________________________________

/*
fun main() = runBlocking {
    val job = launch {
        try {
            // Opening Resources
            repeat(1000) { i ->
                println("Job Going To Sleep... $i")
                delay(500L)
            }
        } finally {
            // Release Resources
            println("Job: Running Finally Block")
        }
    }
    delay( 1300L )
    println("Main: ")
    job.cancelAndJoin()
    println("Main Exiting")
}
*/
//Closing resources with finally
//  Cancellable suspending functions throw [CancellationException] on cancellation
//  which can be handled in the usual way. For example, try {...} finally {...}
//  expression and Kotlin use function execute their finalization actions normally
//  when a coroutine is cancelled:

// Both [join][Job.join] and [cancelAndJoin] wait for all finalization actions to complete,

//______________________________________________________________________________________
/*
fun main() = runBlocking {
    val job = launch {
        try {
            repeat( 1000 ) { i ->
                println("Job Going To Sleep... $i")
                delay(500L)
            }
        } finally {
            withContext( NonCancellable ) {
                println("Job Is Running Finally...")
                delay(1000L)
                println("Exiting Non Cancellable Code")
            }
        }
    }
    delay( 1300L )
    println("Main: ")
    job.cancelAndJoin()
    println("Main Exiting")
}
*/

//Run non-cancellable block
// Any attempt to use a suspending function in the finally block of the previous example causes
// [CancellationException], because the coroutine running this code is cancelled.

// Usually, this is not a problem, since all well-behaving
// closing operations (closing a file, cancelling a job, or closing any kind of a
// communication channel) are usually non-blocking and do not involve any
// suspending functions.
//
// However, in the rare case when you need to suspend in a cancelled coroutine
// you can wrap the corresponding code in withContext(NonCancellable) {...} using
// [withContext] function and [NonCancellable] context

//______________________________________________________________________________________
/*
fun main() = runBlocking {
    withTimeout( 1300L ) {
        repeat( 1000 ) { i ->
            println("I Am Sleeping $i")
            delay( 500L )
        }
    }
}
*/

// The TimeoutCancellationException that is thrown by [withTimeout]
// is a subclass of [CancellationException].

// We have not seen its stack trace printed on the console before.
// That is because inside a cancelled coroutine CancellationException
// is considered to be a normal reason for coroutine completion.
// However, in this example we have used withTimeout right inside the main function.

// Since cancellation is just an exception, all resources are closed in the usual way.
// You can wrap the code with timeout in a
// try {...} catch (e: TimeoutCancellationException){...} block
// if  you need to do some additional action specifically on any kind of timeout or
// use the [withTimeoutOrNull] function  that is similar to [withTimeout]
// but returns null on timeout instead of throwing an exception:

//______________________________________________________________________________________
/*
fun main() = runBlocking {
    val result = withTimeoutOrNull( 1300L ) {
        repeat( 1000 ) { i ->
            println("I Am Sleeping $i")
            delay( 500L )
        }
        "Done"
    }

    println("Result : $result")
}
*/

//______________________________________________________________________________________

/*
fun main() = runBlocking {
    val time = measureTimeMillis {
//        val one : Deferred<Int>  = async { doSomethingUsefulOne() }
        val one     = async { doSomethingUsefulOne() }
        val two     = async { doSomethingUsefulTwo() }
        val three   = async { doSomethingUsefulThree() }
        println("Three Coroutines Launched...")
        val result1 = one.await()
        val result2 = two.await()
        val result3 = three.await()
        val result = result1 + result2 + result3 // max ( T1, T2, T3 )
        println("The Final Output: $result")
    }
    println("Completed In Time: $time")
}

suspend fun doSomethingUsefulOne() : Int { // T1
    delay( 3000L )
    return 33
}

suspend fun doSomethingUsefulTwo() : Int { // T2
    delay( 2000L )
    return 22
}

suspend fun doSomethingUsefulThree() : Int { // T3
    delay( 5000L )
    return 55
}
*/

//The Final Output: 1010
//Completed In Time: 10050

//The Final Output: 110
//Completed In Time: 5019

// Sequential by default
// We use a normal sequential invocation, because the code in the coroutine,
// just like in the regular code, is sequential by default.

//Concurrent using async
//What if there are no dependencies between invocation of doSomethingUsefulOne and
//doSomethingUsefulTwo and

//we want to get the answer fater, by doing both concurrently? This is where [async] comes to help.
//Conceptually, [async] is just like [launch]. It starts a separate coroutine which is a light-weight thread
//that works concurrently with all the other coroutines. The difference is that launch returns a [Job] and
//does not carry any resulting value, while async returns a [Deferred] — a light-weight non-blocking
// future that represents a promise to provide a result later.
//
// You can use .await() on a deferred value to get its eventual result,
// but Deferred is also a Job , so you can cancel it if needed.

//______________________________________________________________________________________
/*
fun main() = runBlocking {
    val time = measureTimeMillis {
//        val one : Deferred<Int>  = async { doSomethingUsefulOne() }
        val one     = async( start = CoroutineStart.LAZY) { doSomethingUsefulOne() }
        val two     = async( start = CoroutineStart.LAZY) { doSomethingUsefulTwo() }
        val three   = async( start = CoroutineStart.LAZY) { doSomethingUsefulThree() }
        println("Three Coroutines Launched...")

        one.start()
        two.start()
        three.start()

        val result1 = one.await()
        val result2 = two.await()
        val result3 = three.await()
        val result = result1 + result2 + result3 // max ( T1, T2, T3 )
        println("The Final Output: $result")
    }
    println("Completed In Time: $time")
}

suspend fun doSomethingUsefulOne() : Int { // T1
    delay( 3000L )
    return 33
}

suspend fun doSomethingUsefulTwo() : Int { // T2
    delay( 2000L )
    return 22
}

suspend fun doSomethingUsefulThree() : Int { // T3
    delay( 5000L )
    return 55
}
*/

//Lazily started async
//There is a laziness option to [async] using an optional start parameter with a value of
//[CoroutineStart.LAZY].
//It starts coroutine only when its result is needed by some
//[await][Deferred.await] or if a [start][Job.start] function
//is invoked.

//______________________________________________________________________________________
/*
fun main() = runBlocking {
    val time = measureTimeMillis {
//        val one : Deferred<Int>  = async { doSomethingUsefulOne() }
        val one     = doSomethingUsefulOneAsync()
        val two     = doSomethingUsefulTwo()
        val three   = doSomethingUsefulThree()
        println("Three Coroutines Launched...")

        val result1 = one.await()
        val result2 = two.await()
        val result3 = three.await()
        val result = result1 + result2 + result3 // max ( T1, T2, T3 )
        println("The Final Output: $result")
    }
    println("Completed In Time: $time")
}

fun doSomethingUsefulOneAsync() = GlobalScope.async {
    doSomethingUsefulOne()
}

suspend fun doSomethingUsefulOne() : Int { // T1
    delay( 3000L )
    return 33
}

suspend fun doSomethingUsefulTwo() : Int { // T2
    delay( 2000L )
    return 22
}

suspend fun doSomethingUsefulThree() : Int { // T3
    delay( 5000L )
    return 55
}

*/

//______________________________________________________________________________________
/*
fun main() = runBlocking {
    val time = measureTimeMillis {
        println("The Answer Is: ${ concurrentSum() } ")
    }
    println("Completed In Time: $time")
}

suspend fun concurrentSum(): Int = coroutineScope {
    val one = async { doSomethingUsefulOne() }
    val two = async { doSomethingUsefulTwo() }
    one.await() + two.await()
}

suspend fun doSomethingUsefulOne() : Int { // T1
    delay( 3000L )
    return 33
}

suspend fun doSomethingUsefulTwo() : Int { // T2
    delay( 2000L )
    return 22
}

*/
//This way, if something goes wrong inside the code of concurrentSum function
// and it throws an exception,  all the coroutines that were launched in
// its scope are cancelled.

//______________________________________________________________________________________
/*
fun main() = runBlocking {
    try {
        failedConcurrentSum()
    } catch( e: ArithmeticException ) {
        println("Caught ArithmeticException")
    }
    println("Exiting Main...")
}

suspend fun failedConcurrentSum() : Int = coroutineScope {
    val one = async<Int> {
        try {
            delay( Long.MAX_VALUE )
            44
        } finally {
            println("First Child : Cancelled...")
        }
    }

    val two = async<Int> {
        println("Second Child : Throws Exception")
        throw ArithmeticException()
    }

    one.await() + two.await()
}
*/
//Note, how both first async and awaiting parent are cancelled on the one child failure:

//______________________________________________________________________________________

// Coroutine Context and Dispatchers

//  Coroutines always execute in some context which is represented by
//  the value of CoroutineContext

// The coroutine context is a set of various elements. T
// he main elements are the [Job] of the coroutine, which we’ve seen before, and its dispatcher,

//Coroutine context includes a coroutine dispatcher (see
// [CoroutineDispatcher]) that determines what thread
//or threads the corresponding coroutine uses for its execution.
// Coroutine dispatcher can confine coroutine execution
// to a specific thread, dispatch it to a thread pool, or let it run unconfined.

/*
fun main() = runBlocking<Unit> {
    launch { // context of the parent, main runBlocking coroutine
        println("Launched Coroutine: ${ Thread.currentThread().name }")
    }

    launch( Dispatchers.Unconfined ) { // not confined -- will work with main thread
        println("Unconfined Coroutine: ${Thread.currentThread().name }")
    }

    launch( Dispatchers.Default ) { // // will get dispatched to DefaultDispatcher
        println("Default Coroutine: ${Thread.currentThread().name }" )
    }

    launch( newSingleThreadContext("MyOwnThread") ) { // will get its own new thread
        println("OwnThread Coroutine: ${Thread.currentThread().name }")
    }
}
*/

//Unconfined Coroutine: main
//Default Coroutine: DefaultDispatcher-worker-1
//Launched Coroutine: main
//OwnThread Coroutine: MyOwnThread


//When launch { ... } is used without parameters, it inherits the context (and thus dispatcher)
//from the [CoroutineScope] that it is being launched from.

//In this case, it inherits the context of the main runBlocking coroutine which runs in the main thread.

//[Dispatchers.Unconfined] is a special dispatcher that also appears to run
// in the main thread, but it is, in fact, a different mechanism that is explained later.

//The default dispatcher, that is used when coroutines are launched in [GlobalScope],
//is represented by [Dispatchers.Default] and uses shared background pool of threads,
//so launch(Dispatchers.Default) { ... } uses the same dispatcher as GlobalScope.launch { ...
//} .

//[newSingleThreadContext] creates a thread for the coroutine to run.
//A dedicated thread is a very expensive resource.

//In a real application it must be either released, when no longer needed, using [close]
//[ExecutorCoroutineDispatcher.close] function, or stored in a top-level variable
// and reused throughout the application.

//Unconfined vs confined dispatcher
//The [Dispatchers.Unconfined] coroutine dispatcher starts coroutine in the caller thread,
//but only until the first suspension point.
//
//After suspension it resumes in the thread that is fully determined by the
//suspending function that was invoked.
//
//Unconfined dispatcher is appropriate when coroutine does not
//consume CPU time nor updates any shared data (like UI) that is confined to a specific thread.
//
//On the other side, by default, a dispatcher for the outer [CoroutineScope] is inherited.
//The default dispatcher for [runBlocking] coroutine, in particular, is confined to the invoker thread,
//so inheriting it has the effect of confining execution to this thread with a predictable
//FIFO scheduling.

//______________________________________________________________________________________
/*
fun main() = runBlocking<Unit> {

    launch(Dispatchers.IO) { // context of the parent, main runBlocking coroutine
        println("Outer Launched Coroutine: ${Thread.currentThread().name}")

        launch(Dispatchers.Unconfined) { // not confined -- will work with main thread
            println("Unconfined Coroutine: ${Thread.currentThread().name}")
            delay(500L)
            println("Unconfined Coroutine: ${Thread.currentThread().name}")
        }
        println("Outer Launched Coroutine: ${Thread.currentThread().name}")
    }
        launch { // context of the parent, main runBlocking coroutine
            println("Launched Coroutine: ${ Thread.currentThread().name }")
            delay( 1000L )
            println("Launched Coroutine: ${ Thread.currentThread().name }")
        }
}
*/
// NOTE ::
//Unconfined dispatcher is an advanced mechanism that can be helpful in certain corner cases where
//dispatching of coroutine for its execution later is not needed or produces undesirable side-effects,
//because some operation in a coroutine must be performed right away.
//
//Unconfined dispatcher should not be used in general code.

//______________________________________________________________________________________

/*
fun log( message: String ) = println("[ ${Thread.currentThread().name} ] $message")

@DelicateCoroutinesApi
fun main() {
    newSingleThreadContext("Thread_Context_01").use { context1 ->
        newSingleThreadContext("Thread_Context_02").use { context2 ->
            runBlocking(context1) {
                log("Started In Context_01")
                // data
                withContext( context2 ) {
                    log("Working In Context_02")
                }
                log("Back To Context_01")
            }
        }
    }
}
*/
//[ Context_01 ] Started In Context_01
//[ Context_02 ] Working In Context_02
//[ Context_01 ] Back To Context_01

//______________________________________________________________________________________
/*
fun log( message: String ) = println("[ ${Thread.currentThread().name} ] $message")

fun main() = runBlocking( CoroutineName("MainRunBlocking") ) {
    launch( CoroutineName("LaunchCoroutine") + Dispatchers.Default ) {

    }
}
*/

//______________________________________________________________________________________

// Channels
// Deferred values provide a convenient way to transfer a single value between coroutines.
// Channels provide a way to transfer a stream of values.

//Channel basics
//A [Channel] is conceptually very similar to BlockingQueue.
// One key difference is that instead of a blocking put operation
// it has a suspending [send][SendChannel.send], and instead of
// a blocking take operation it has a suspending [receive][ReceiveChannel.receive].
/*
fun main() = runBlocking { // MC: Consumer
    val channel = Channel<Int>()

    launch { // C1: Producer
        for ( x in 1..5 ) {
            channel.send( x * x )
        }
    }

//    repeat( 5 ) {
    repeat( 10 ) {
        println( channel.receive() )
    }
    println("Done Work!")
}
*/

//______________________________________________________________________________________
/*
fun main() = runBlocking { // MC: Consumer
    val channel = Channel<Int>()

    launch { // C1: Producer
        for ( x in 1..5 ) {
            channel.send( x * x )
        }
//        channel.close()
    }

    launch { // C2: Producer
        for ( x in 10..15 ) {
            channel.send( x * x )
        }
//        channel.close()
    }

    for ( y in channel ) println( y )
    println("Done Work!")
}
*/

//______________________________________________________________________________________

import kotlinx.coroutines.channels.*
/*
fun CoroutineScope.produceSquared() : ReceiveChannel<Int> = produce {
    for ( x in 1..10 ) send( x * x )
}

fun main() = runBlocking {
    val squares = produceSquared()
    squares.consumeEach { println(it) }
    println("Done!")
}
*/
//______________________________________________________________________________________
/*
fun CoroutineScope.produceNumbers() = produce<Int> { // Producer
    var x = 1
    while( true ) send( x++ )
}

fun CoroutineScope.sqaure( numbers: ReceiveChannel<Int> ): ReceiveChannel<Int> = produce {
    for ( x in numbers ) send( x * x )
}

fun main() = runBlocking { // Consumer
    val numbers = produceNumbers()
    val squares = sqaure( numbers )

    for( i in 1..5 ) println( squares.receive() )
    println("Done!")
    coroutineContext.cancelChildren()
}
*/
//All functions that create coroutines are defined as extensions on [CoroutineScope],
//so that we can rely on structured concurrency to make
//sure that we don’t have lingering global coroutines in our application.

//______________________________________________________________________________________
/*
fun main() = runBlocking {
    var current = numbersFrom( 2 )
    for ( i in 1..10 ) {
        val prime = current.receive()
        println( prime )
        current = filter( current, prime )
    }

    coroutineContext.cancelChildren()
}

fun CoroutineScope.numbersFrom( start: Int ) = produce<Int> {
    var x = start
    while( true ) send( x++ )
}

fun CoroutineScope.filter( numbers: ReceiveChannel<Int>, prime: Int ) = produce<Int> {
    for ( x in numbers ) if ( x % prime != 0 ) send( x )
}
*/

//______________________________________________________________________________________
/*
fun CoroutineScope.produceNumbers( ) = produce<Int> {
    var x = 1
    while( true ) {
        send( x++ )
        delay( 100 )
    }
}

fun CoroutineScope.launchProcessor( id: Int, channel: ReceiveChannel<Int> ) = launch {
    for ( message in channel ) {
        println("Processor $id Received $message")
    }
}

fun main() = runBlocking {
    val producer = produceNumbers()
    repeat( 5 ) { launchProcessor( it, producer ) }
    delay( 300 )
    producer.cancel()
}
*/

//______________________________________________________________________________________

fun main() = runBlocking<Unit> {
    val channel = Channel<Int>( 4 ) // Buffered Channel

    var sender = launch {
        repeat( 10 ) {
            println("Sending: $it")
            channel.send( it )
        }
        println("Producer...")
    }
    delay( 5000 )
    for ( y in channel ) println( channel.receive() )
    println("Consume Done!")
//    sender.cancel()
}

//______________________________________________________________________________________
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


//______________________________________________________________________________________
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


