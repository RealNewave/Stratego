import korlibs.image.color.*

open class Player(val name: String, val color: RGBA){
    val pieces: MutableList<Piece> = mutableListOf()
    val lostPieces: MutableList<Piece> = mutableListOf()
    init {
        createPieces()
        pieces.shuffle()
    }

    fun removePiece(piece: Piece){
       piece.setSelectedColor()
       lostPieces.add(piece)
       pieces.remove(piece)
    }
    
    private fun createPieces() {
        pieces.add(Piece(Type.FLAG, 0, color))
        pieces.add(Piece(Type.SPY, color = color))
        for (i in 0..7) {
            pieces.add(Piece(Type.SCOUT, 10, color))
        }
        for (i in 0..4) {
            pieces.add(Piece(Type.MINER, color = color))
        }
        for (i in 0..3) {
            pieces.add(Piece(Type.SERGEANT, color = color))
            pieces.add(Piece(Type.LIEUTENANT, color = color))
            pieces.add(Piece(Type.CAPTAIN, color = color))
        }
        for (i in 0..2) {
            pieces.add(Piece(Type.MAJOR, color = color))
        }
        for (i in 0..1) {
            pieces.add(Piece(Type.COLONEL, color = color))
        }
        pieces.add(Piece(Type.GENERAL, color = color))
        pieces.add(Piece(Type.MARSHALL, color = color))
        for (i in 0..5) {
            pieces.add(Piece(Type.BOMB, 0, color))
        }
    }
}
