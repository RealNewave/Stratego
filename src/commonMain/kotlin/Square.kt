import korlibs.image.color.*
import korlibs.korge.input.*
import korlibs.korge.view.*
import korlibs.korge.view.align.*

data class Square(var piece: Piece?, val x: Float, val y: Float, val xCoord: Int, val yCoord: Int){
    val view = Container().xy(x, y)
    init {
        view.addChildAt(Container().solidRect(squareSize / 1.25, squareSize / 1.25, Colors.BROWN) {
            zIndex = 0f
        }, 0)

        view.onClick{
            handleClick()
        }
    }

    fun handleClick(): Boolean {
        if (firstSelectedSquare == null) {
            if(piece == null){
                infoText.firstChild.setText("No piece on this square")
                return false
            }
            if (piece?.movement == 0) {
                infoText.firstChild.setText("This piece can not move")
                return false
            }
            firstSelectedSquare = this
            view.getChildAt(0).colorMul = Colors.GREEN
            return true
        }
        if (firstSelectedSquare == this) {
            infoText.firstChild.setText("Unselected unit")
            view.getChildAt(0).colorMul = Colors.BROWN
            firstSelectedSquare = null
            return false
        }
        if(secondSelectedSquare == null){
            secondSelectedSquare = this
            return true
        }
        return false
    }

    fun addPiece(piece: Piece){
        this.piece = piece
        view.addChild(piece.view)
        piece.view.centerOn(view)
    }

    fun removePiece(){
        this.piece = null
        view.removeChildAt(1)
    }

}
