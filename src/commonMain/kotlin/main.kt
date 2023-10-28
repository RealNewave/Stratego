import korlibs.image.color.*
import korlibs.korge.*
import korlibs.korge.view.*
import korlibs.korge.view.align.*
import korlibs.math.geom.*
import korlibs.time.*
import kotlin.math.*

const val squareSize = 80
var firstSelectedSquare: Square? = null
var secondSelectedSquare: Square? = null
val infoText = Container().xy(10, 30)
var gameOver = false

suspend fun main() = Korge(
    windowSize = Size(1440, 900),
    title = "Stratego",
    bgcolor = RGBA(253, 247, 240),
    /**
    `gameId` is associated with the location of storage, which contains `history` and `best`.
    see [Views.realSettingsFolder]
     */
    gameId = "com.devex.stratego",
    forceRenderEveryFrame = false, // Optimization to reduce battery usage!
) {

    val board = Array(10) { i -> Array(10) { j ->
            Square(null, 300 + (i.toFloat() * squareSize / 1.1f), 50 + (j.toFloat() * squareSize / 1.1f), i, j)
        }
    }

    val player1: Player = Player("Hans", Colors["#2b11ff"])
    val player2: Player = SmartAI(Colors["#ffb42c"])
    var currentPlayer = player1
    var enemyPlayer = player2
    this.addChildren(board.flatMap { it.map { square -> square.view } })
    placePiecesOnBoard(board, player1.pieces, player2.pieces)

    val playerTurnText = Container().xy(10, 10)
    playerTurnText.addChild(Text("${currentPlayer.name}'s turn", color = Colors.BLACK, textSize = 20f))
    infoText.addChild(Text("", color = Colors.ORANGERED, textSize = 20f))
    this.addChildren(listOf(playerTurnText, infoText))


    addFixedUpdater(60.timesPerSecond) {
        if (!gameOver) {
            if (currentPlayer == player2) {
//                (player2 as SmartAI).calculateNextMove()
            }
        }
    }

    addFixedUpdater(10.timesPerSecond) {
        if (!gameOver) {
            firstSelectedSquare?.let { first ->
                if (first.piece !in currentPlayer.pieces) {
                    infoText.firstChild.setText("This is not your unit")
                    resetSelected()
                    return@addFixedUpdater
                }
                secondSelectedSquare?.let { second ->
                    if (second.piece in currentPlayer.pieces) {
                        infoText.firstChild.setText("This is your own unit")
                        resetSelected()
                        return@addFixedUpdater
                    }
                    if (first.xCoord != second.xCoord &&
                        first.yCoord != second.yCoord
                    ) {
                        infoText.firstChild.setText("Only move in a straight line")
                        resetSelected()
                        return@addFixedUpdater
                    }

                    val deltaX = second.xCoord - first.xCoord
                    val deltaY = second.yCoord - first.yCoord

                    val direction = when {
                        deltaX == 0 -> {
                            if (deltaY < 0) "UP" else "DOWN"
                        }

                        deltaY == 0 -> {
                            if (deltaX > 0) "RIGHT" else "LEFT"
                        }

                        else -> {
                            "ERROR"
                        }
                    }

                    when (direction) {
                        "UP" -> {
                            var currentSquare = board[first.xCoord][first.yCoord]
                            for (i in 1..minOf(first.piece?.movement!!, abs(deltaY))) {
                                val nextSquare = board[first.xCoord][first.yCoord - i]
                                if (!isSquareFree(nextSquare, currentPlayer)) return@addFixedUpdater
                                if (!moveToNextSquare(nextSquare, currentSquare, currentPlayer, enemyPlayer)) break
                                currentSquare = nextSquare
                            }
                        }

                        "DOWN" -> {
                            var currentSquare = board[first.xCoord][first.yCoord]
                            for (i in 1..minOf(first.piece?.movement!!, deltaY)) {
                                val nextSquare = board[first.xCoord][first.yCoord + i]
                                if (!isSquareFree(nextSquare, currentPlayer)) return@addFixedUpdater
                                if (!moveToNextSquare(nextSquare, currentSquare, currentPlayer, enemyPlayer)) break
                                currentSquare = nextSquare
                            }
                        }

                        "LEFT" -> {
                            var currentSquare = board[first.xCoord][first.yCoord]
                            for (i in 1..minOf(first.piece?.movement!!, abs(deltaX))) {
                                val nextSquare = board[first.xCoord - i][first.yCoord]
                                if (!isSquareFree(nextSquare, currentPlayer)) return@addFixedUpdater
                                if (!moveToNextSquare(nextSquare, currentSquare, currentPlayer, enemyPlayer)) break
                                currentSquare = nextSquare
                            }
                        }

                        "RIGHT" -> {
                            var currentSquare = board[first.xCoord][first.yCoord]
                            for (i in 1..minOf(first.piece?.movement!!, deltaX)) {
                                val nextSquare = board[first.xCoord + i][first.yCoord]
                                if (!isSquareFree(nextSquare, currentPlayer)) return@addFixedUpdater
                                if (!moveToNextSquare(nextSquare, currentSquare, currentPlayer, enemyPlayer)) break
                                currentSquare = nextSquare
                            }
                        }

                        "ERROR" -> {
                            resetSelected()
                            infoText.firstChild.setText("Invalid movement")
                            return@addFixedUpdater
                        }
                    }
                    var index = 0
                    var x = 100
                    player1.lostPieces.forEach { piece ->
                        if(index == 10){
                            index = 0
                            x+= 50
                        }
                        piece.visible = true
                        piece.view.xy(x,50 + 50 * index)
                        piece.pieceView.size(squareSize /1.5f, squareSize/1.5f)
                        addChild(piece.view)
                        index++
                    }
                    player2.lostPieces.forEachIndexed { index, piece ->
                        piece.visible = true
                        piece.view.xy(1100,50 + 50 * index)
                        piece.pieceView.size(squareSize /1.5f, squareSize/1.5f)
                        addChild(piece.view)
                    }
                    resetSelected()
                    currentPlayer = enemyPlayer.also { enemyPlayer = currentPlayer }
                    switchView(board, currentPlayer, enemyPlayer)
                    infoText.firstChild.setText("")
                    playerTurnText.firstChild.setText("${currentPlayer.name}'s turn")
                }
            }
        }
    }
}

