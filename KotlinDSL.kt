
package learnKotlin

//_____________________________________________________________

fun buildString( buildAction: (StringBuilder) -> Unit ) : String {
    val sb = StringBuilder()
    buildAction( sb )
    return sb.toString()
}

fun createString( buildAction: StringBuilder.() -> Unit ) : String {
    val sb = StringBuilder()
    sb.buildAction( )
    return sb.toString()
}


fun playWithHoF() {
    val ss = buildString( { 
        it.append("Hello")
        it.append("  ")
        it.append("World!")
    } )

    println( ss )

    val sss = buildString { 
        it.append("Hello")
        it.append("  ")
        it.append("World!")
    }

    println( sss )

    val rr = createString { 
        this.append("Hello")
        this.append("  ")
        this.append("World!")
    }
    println( rr )

    val rrr = createString { 
        append("Hello")
        append("  ")
        append("World!")
    }
    println( rrr )

}

//_____________________________________________________________

open class Tag(val name: String) {
    private val childern = mutableListOf<Tag>()

    protected fun <T: Tag> doInit( child: T, init: T.() -> Unit ) {
        child.init()
        childern.add( child )
    }

    override fun toString() = "<$name>${childern.joinToString("")}</$name>"
}

fun table( init: TABLE.() -> Unit ) = TABLE().apply( init )

class TABLE : Tag("table") {
    fun tr( init: TR.() -> Unit ) = doInit( TR(), init )
}

class TR : Tag("tr") {
    fun td(init: TD.() -> Unit ) = doInit( TD(), init )
}

class TD : Tag("td")

fun createTable() = 
    table {
        tr {
            td {

            }
        }
    }

fun createTableAgain() = table {   
    for( i in 1..3 ) {
        tr {
            td {

            }
        }
    }
}

fun playWithHTMLDSL() {
    val html = createTable()
    println( html )

    val htmlAgain = createTableAgain()
    println( htmlAgain )
}

// Function: playWithHTMLDSL
// <table><tr><td></td></tr></table>

// <table><tr><td></td></tr><tr><td></td></tr><tr><td></td></tr></table>

//_____________________________________________________________
//_____________________________________________________________
//_____________________________________________________________
//_____________________________________________________________
//_____________________________________________________________

fun main() {
    println("\nFunction: playWithHoF")
    playWithHoF()

    println("\nFunction: playWithHTMLDSL")
    playWithHTMLDSL()

    // println("\nFunction: ")  
    // println("\nFunction: ")  
    // println("\nFunction: ")  
    // println("\nFunction: ")  
    // println("\nFunction: ")  
    // println("\nFunction: ")  
}


