import korlibs.image.color.*
import korlibs.image.format.*
import korlibs.io.file.std.*
import korlibs.korge.view.*
import korlibs.korge.view.align.*
import korlibs.time.*
import kotlinx.coroutines.*
import kotlin.math.*
import kotlin.random.*

class GuessAI(color: RGBA) : Player("GuessAI", color) {
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
    private val humanPlayer = Player("Human", Colors.BLUE)
    private var bestMoves: List<Triple<Piece, Int, Piece>> = mutableListOf()
    private var timesCalled = 0
    init {
        placePiecesOnBoard(guessingBoard, humanPlayer.pieces, pieces)
    }

    private fun placePiecesOnBoard(squares: Array<Array<Square>>, player1Pieces: MutableList<Piece>, player2Pieces: MutableList<Piece>) {
        var index = 0
        for (i in 0 until 4) {
            for (j in 0..9) {
                val piece = player1Pieces[index]
                squares[j][i].addPiece(piece)
                index++
            }
        }
        index = 0
        for (i in 6 until 10) {
            for (j in 0..9) {
                val piece = player2Pieces[index]
                piece.tag.setText("?")
                piece.tag.centerOn(piece.view)
                squares[j][i].addPiece(piece)
                index++
            }
        }
    }

    fun calculateNextMove(): Triple<Piece, Int, Piece> {
        timesCalled++
        if(bestMoves.isNotEmpty()) {
            return bestMoves[timesCalled]
        }
        val result = runBlocking {
            val pieceDistances = mutableListOf<List<Triple<Piece, Int, Piece>>>()
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
        bestMoves = result.flatMap { entry ->
            entry.filter { (aiPiece, distance, humanPiece) ->
                humanPiece.type.rank < aiPiece.type.rank
            }.sortedBy { it.second }
        }
        return bestMoves.first()

    }
    fun makeNextMove(aiPiece: Piece, humanPiece: Piece): Array<Int> {
        val ownPiece =
            guessingBoard.flatMap { squareList -> squareList.filter { it.piece == aiPiece } }.first()

        val enemyPiece =
            guessingBoard.flatMap { squareList -> squareList.filter { it.piece == humanPiece } }.first()

        val x1 = ownPiece.xCoord
        val y1 = ownPiece.yCoord
        var x2 = enemyPiece.xCoord
        var y2 = enemyPiece.yCoord
        if(x1 != x2 && y1 != y2){
            if(Random.nextBoolean()) x2 = x1 else y2 = y1
        }
        return arrayOf(x1, y1, x2, y2)
    }

    fun updateGuessMove(x1: Int, y1: Int, x2: Int, y2: Int) {
        guessingBoard[x2][y2].piece = guessingBoard[x1][y1].piece
        invalidateBestMove()
    }
    fun updateGuessConfront(hasWonConfrontation: Boolean?, x1: Int, y1: Int, x2: Int, y2: Int){
        val currentSquare = guessingBoard[y1][x1]
        val nextSquare = guessingBoard[y2][x2]
        if(hasWonConfrontation == null){
            currentSquare.removePiece()
            nextSquare.removePiece()
        } else if (hasWonConfrontation) {
            nextSquare.removePiece()
            currentSquare.piece?.let { nextSquare.addPiece(it) }
            currentSquare.removePiece()
        } else {
            currentSquare.removePiece()
        }
        invalidateBestMove()
    }

    private fun invalidateBestMove() {
        bestMoves = mutableListOf()
        timesCalled = 0
    }

    private fun findAiPiece(humanPiece: Piece, y1: Int, x1: Int): List<Triple<Piece, Int, Piece>> {
        //find enemy lowest accessible piece
        return (0..9).flatMap { y2 ->
            (0..9).mapNotNull { x2 ->
                guessingBoard[y2][x2].piece?.let {
                    if (it in pieces) {
                        val distanceScore = (abs(y1 - y2) + abs(x1 - x2))
                        Triple(it, distanceScore, humanPiece)
                    } else null
                }
            }
        }
    }

}
