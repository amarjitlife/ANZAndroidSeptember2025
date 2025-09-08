
package learnKotlin

import java.util.Comparator
import java.io.File

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

enum class Colour(val r: Int, val g: Int, val b: Int) {
    RED(255, 0, 0), GREEN(0, 255, 0), BLUE(0, 0, 255)
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

// Program: Proof
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

class User( val id: Int, val name: String, val address: String )

fun saveUser( user: User ) {
	// Validation
	if ( user.name.isEmpty() ) {
		throw IllegalArgumentException("Can't Save User: ${user.id}: Empty Name ")
	}

	if ( user.address.isEmpty() ) {
		throw IllegalArgumentException("Can't Save User: ${user.id}: Empty Address")
	}

	// Saving User
	println("Saving User: ${user.id}")
}

fun playwithSaveUser() {
	val gabbar = User( 420 , "Gabbar", "Ramgrah")
	saveUser( gabbar )
}

//_____________________________________________________________

val something  = 99

fun saveUserOnceAgain( user: User ) { // Outside Context
	// Validation Logic
	//		Local Function: Function Defined Inside Function
	// fun validate( user: User, value: String, field: String ) {
	fun validate( value: String, field: String ) { // Inside Context 
		if ( value.isEmpty() ) { 
			throw IllegalArgumentException("Can't Save User: ${user.id}: Empty $field")
		}
	}

	validate( user.name, "Name" )
	validate( user.address, "Address" )

	// Saving User
	println("Saving User: ${user.id}")
}

fun playwithSaveUserAgain() {
	val gabbar = User( 420 , "Gabbar", "Ramgrah")
	saveUserOnceAgain( gabbar )
}

//_____________________________________________________________

fun User.save( ) { // Outside Context
	// Validation Logic
	//		Local Function: Function Defined Inside Function
	// fun validate( user: User, value: String, field: String ) {
	val user = this
	fun validate( value: String, field: String ) { // Inside Context 
		if ( value.isEmpty() ) { 
			throw IllegalArgumentException("Can't Save User: ${user.id}: Empty $field")
		}
	}

	validate( user.name, "Name" )
	validate( user.address, "Address" )

	// Local Class: Class Defined Inside A Function
	class SomeLocal { // Inside Context
		val something: String = "Unknown"
		val some: Int = 999
	}

	val someLocal = SomeLocal()
	println( someLocal.something )
	// Saving User
	println("Saving User: ${user.id}")
}

fun playwithSaveUserMore() {
	val gabbar = User( 420 , "Gabbar", "Ramgrah")
	gabbar.save()
}

//_____________________________________________________________

class Car1( val carName: String ) { // Outside Context
	val something = "Hello"
	fun doSomething() { println( something ) }

	// In Kotlin:
	//		By Default Class Defined Inside Other Class Are Nested Classes
	//		Nested Classes Doesn't Capture Outside Context.

	// In Java:
	//		By Default Class Defined Inside Other Class Are Inner Classes
	//		Inner Classes Capture Outside Context.

	// Nested Class: Class Defined Inside Other Class
	class Engine( val engineName: String ) { // Inside Context
		override fun toString(): String {
			return "$engineName"
			// return "$engineName In $carName"
		}
	}	
}

class Car2( val carName: String ) { // Outside Context
	val something = "Hello"
	fun doSomething() { println( something ) }
	// In Kotlin:
	//		By Default Class Defined Inside Other Class Are Nested Classes
	//		Nested Classes Doesn't Capture Outside Context.

	// In Java:
	//		By Default Class Defined Inside Other Class Are Inner Classes
	//		Inner Classes Capture Outside Context.

	// Inner Class: Class Defined Inside Other Class
	inner class Engine( val engineName: String ) { // Inside Context
		override fun toString(): String {
			// return "$engineName"
			return "$engineName In $carName"
		}
	}	
}

// KotlinClasses.kt:197:28: error: unresolved reference 'carName'.
// 			return "$engineName In $carName"
fun playWithNestedAndInnerClasses() {
	val mazda1 = Car1( "Mazda" )
	val mazdaEngine1 = Car1.Engine("Fiat")

	println( mazdaEngine1 )

	val mazda2 = Car2( "Mazda" )
	val mazdaEngine2 = mazda2.Engine("Fiat")

	println( mazdaEngine2 )
}

//_____________________________________________________________
// DESIGN APPROACH

// 	1. Start With Enums
//	2. Create Function
//	3. Final Classes
//	4. Sealed Classes
//	5. Full Classes :: This Should Be Last Choice

// VALUE DRIVEN PROGRAMMING

//_____________________________________________________________

// Singleton Classes
object India {
	val some = "Bharat!"

