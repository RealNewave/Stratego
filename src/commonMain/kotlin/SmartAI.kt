import korlibs.image.color.*
import kotlinx.coroutines.*
import kotlin.math.*

class SmartAI(color: RGBA) : Player("SmartAI", color) {
    val guessingBoard = Array(10) { i ->
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

    fun calculateNextMove(): Triple<Piece, Int, Piece> {

        val result = runBlocking {
            val pieceDistances = mutableListOf<List<Triple<Piece, Int ,Piece>>>()
                coroutineScope {
                    (0..9).forEach { y1 ->
                        launch {
                        (0..9).forEach { x1 ->
                            guessingBoard[y1][x1].piece?.let {
                                if (it in humanPlayer.pieces) {
                                    //find closest own highest piece

                                    pieceDistances.add(findAiPiece(it, y1, x1))
                                }
                            }
                        }
                    }
                }
            }
            return@runBlocking pieceDistances
        }
        return result.flatMap { entry ->
            entry.filter { triple ->
                val aiPiece = triple.first
                val distance = triple.second
                val humanPiece = triple.third
                humanPiece.type.rank < aiPiece.type.rank
            }
        }.minBy { it.second }

        //find enemy lowest accessible piece


        // move piece
//
//        val x1 = 0
//        val y1 = 0
//        val x2 = 0
//        val y2 = 0
//        return arrayOf(x1, y1, x2, y2)
    }

    private fun findAiPiece(humanPiece: Piece, y1: Int, x1: Int): List<Triple<Piece, Int, Piece>> {
        return (0..9).flatMap { y2 ->
                (0..9).mapNotNull { x2 ->
                guessingBoard[y2][x2].piece?.let {
                    if (it in pieces) {
                        val distanceScore =  (abs(y1 - y2) + abs(x1 - x2))
                        Triple(it, distanceScore, humanPiece)
                    } else null
                }
            }
        }
    }

}
