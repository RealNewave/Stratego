import korlibs.image.color.*
import korlibs.korge.*
import korlibs.korge.view.*
import korlibs.math.geom.*
import korlibs.time.*

val squareSize = 50
var firstSelectedSquare: Square? = null
var secondSelectedSquare: Square? = null

suspend fun main() = Korge(
    windowSize = Size(800, 600),
    title = "Stratego",
    bgcolor = RGBA(253, 247, 240),
    /**
    `gameId` is associated with the location of storage, which contains `history` and `best`.
    see [Views.realSettingsFolder]
     */
    gameId = "com.devex.stratego",
    forceRenderEveryFrame = false, // Optimization to reduce battery usage!
) {

    val board = Array(10) { i ->
        Array(10) { j -> Square(null, 150 + (i.toFloat() * squareSize), 50 + (j.toFloat() * squareSize), i, j) }
    }


    val player1 = Player("Hans", Colors.BLUE)
    val player2 = Player("Jasmijn", Colors.GOLD)
    var currentPlayer = player1
    var enemyPlayer = player2
    this.addChildren(board.flatMap { it.map { square -> square.view } })
    placePiecesOnBoard(board, player1, player2)

    val textContainer = Container().xy(10, 10)
    textContainer.addChild(Text("${currentPlayer.name}'s turn", color = Colors.BLACK))
    this.addChild(textContainer)


    addFixedUpdater(10.timesPerSecond) {
        if(firstSelectedSquare != null) {
            if (!currentPlayer.pieces.contains(firstSelectedSquare?.piece)) {
                println("This is not your unit")
                firstSelectedSquare = null
                secondSelectedSquare = null
                return@addFixedUpdater
            }
            if (secondSelectedSquare != null) {
                if (firstSelectedSquare?.xCoord != secondSelectedSquare?.xCoord &&
                    firstSelectedSquare?.yCoord != secondSelectedSquare?.yCoord
                ) {
                    println("Only move in a straight line")
                    firstSelectedSquare = null
                    secondSelectedSquare = null
                    return@addFixedUpdater
                }
//                val moves = firstSelectedSquare?.piece?.movement
                //TODO: handle multiple squares
//                for (it in 0 until moves!!) {
                    if (secondSelectedSquare?.piece == null) {
                        secondSelectedSquare?.addPiece(firstSelectedSquare?.piece!!)
                        firstSelectedSquare?.removePiece()
                    } else {
                        val hasWonConfrontation = confrontPiece(firstSelectedSquare!!.piece!!, currentPlayer, secondSelectedSquare!!.piece!!, enemyPlayer)
                        if (hasWonConfrontation) {
                            secondSelectedSquare?.removePiece()
                            secondSelectedSquare?.addPiece(firstSelectedSquare!!.piece!!)
                            firstSelectedSquare!!.removePiece()
                        } else {
                            firstSelectedSquare!!.removePiece()
                        }
//                        break
//                    }
                }
                firstSelectedSquare = null
                secondSelectedSquare = null
                currentPlayer = enemyPlayer.also { enemyPlayer = currentPlayer}
                textContainer.firstChild.setText("${currentPlayer.name}'s turn")
            }
        }


    }

    /*
    *  0  1  2  3  4  5  6  7  8  9
    * 10 11 12 13 14 15 16 17 18 19
    * 20 21 22 23 24 25 26 27 28 29
    *
    */


//    squares.forEach { square ->
//        square.view.onClick {
//                if (validateMove(square, currentPlayer)) {
//                    playerUnitSquare = null
//                    currentPlayer = if (currentPlayer == player1) player2 else player1
//                    textContainer.firstChild.setText("${currentPlayer.name}'s turn")
//                    return@onClick
//                }
//                val squareIndex = squares.indexOf(square)
//                val selectedIndex = squares.indexOf(playerUnitSquare)
//                val handleConflict = confrontPiece(playerUnitSquare!!.piece!!, player1, square.piece!!, player2)
//                if (handleConflict) {
//                    square.removePiece()
//                    square.setPiece(playerUnitSquare!!.piece!!, playerUnitSquare!!.piece!!.view)
//                    playerUnitSquare!!.removePiece()
//                } else {
//                    playerUnitSquare!!.removePiece()
//                }
//                playerUnitSquare = null
//                currentPlayer = if (currentPlayer == player1) player2 else player1
//                textContainer.firstChild.setText("${currentPlayer.name}'s turn")
//
//            }
//        }
//    }
}

fun placePiecesOnBoard(squares: Array<Array<Square>>, player1: Player, player2: Player) {

    var index = 0
    for (i in 0 until 4) {
        for (j in 0..9) {
            val piece = player1.pieces[index]
            squares[j][i].addPiece(piece)
            index++
        }
    }
    index = 0
    for (i in 6 until 10) {
        for (j in 0..9) {
            val piece = player2.pieces[index]
            squares[j][i].addPiece(piece)
            index++
        }
    }
}

fun confrontPiece(playerPiece: Piece, player: Player, enemyPiece: Piece, enemy: Player): Boolean {


    if (enemyPiece.type === Type.WATER) {
        println("Can not go through water")
    }

    when {
        enemyPiece.type === Type.FLAG -> {
            println("This is the flag! " + player.name + " wins!")
            return true
        }

        playerPiece.type === Type.MINER && enemyPiece.type === Type.BOMB -> {
            enemy.pieces.remove(enemyPiece)
            return true
        }

        playerPiece.type === Type.MARSHALL && enemyPiece.type === Type.SPY -> {
            player.pieces.remove(playerPiece)
            return false
        }

        playerPiece.rank == enemyPiece.rank -> {
            enemy.pieces.remove(enemyPiece)
            player.pieces.remove(playerPiece)
            return false
        }

        playerPiece.rank < enemyPiece.rank -> {
            player.pieces.remove(playerPiece)
            return false
        }

        else -> {
            enemy.pieces.remove(enemyPiece)
            return true
        }
    }
}


