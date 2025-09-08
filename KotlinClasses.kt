
package learnKotlin

//_____________________________________________________________

open class View {
    open fun click() = println("View clicked")
}

class Button: View() {
    override fun click() = println("Button clicked")
    fun magic() = println("Button Magic")
}

// KotlinClasses.kt:10:15: error: this type is final, so it cannot be extended.
// class Button: View() {
//               ^^^^
// KotlinClasses.kt:11:5: error: 'click' in 'View' is final and cannot be overridden.
//     override fun click() = println("Button clicked")

fun View.showOff() = println("I'm a view!")
fun Button.showOff() = println("I'm a button!")

fun noOverridingForExtensionFunctions1() {
    val vo: View = Button()
    vo.click()
    vo.showOff()

    val bo: Button = Button()
    bo.click()
    bo.showOff()
    bo.magic()
}

//_____________________________________________________________
// DESIGN PRINCIPLE
//		Design Towards Determinism Rather Than Non Determinism
//		Avoid Writing Contextual Code

enum class Color {
    RED, GREEN, BLUE, ORANGE
}

// when Is Type Safe Expression
fun getMnemonic(color: Color) = when (color) {
        Color.RED       -> "Red Color"
        Color.GREEN     -> "Green Color"
 // 	error: 'when' expression must be exhaustive. Add the 'BLUE' branch or an 'else' branch.
        Color.BLUE 		-> "Blue Color"
        Color.ORANGE 	-> "Orange Color"
        // else 		-> "Uknown Color"
}

fun playWithColors() {
	println( getMnemonic( Color.RED ) )
	println( getMnemonic( Color.GREEN ) )	
	println( getMnemonic( Color.BLUE ) )	
}


//_____________________________________________________________
// DESIGN PRINCIPLE
//		Exceptions Are Not Exceptional Such That It Breaks Your Design

// Theorem
// Type System
// Expr, Num, Sum Type

interface Expr
class Num(val value: Int) : Expr
class Sum(val left: Expr, val right: Expr) : Expr

// Proof
fun evaluate(e: Expr): Int = when (e) {
    is Num  ->	e.value
    is Sum  ->	evaluate(e.right) + evaluate(e.left)
    // error: 'when' expression must be exhaustive. Add an 'else' branch.
    else 	->	throw IllegalArgumentException("Unknown expression")
    // is Expr -> 99
}

fun playWithEvaluate() {
    println("\nFunction: evalWhen")
    println( evaluate(Sum(Num(1), Num(2))))    
}

//_____________________________________________________________

sealed class Expr1 {
	class Num(val value: Int) : Expr1()
	class Sum(val left: Expr1, val right: Expr1) : Expr1()
}

// Proof
fun evaluateAgain(e: Expr1): Int = when (e) {
    is Expr1.Num  ->	e.value
    is Expr1.Sum  ->	evaluateAgain(e.right) + evaluateAgain(e.left)
    // error: 'when' expression must be exhaustive. Add an 'else' branch.
    // else 	->	throw IllegalArgumentException("Unknown expression")
}

fun playWithEvaluateAgain() {
    println("\nFunction: evaluateAgain")
    println( evaluateAgain( Expr1.Sum( Expr1.Num(1), Expr1.Num(2)) ) )    
}

//_____________________________________________________________
//_____________________________________________________________
//_____________________________________________________________
//_____________________________________________________________
//_____________________________________________________________
//_____________________________________________________________


fun main() {
	println("\nFunction: playWithColors")
	playWithColors()

	println("\nFunction: playWithEvaluate")
	playWithEvaluate()
	
	println("\nFunction: playWithEvaluateAgain")
	playWithEvaluateAgain()

	// println("\nFunction: ")
	// println("\nFunction: ")
	// println("\nFunction: ")
	// println("\nFunction: ")
	// println("\nFunction: ")
	// println("\nFunction: ")
	// println("\nFunction: ")	
}
