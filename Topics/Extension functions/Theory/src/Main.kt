// You can experiment here, it won’t be checked

fun main(args: Array<String>) {
  val size1 = Size(10,10)
    val size2 = Size(15,15,size1)
}

class Size(val width: Int, val height: Int) {
    var area: Int = width * height

    constructor(width: Int, height: Int, outerSize: Size) : this(width, height) {
        outerSize.area -= this.area
        println("Updated outer object's area is equal to ${outerSize.area}")
    }
}
