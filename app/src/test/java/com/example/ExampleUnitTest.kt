package com.example

import com.example.chess.engine.ChessEvaluator
import com.example.chess.engine.ChessGame
import com.example.chess.engine.GameStatus
import com.example.chess.model.Move
import com.example.chess.model.PieceColor
import com.example.chess.model.Square
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testStartingPositionLegalMoves() {
        val game = ChessGame()
        val legalMoves = game.generateLegalMoves()
        // In chess, standard starting position has exactly 20 legal moves (16 pawn pushes + 4 knight moves)
        assertEquals(20, legalMoves.size)
        assertEquals(PieceColor.WHITE, game.activeColor)
    }

    @Test
    fun testMoveExecutionAndFen() {
        val game = ChessGame()
        // Play 1. e4
        val e2 = Square.fromNotation("e2")
        val e4 = Square.fromNotation("e4")
        val move = Move(e2, e4)
        val success = game.makeMove(move)
        assertTrue(success)
        assertEquals(PieceColor.BLACK, game.activeColor)
        assertEquals(Square.fromNotation("e3"), game.enPassantSquare)
    }

    @Test
    fun testScholarMateCheckmate() {
        val game = ChessGame()
        // 1. e4 e5
        game.makeMove(Move(Square.fromNotation("e2"), Square.fromNotation("e4")))
        game.makeMove(Move(Square.fromNotation("e7"), Square.fromNotation("e5")))
        // 2. Bc4 Nc6
        game.makeMove(Move(Square.fromNotation("f1"), Square.fromNotation("c4")))
        game.makeMove(Move(Square.fromNotation("b8"), Square.fromNotation("c6")))
        // 3. Qh5 Nf6??
        game.makeMove(Move(Square.fromNotation("d1"), Square.fromNotation("h5")))
        game.makeMove(Move(Square.fromNotation("g8"), Square.fromNotation("f6")))
        // 4. Qxf7#
        val mateMove = Move(Square.fromNotation("h5"), Square.fromNotation("f7"), isCapture = true)
        game.makeMove(mateMove)

        assertEquals(GameStatus.CHECKMATE, game.getGameStatus())
    }

    @Test
    fun testEvaluationEngine() {
        val game = ChessGame()
        val eval = ChessEvaluator.analyze(game, depth = 2)
        assertNotNull(eval.bestMove)
        assertTrue(eval.principalVariation.isNotEmpty())
    }
}