private fun switchView(board: Array<Array<Square>>, currentPlayer: Player, enemyPlayer: Player) {
    for (y in 0 .. 9) {
        for (x in 0..9) {
            board[y][x].piece?.let{
                if(it in currentPlayer.pieces) it.showPiece()
                else if(it in enemyPlayer.pieces && it.visible) it.showPiece()
                else it.hidePiece()
            }
        }
    }
}

private fun isSquareFree(nextSquare: Square, currentPlayer: Player): Boolean {
    if (nextSquare.piece in currentPlayer.pieces) {
        infoText.firstChild.setText("Your own unit is in the way")
        resetSelected()
        return false
    }
    return true
}

private fun resetSelected() {
    firstSelectedSquare?.view?.getChildAt(0)?.colorMul = Colors.BROWN
    firstSelectedSquare = null
    secondSelectedSquare = null
}

private fun moveToNextSquare(
    nextSquare: Square,
    currentSquare: Square,
    currentPlayer: Player,
    enemyPlayer: Player
): Boolean {
    return if (nextSquare.piece == null) {
        currentSquare.piece?.let { nextSquare.addPiece(it) }
        currentSquare.removePiece()
        true
    } else {
        currentSquare.piece?.visible = true
        nextSquare.piece?.visible = true
        val hasWonConfrontation = confrontPiece(currentSquare.piece, currentPlayer, nextSquare.piece, enemyPlayer)

        if(hasWonConfrontation == null){
            currentSquare.removePiece()
            nextSquare.removePiece()
            false
        }
        else if (hasWonConfrontation) {
            nextSquare.removePiece()
            currentSquare.piece?.let { nextSquare.addPiece(it) }
            currentSquare.removePiece()
            false
        } else {
            currentSquare.removePiece()
            false
        }
    }
}

fun placePiecesOnBoard(squares: Array<Array<Square>>, player1Pieces: MutableList<Piece>, player2Pieces: MutableList<Piece>) {
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



fun confrontPiece(playerPiece: Piece?, player: Player, enemyPiece: Piece?, enemy: Player): Boolean? {

    if (enemyPiece == null || playerPiece == null) throw RuntimeException("Can not confront non-existing pieces")

    if (enemyPiece.type === Type.WATER) {
        infoText.firstChild.setText("Can not go through water")
    }

    when {
        enemyPiece.type === Type.FLAG -> {
            infoText.firstChild.setText("This is the flag! " + player.name + " wins!")
            gameOver = true
            return true
        }

        playerPiece.type === Type.MINER && enemyPiece.type === Type.BOMB -> {
            enemy.removePiece(enemyPiece)
            return true
        }

        playerPiece.type === Type.MARSHALL && enemyPiece.type === Type.SPY -> {
            player.removePiece(playerPiece)
            return false
        }

        playerPiece.type.rank == enemyPiece.type.rank -> {
            enemy.removePiece(enemyPiece)
            player.removePiece(playerPiece)
            return null
        }

        playerPiece.type.rank < enemyPiece.type.rank -> {
            player.removePiece(playerPiece)
            return false
        }

        else -> {
            enemy.removePiece(enemyPiece)
            return true
        }
    }
}
