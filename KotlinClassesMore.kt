//_____________________________________________________________

// Secondary Constructors
open class Shape1 {
    // Secondary Constructors
    constructor(size: Int) {
        // ...
    }
    // Secondary Constructors
    constructor(size: Int, color: String) : this(size) {
        // ...
    }
}

class Circle1 : Shape1 {
    // Secondary Constructors
    constructor(size: Int) : super(size) {
        // ...
    }

    // Secondary Constructors
    constructor(size: Int, color: String) : super(size, color) {
        // ...
    }
}

//_____________________________________________________________

enum class Colour { RED, GREEN, BLUE, WHITE, BLACK }

open class Shape {
    var boundaryColor: Colour
    var fillColor : Colour

	// Delegating Initialisation To Major Constructor
    constructor( ): this(boundaryColor = Colour.BLACK, fillColor = Colour.WHITE ) {
         // this.boundaryColor  = Colour.BLACK
         // this.fillColor      = Colour.WHITE
    }
    // Delegating Initialisation To Major Constructor
    constructor( boundaryColor : Colour ) : this( boundaryColor, Colour.WHITE) {}
    
    // Major Constructor
    constructor( boundaryColor : Colour, fillColor: Colour ) {
    	this.boundaryColor = boundaryColor
    	this.fillColor = fillColor
    }

    override fun toString() = "Shape(boundaryColor=$boundaryColor, fillColor=$fillColor)"
}

class Circle : Shape {
    var radius : Int 

	// Delegating Initialisation To Major Constructor
    constructor( boundaryColor : Colour ) : this( boundaryColor, Colour.WHITE, 0 ) { }
    
    // Delegating Initialisation To Major Constructor
    constructor( boundaryColor : Colour, fillColor : Colour ) : this( boundaryColor, fillColor, 0 ) { }

    // Major Constructor
    constructor( boundaryColor : Colour, fillColor : Colour, radius : Int ) : super(boundaryColor, fillColor) {
        this.radius = radius    
    }

    override fun toString() = "Circle(boundaryColor=$boundaryColor, fillColor=$fillColor, radius=$radius)"   
}

fun playWithShapes() {
    val shape = Shape( Colour.RED, Colour.GREEN )
    println( shape )

    val circle = Circle( Colour.RED, Colour.GREEN, 99 )
    println( circle )
}

//_____________________________________________________________
