import korlibs.image.color.*
import korlibs.korge.input.*
import korlibs.korge.view.*
import korlibs.korge.view.align.*

data class Piece(val type: Type, val movement: Int = 1, val color: RGBA, var visible: Boolean = false){
    val view = Container()
    val tag = Text(type.abr + "|" + type.rank)
    val pieceView = Container().solidRect(squareSize / 1.2, squareSize / 1.2, color){ zIndex = 0f }

    init {
        tag.zIndex = 1f
        view.addChildren(listOf( pieceView, tag))
        tag.centerOn(pieceView)
    }

    fun setSelectedColor(){
        pieceView.colorMul = Colors.GREEN
    }
    fun resetColor(){
        pieceView.colorMul = color
    }

    fun showPiece(){
        tag.setText(type.abr + "|" + type.rank)
        tag.centerOn(pieceView)
    }

    fun hidePiece() {
        tag.setText("?")
        tag.centerOn(pieceView)
    }
}

enum class Type(val abr: String, val rank: Int){
    WATER("WA", -1),
    FLAG("FL", 0),
    SPY("SP", 1),
    SCOUT("SC", 2),
    MINER("MI", 3),
    SERGEANT("SE", 4),
    LIEUTENANT("LI", 5),
    CAPTAIN("CA", 6),
    MAJOR("MJ", 7),
    COLONEL("CO", 8),
    GENERAL("GE", 9),
    MARSHALL("MR", 10),
    BOMB("BO", 12)
}
