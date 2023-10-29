import korlibs.datastructure.*
import korlibs.image.color.*
import korlibs.image.format.*
import korlibs.io.file.std.*
import korlibs.korge.*
import korlibs.korge.input.*
import korlibs.korge.ui.*
import korlibs.korge.view.*
import korlibs.korge.view.align.*
import korlibs.math.geom.*
import korlibs.time.*
import kotlin.math.*

const val squareSize = 80
var firstSelectedSquare: Square? = null
var secondSelectedSquare: Square? = null
val infoText = Container().xy(610, 30)
val playerTurnText = Container().xy(660, 10)
var gameOver = false
lateinit var currentPlayer: Player
lateinit var enemyPlayer: Player
lateinit var board: Array<Array<Square>>
lateinit var player1: Player
lateinit var player2: Player

suspend fun main() = Korge(
    windowSize = Size(1440, 900),
    virtualSize = Size(1440, 800),
    title = "Stratego",
    bgcolor = RGBA(253, 247, 240),
    /**
    `gameId` is associated with the location of storage, which contains `history` and `best`.
    see [Views.realSettingsFolder]
     */
    gameId = "com.devex.stratego",
    forceRenderEveryFrame = false, // Optimization to reduce battery usage!
) {
    val restartButton = uiButton("Restart")
    restartButton.alignLeftToLeftOf(this.stage, 20)
    restartButton.alignTopToTopOf(this.stage, 20)
    restartButton.size(squareSize * 1.5, squareSize * 1.0)
    restartButton.textSize = 20f
    restartButton.onClick {
        this.removeChildrenIf { index, child -> index > 3 }
        initGame()
    }

    val quitButton = uiButton("Quit")
    quitButton.alignLeftToLeftOf(this.stage, 20)
    quitButton.alignTopToTopOf(this.stage, 140)
    quitButton.size(squareSize * 1.5, squareSize * 1.0)
    quitButton.textSize = 20f
    quitButton.onPress {
        views.gameWindow.close()
    }

    this.addChildren(listOf(playerTurnText, infoText, restartButton, quitButton))


    initGame()

    addFixedUpdater(60.timesPerSecond) {
        if (!gameOver) {
            if (currentPlayer == player2) {
                val guessAI = player2 as GuessAI
                val bestMove = guessAI.calculateNextMove()
                val nextMove = guessAI.makeNextMove(bestMove.first, bestMove.third)
                val firstClick = board[nextMove[0]][nextMove[1]].handleClick()
                if(firstClick) {
                    val secondClick = board[nextMove[2]][nextMove[3]].handleClick()
                    return@addFixedUpdater
                }
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

                    val deltaX = second.xCoord - first.xCoord
                    val deltaY = second.yCoord - first.yCoord

                    val direction = when {
                        deltaX != 0 && deltaY != 0 -> {
                            "ERROR"
                        }
                        deltaX == 0 -> {
                            if (deltaY < 0) "UP" else "DOWN"
                        }

                        deltaY == 0 -> {
                            if (deltaX > 0) "RIGHT" else "LEFT"
                        }

                        else -> {""}
                    }

                    var currentSquare = board[first.xCoord][first.yCoord]
                    when (direction) {
                        "UP" -> {
                            for (i in 1..minOf(first.piece?.movement!!, abs(deltaY))) {
                                val nextSquare = board[first.xCoord][first.yCoord - i]
                                val moveInDirection = moveInDirection(currentSquare, nextSquare, currentPlayer, enemyPlayer)
                                if(moveInDirection == null) return@addFixedUpdater
                                else if(!moveInDirection) break
                                else currentSquare = nextSquare
                            }
                        }

                        "DOWN" -> {
                            for (i in 1..minOf(first.piece?.movement!!, deltaY)) {
                                val nextSquare = board[first.xCoord][first.yCoord + i]
                                val moveInDirection = moveInDirection(currentSquare, nextSquare, currentPlayer, enemyPlayer)
                                if(moveInDirection == null) return@addFixedUpdater
                                else if(!moveInDirection) break
                                else currentSquare = nextSquare
                            }
                        }

                        "LEFT" -> {
                            for (i in 1..minOf(first.piece?.movement!!, abs(deltaX))) {
                                val nextSquare = board[first.xCoord - i][first.yCoord]
                                val moveInDirection = moveInDirection(currentSquare, nextSquare, currentPlayer, enemyPlayer)
                                if(moveInDirection == null) return@addFixedUpdater
                                else if(!moveInDirection) break
                                else currentSquare = nextSquare
                            }
                        }

                        "RIGHT" -> {
                            for (i in 1..minOf(first.piece?.movement!!, deltaX)) {
                                val nextSquare = board[first.xCoord + i][first.yCoord]
                                val moveInDirection = moveInDirection(currentSquare, nextSquare, currentPlayer, enemyPlayer)
                                if(moveInDirection == null) return@addFixedUpdater
                                else if(!moveInDirection) break
                                else currentSquare = nextSquare
                            }
                        }
                        "ERROR" -> {
                            infoText.firstChild.setText("Only move in a straight line")
                            resetSelected()
                            return@addFixedUpdater
                        }
                    }

                    showLostPieces(player1, 100)
                    showLostPieces(player2, 1100)
                    resetSelected()
                    currentPlayer = enemyPlayer.also { enemyPlayer = currentPlayer }
//                    switchView(board, currentPlayer, enemyPlayer)
                    infoText.firstChild.setText("")
                    playerTurnText.firstChild.setText("${currentPlayer.name}'s turn")
                }
            }
        }
    }
}