	fun getName(): String { return some }
}

fun playWithIndia() {
	println( India.some )
	println( India.getName() )
}

//_____________________________________________________________

// import java.util.Comparator
// import java.io.File

object CaseInsensitiveFileComparator: Comparator<File> {
	override fun compare( file1: File, file2: File ) : Int {
		return file1.path.compareTo( file2.path, ignoreCase = true )
	}
}

fun playWithObjectClasses() {
	println( CaseInsensitiveFileComparator.compare(
		File("/User"), File("/user")) )

	val files = listOf( 		File("/User"), File("/user") )
	println( files.sortedWith( CaseInsensitiveFileComparator ) )
}


//_____________________________________________________________

data class Person( val name: String ) {
	object NameComparator : Comparator< Person > {
		override fun compare( person1: Person, person2: Person ) : Int {
			return person1.name.compareTo( person2.name )
		}
	}
}

fun playWithObjectClassesAgain() {
	val persons = listOf( Person("Bob"), Person("Gabbar"), Person("Alice") )
	println( persons.sortedWith( Person.NameComparator ) )
}

//_____________________________________________________________
// Static Use Cases

// 1. Single Instance : Singleton 
//			object Keyword	

// 2. Factory Methods
//			Methods Binded With Type Class
//			companion object

// 3. static final For Creating Constants
//			Not Required

// 4. Common State Shared Across Objects
//			Not Required

// 5. Syncronisation Static Keyword
//		Not Required

//_____________________________________________________________


// class Gandhi {
// 		
// }

// class Congress {
// 		static Gandhi gandhi = new Gandhi()
// }
//

//_____________________________________________________________

class A {
	companion object {
		// Used To Create Type/Class Level Member
		fun bar() {
			println("Companion Object: Called")
		}
	}
}

fun playWithCompanionObjects() {
	// Accessing Type/Class Member Using Class Name
	A.bar()
}

//_____________________________________________________________

fun getFacebookName( accountID: Int ) = "FB:$accountID"

//				Making Constructor Private
class UserAgain private constructor( val nickname: String ) {
	companion object {
		// Factory Methods: To Create Object With Given Configuration
		fun newSubscribingUser( email: String ) = UserAgain( email.substringBefore('@') )
		fun newFacebookUser( accountID: Int ) 	= UserAgain( getFacebookName( accountID ) )
	}
}

fun playWithCompanionObjectsAgain() {
	val subscribingUser = UserAgain.newSubscribingUser("gabbar@gmail.com")
	val facebookUser = UserAgain.newFacebookUser(420)

	println( subscribingUser.nickname )
	println( facebookUser.nickname )
}

//_____________________________________________________________

/*
class Human {
	HumanState state 
}

// Utility Classes
	class HumanState {
		final static something = ""
		final static somethingAgain = ""

		static fun somethingCommon() {

		}
	}

// Working With Utilities
	HumanState.doSomething()
	HumanState.something
	HumanState.something

// Alternative Possible
// Utility Module .kt Files
	val something = 
	val soemthing Again = 

	fun somethingCommon() {
		
	}
*/

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

	println("\nFunction: playwithSaveUser")
	playwithSaveUser()

	println("\nFunction: playwithSaveUserAgain")
	playwithSaveUserAgain()

	println("\nFunction: playwithSaveUserMore")
	playwithSaveUserMore()

	println("\nFunction: playWithNestedAndInnerClasses")
	playWithNestedAndInnerClasses()

	println("\nFunction: playWithIndia")
	playWithIndia()

	println("\nFunction: playWithObjectClassesAgain")
	playWithObjectClassesAgain()

	println("\nFunction: playWithCompanionObjects")
	playWithCompanionObjects()

	println("\nFunction: playWithCompanionObjectsAgain")	
	playWithCompanionObjectsAgain()

	// println("\nFunction: ")	
	// println("\nFunction: ")	
	// println("\nFunction: ")	
}
