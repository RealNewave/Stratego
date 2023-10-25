import korlibs.image.color.*

class SmartAI(color: RGBA): Player("SmartAI", color) {
    private val guessingBoard = Array(10) { i ->
        Array(10) { j ->
            Square(
                null,
                300 + (i.toFloat() * squareSize / 1.1f),
                50 + (j.toFloat() * squareSize / 1.1f),
                i,
                j
            )
        }
    }
    val humanPlayer = Player("Human", Colors.BLUE)
    init {
        placePiecesOnBoard(guessingBoard, humanPlayer.pieces, pieces)
    }

    fun calculateNextMove(): Array<Int>{

        //find enemy lowest accessible piece
        for (y in 10 downTo 0) {
            for (x in 0..9) {
                if(guessingBoard[y][x].piece in humanPlayer.pieces){
                    //find closest own highest piece

                    /*return min(dist(points[0], points[1]),
               dist(points[0], points[2]),
               dist(points[1], points[2]))*/
                    guessingBoard[y][x]
                }
            }
        }

        // move piece

        val x1 = 0
        val y1 = 0
        val x2 = 0
        val y2 = 0
        return arrayOf(x1,y1,x2,y2)
    }

}