private suspend fun Stage.initGame() {
    val xySize = squareSize / 1.1f
    board = Array(10) { i -> Array(10) { j ->
        Square(null, (i.toFloat() * xySize), (j.toFloat() * xySize), i, j)
        }
    }

    val boardContainer = Container()

    player1 = Player("Player", Colors["#2b11ff"])
    player2 = GuessAI(Colors["#ffb42c"])
    currentPlayer = player1
    enemyPlayer = player2
    boardContainer.addChildren(board.flatMap { it.map { square -> square.view } })
    placePiecesOnBoard(board, player1.pieces, player2.pieces)
    this.addChild(boardContainer)
    playerTurnText.addChild(Text("${currentPlayer.name}'s turn", color = Colors.BLACK, textSize = 30f))
    infoText.addChild(Text("", color = Colors.ORANGERED, textSize = 30f))
    playerTurnText.centerOn(this.stage)
    playerTurnText.alignTopToTopOf(this.stage)
    infoText.centerOn(this.stage)
    infoText.alignTopToBottomOf(playerTurnText)
    boardContainer.centerOn(this.stage)
    boardContainer.alignTopToBottomOf(infoText)

    gameOver = false

}

fun moveInDirection(currentSquare: Square, nextSquare: Square, currentPlayer: Player, enemyPlayer: Player): Boolean? {
    if (!isSquareFree(nextSquare, currentPlayer)) return null
    if (!moveToNextSquare(nextSquare, currentSquare, currentPlayer, enemyPlayer)) return false
    return true
}

