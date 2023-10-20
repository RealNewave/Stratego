import korlibs.image.color.*
import korlibs.korge.input.*
import korlibs.korge.view.*

data class Piece(val rank: Int, val type: Type, val movement: Int = 1, val color: RGBA){
    val view = Container()
    val tag = Text(type.abr)
    init {
        view.addChildren(listOf(Container().solidRect(squareSize / 1.5, squareSize / 1.5, color) {
            zIndex = 0f
        }, tag))
    }
}

enum class Type(val abr: String){
    WATER("WA"),
    FLAG("FL"),
    BOMB("BO"),
    SPY("SP"),
    SCOUT("SC"),
    MINER("MI"),
    SERGEANT("SE"),
    LIEUTENANT("LI"),
    CAPTAIN("CA"),
    MAJOR("MJ"),
    COLONEL("CO"),
    GENERAL("GE"),
    MARSHALL("MR")
}
