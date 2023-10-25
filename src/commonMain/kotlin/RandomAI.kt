import kotlin.random.*

class RandomAI {

    //Needs tweaking
    private fun randomAI(currentPlayer: Player, board: Array<Array<Square>>) {
        val highRankPiece = currentPlayer.pieces.filter { it.type.rank in 6..10 }.randomOrNull()
        val randomPiece = currentPlayer.pieces.random()

        val selectedPiece =
            if (Random.nextInt(0, 10) < 3 && highRankPiece != null) highRankPiece else randomPiece
        val chosenPiece =
            board.flatMap { squareList -> squareList.filter { it.piece == selectedPiece } }.first()

        if (!chosenPiece.handleClick()) return
        val isScout = chosenPiece.piece?.type == Type.SCOUT

        var stepsY =
            if (isScout) (1..(9 - chosenPiece.yCoord)).random()
            else if (chosenPiece.yCoord == 0) -1
            else 1
        var stepsX =
            if (isScout) (1..(9 - chosenPiece.xCoord)).random()
            else if (chosenPiece.xCoord == 9) -1
            else 1

        if (Random.nextInt(0, 10) < 7) stepsX = 0 else stepsY = 0

        val chosenSquare =
            if (Random.nextInt(0, 10) < 7) board[chosenPiece.xCoord - stepsX][chosenPiece.yCoord - stepsY]
            else board[chosenPiece.xCoord + stepsX][chosenPiece.yCoord + stepsY]
        chosenSquare.handleClick()
    }
}
