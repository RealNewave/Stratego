import korlibs.image.color.*
import korlibs.korge.input.*
import korlibs.korge.view.*

data class Square(var piece: Piece?, val x: Float, val y: Float, val xCoord: Int, val yCoord: Int){
    val view = Container().xy(x, y)

    init {
        view.addChildAt(Container().solidRect(squareSize / 1.25, squareSize / 1.25, Colors.BROWN) {
            zIndex = 0f
        }, 0)

        view.onClick{
            if (firstSelectedSquare == null) {
                if(piece == null){
                    println("No piece on this square")
                    return@onClick
                }
                if (piece?.movement == 0) {
                    println("This piece can not move")
                    return@onClick
                }
                firstSelectedSquare = this
            }
            else if (firstSelectedSquare == this) {
                println("Unselected unit")
                firstSelectedSquare = null
            }
            else if(secondSelectedSquare == null){
                secondSelectedSquare = this
            }
        }
    }

    fun addPiece(piece: Piece){
        this.piece = piece
        view.addChild(piece.view)
    }

    fun removePiece(){
        this.piece = null
        view.removeChildAt(1)
    }

}
