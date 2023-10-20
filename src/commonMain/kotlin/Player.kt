import korlibs.image.color.*

data class Player(val name: String, val color: RGBA, val pieces: MutableList<Piece> = mutableListOf()){
        
    init {
        createPieces()
        pieces.shuffle()
    }
    
    private fun createPieces() {
        pieces.add(Piece(0, Type.FLAG, 0, color))
        pieces.add(Piece(1, Type.SPY, color = color))
        for (i in 0..7) {
            pieces.add(Piece(2, Type.SCOUT, 10, color))
        }
        for (i in 0..4) {
            pieces.add(Piece(3, Type.MINER, color = color))
        }
        for (i in 0..3) {
            pieces.add(Piece(4, Type.SERGEANT, color = color))
            pieces.add(Piece(5, Type.LIEUTENANT, color = color))
            pieces.add(Piece(6, Type.CAPTAIN, color = color))
        }
        for (i in 0..2) {
            pieces.add(Piece(7, Type.MAJOR, color = color))
        }
        for (i in 0..1) {
            pieces.add(Piece(8, Type.COLONEL, color = color))
        }
        pieces.add(Piece(9, Type.GENERAL, color = color))
        pieces.add(Piece(10, Type.MARSHALL, color = color))
        for (i in 0..5) {
            pieces.add(Piece(12, Type.BOMB, 0, color))
        }
    }
}
