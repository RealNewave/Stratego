import korlibs.image.color.*
import korlibs.korge.view.*
import korlibs.korge.view.align.*

data class Piece(val type: Type, val movement: Int = 1, val color: RGBA, var visible: Boolean = false){
    val view = Container()
    val tag = Text("${type.rank}")

    init {
        tag.zIndex = 1f
        tag.colorMul = Colors.BLACK
        tag.fontSize = 30f
        view.addChild(tag)
    }


    fun setSelectedColor(){
        view.lastChild?.visible(true)
        view.lastChild?.prevSibling?.visible(false)
    }
    fun resetColor(){
        view.lastChild?.visible(false)
        view.lastChild?.prevSibling?.visible(true)
    }

    fun showPiece(){
        tag.setText("${type.rank}")
        tag.centerOn(view)
    }

    fun hidePiece() {
        tag.setText("?")
        tag.centerOn(view)

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
