//import korlibs.image.color.*
//import kotlin.test.*
//
//class SmartAITest {
//
//
//    @Test
//    fun testCalculateNextMove() {
//
//
//        val smartAI = SmartAI(Colors.RED)
//        for (j in 0..100) {
//            val randomSquare = smartAI.guessingBoard[(0..9).random()][(0..9).random()]
//            val secondRandomSquare = smartAI.guessingBoard[(0..9).random()][(0..9).random()]
//            randomSquare.piece = secondRandomSquare.piece.also { secondRandomSquare.piece = randomSquare.piece }
//        }
//        smartAI.guessingBoard.forEachIndexed { index, column -> println(); column.forEach { square ->
//            var s = " " + if (square.piece in smartAI.pieces){
//                "player"
//            } else {"enemy"} + "|" + square.piece?.type?.rank
//            for(i in 0..12- s.length){
//               s+=" "
//            }
//            print(s)
//        } }
//        println()
//        val calculateNextMove = smartAI.calculateNextMove()
//        println("${calculateNextMove.first.type.rank}")
//        println("distance to: ${calculateNextMove.second}")
//        println("${calculateNextMove.third.type.rank}")
//        println()
//        assertTrue(calculateNextMove.first.type.rank > calculateNextMove.third.type.rank)
//        assertTrue( calculateNextMove.first in smartAI.pieces)
//        assertFalse( calculateNextMove.third in smartAI.pieces)
//    }
//}