private fun Stage.showLostPieces(player1: Player, x: Int) {
    var x1 = x
    var index = 0
    player1.lostPieces.forEach { piece ->
        if (index == 10) {
            index = 0
            x1 += 50
        }
        piece.visible = true
        piece.view.xy(x1, 50 + 50 * index)
        piece.view.size(squareSize / 1.5f, squareSize / 1.5f)
        addChild(piece.view)
        index++
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
    firstSelectedSquare?.piece?.resetColor()
    secondSelectedSquare?.piece?.resetColor()
    firstSelectedSquare = null
    secondSelectedSquare = null
}

private fun moveToNextSquare(
    nextSquare: Square,
    currentSquare: Square,
    currentPlayer: Player,
    enemyPlayer: Player
): Boolean {
    val guessAI: GuessAI = if(enemyPlayer is GuessAI) enemyPlayer.fastCastTo() else currentPlayer.fastCastTo()
    nextSquare.piece?.resetColor()
    currentSquare.piece?.resetColor()
    return if (nextSquare.piece == null) {
        currentSquare.piece?.let { nextSquare.addPiece(it) }
        currentSquare.removePiece()
        guessAI.updateGuessMove(currentSquare.xCoord, currentSquare.yCoord, nextSquare.xCoord, nextSquare.yCoord)
        true
    } else {
        currentSquare.piece?.visible = true
        nextSquare.piece?.visible = true
        val hasWonConfrontation = confrontPiece(currentSquare.piece, currentPlayer, nextSquare.piece, enemyPlayer)
        guessAI.updateGuessConfront(hasWonConfrontation, currentSquare.xCoord, currentSquare.yCoord, nextSquare.xCoord, nextSquare.yCoord)
        currentSquare.piece?.showPiece()
        nextSquare.piece?.showPiece()
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

suspend fun Stage.placePiecesOnBoard(squares: Array<Array<Square>>, player1Pieces: MutableList<Piece>, player2Pieces: MutableList<Piece>) {

    val spriteMap = resourcesVfs["Small-8-Direction-Characters_by_AxulArt.png"].readBitmap()
    val blueWalkAnimation = SpriteAnimation(
        spriteMap = spriteMap,
        spriteWidth = 128 / 8,
        spriteHeight = 264 / 12,
        marginTop = 264 / 12 * 5,
        marginLeft = 128 / 8 * 4,
        columns = 1,
        rows = 3,
        offsetBetweenColumns = 0,
        offsetBetweenRows = 0,
    )

    val blueSelectedAnimation = SpriteAnimation(
        spriteMap = spriteMap,
        spriteWidth = 128 / 8,
        spriteHeight = 264 / 12,
        marginTop = 264 / 12 * 5,
        marginLeft = 0,
        columns = 8,
        rows = 1,
        offsetBetweenColumns = 0,
        offsetBetweenRows = 0,
    )

    val redWalkAnimation = SpriteAnimation(
        spriteMap = spriteMap,
        spriteWidth = 128 / 8,
        spriteHeight = 264 / 12,
        marginTop = 264 / 12 * 9,
        marginLeft = 0,
        columns = 1,
        rows = 3,
        offsetBetweenColumns = 0,
        offsetBetweenRows = 0,
    )

    val redSelectedAnimation = SpriteAnimation(
        spriteMap = spriteMap,
        spriteWidth = 128 / 8,
        spriteHeight = 264 / 12,
        marginTop = 264 / 12 * 9,
        marginLeft = 0,
        columns = 8,
        rows = 1,
        offsetBetweenColumns = 0,
        offsetBetweenRows = 0,
    )

    var index = 0
    for (i in 0 until 4) {
        for (j in 0..9) {
            val piece = player1Pieces[index]

            val bluePlayerSpriteWalking = sprite(blueWalkAnimation)
            bluePlayerSpriteWalking.size(squareSize/1.5f,squareSize/1.5f)
            bluePlayerSpriteWalking.playAnimationLooped(spriteDisplayTime = 100.milliseconds, reversed = false)

            val bluePlayerSpriteSelected = sprite(blueSelectedAnimation)
            bluePlayerSpriteSelected.size(squareSize/1.5f,squareSize/1.5f)
            bluePlayerSpriteSelected.playAnimationLooped(spriteDisplayTime = 100.milliseconds, reversed = false)

            piece.view.addChildren(listOf(bluePlayerSpriteWalking,bluePlayerSpriteSelected))

            bluePlayerSpriteWalking.centerOn(piece.view)
            bluePlayerSpriteSelected.centerOn(piece.view)
            bluePlayerSpriteSelected.visible(false)

            piece.showPiece()
            squares[j][i].addPiece(piece)
            index++
        }
    }

    index = 0
    for (i in 6 until 10) {
        for (j in 0..9) {
            val piece = player2Pieces[index]
            val redPlayerSpriteWalking = sprite(redWalkAnimation)
            redPlayerSpriteWalking.size(squareSize/1.5f,squareSize/1.5f)
            redPlayerSpriteWalking.playAnimationLooped(spriteDisplayTime = 100.milliseconds, reversed = true)

            val redPlayerSpriteSelected = sprite(redSelectedAnimation)
            redPlayerSpriteSelected.size(squareSize/1.5f,squareSize/1.5f)
            redPlayerSpriteSelected.playAnimationLooped(spriteDisplayTime = 100.milliseconds, reversed = false)

            piece.view.addChildren(listOf(redPlayerSpriteWalking,redPlayerSpriteSelected))
            redPlayerSpriteWalking.centerOn(piece.view)
            redPlayerSpriteSelected.centerOn(piece.view)
            redPlayerSpriteSelected.visible(false)


            piece.hidePiece()
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
            playerTurnText.firstChild.setText("This is the flag! " + player.name + " wins!")
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
