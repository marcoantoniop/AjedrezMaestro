package com.example.chess.engine

import com.example.chess.model.EvalResult
import com.example.chess.model.Move
import com.example.chess.model.MoveQuality
import com.example.chess.model.PieceColor
import com.example.chess.model.PieceType
import com.example.chess.model.Square
import kotlin.math.max
import kotlin.math.min

object ChessEvaluator {

    // Piece-Square Tables (White perspective, rank 0 to 7)
    private val PAWN_PST = intArrayOf(
        0,  0,  0,  0,  0,  0,  0,  0,
        50, 50, 50, 50, 50, 50, 50, 50,
        10, 10, 20, 30, 30, 20, 10, 10,
        5,  5, 10, 25, 25, 10,  5,  5,
        0,  0,  0, 20, 20,  0,  0,  0,
        5, -5,-10,  0,  0,-10, -5,  5,
        5, 10, 10,-20,-20, 10, 10,  5,
        0,  0,  0,  0,  0,  0,  0,  0
    )

    private val KNIGHT_PST = intArrayOf(
        -50,-40,-30,-30,-30,-30,-40,-50,
        -40,-20,  0,  0,  0,  0,-20,-40,
        -30,  0, 10, 15, 15, 10,  0,-30,
        -30,  5, 15, 20, 20, 15,  5,-30,
        -30,  0, 15, 20, 20, 15,  0,-30,
        -30,  5, 10, 15, 15, 10,  5,-30,
        -40,-20,  0,  5,  5,  0,-20,-40,
        -50,-40,-30,-30,-30,-30,-40,-50
    )

    private val BISHOP_PST = intArrayOf(
        -20,-10,-10,-10,-10,-10,-10,-20,
        -10,  0,  0,  0,  0,  0,  0,-10,
        -10,  0,  5, 10, 10,  5,  0,-10,
        -10,  5,  5, 10, 10,  5,  5,-10,
        -10,  0, 10, 10, 10, 10,  0,-10,
        -10, 10, 10, 10, 10, 10, 10,-10,
        -10,  5,  0,  0,  0,  0,  5,-10,
        -20,-10,-10,-10,-10,-10,-10,-20
    )

    private val ROOK_PST = intArrayOf(
        0,  0,  0,  0,  0,  0,  0,  0,
        5, 10, 10, 10, 10, 10, 10,  5,
        -5,  0,  0,  0,  0,  0,  0, -5,
        -5,  0,  0,  0,  0,  0,  0, -5,
        -5,  0,  0,  0,  0,  0,  0, -5,
        -5,  0,  0,  0,  0,  0,  0, -5,
        -5,  0,  0,  0,  0,  0,  0, -5,
        0,  0,  0,  5,  5,  0,  0,  0
    )

    private val QUEEN_PST = intArrayOf(
        -20,-10,-10, -5, -5,-10,-10,-20,
        -10,  0,  0,  0,  0,  0,  0,-10,
        -10,  0,  5,  5,  5,  5,  0,-10,
        -5,  0,  5,  5,  5,  5,  0, -5,
        0,  0,  5,  5,  5,  5,  0, -5,
        -10,  5,  5,  5,  5,  5,  0,-10,
        -10,  0,  5,  0,  0,  0,  0,-10,
        -20,-10,-10, -5, -5,-10,-10,-20
    )

    private val KING_MID_PST = intArrayOf(
        -30,-40,-40,-50,-50,-40,-40,-30,
        -30,-40,-40,-50,-50,-40,-40,-30,
        -30,-40,-40,-50,-50,-40,-40,-30,
        -30,-40,-40,-50,-50,-40,-40,-30,
        -20,-30,-30,-40,-40,-30,-30,-20,
        -10,-20,-20,-20,-20,-20,-20,-10,
        20, 20,  0,  0,  0,  0, 20, 20,
        20, 30, 10,  0,  0, 10, 30, 20
    )

    fun evaluatePosition(game: ChessGame): Int {
        var whiteScore = 0
        var blackScore = 0

        for (r in 0..7) {
            for (f in 0..7) {
                val piece = game.getPiece(f, r) ?: continue
                val isWhite = piece.color == PieceColor.WHITE
                val baseVal = piece.type.baseValue

                val pstRank = if (isWhite) r else 7 - r
                val pstIndex = (7 - pstRank) * 8 + f

                val pstBonus = when (piece.type) {
                    PieceType.PAWN -> PAWN_PST[pstIndex]
                    PieceType.KNIGHT -> KNIGHT_PST[pstIndex]
                    PieceType.BISHOP -> BISHOP_PST[pstIndex]
                    PieceType.ROOK -> ROOK_PST[pstIndex]
                    PieceType.QUEEN -> QUEEN_PST[pstIndex]
                    PieceType.KING -> KING_MID_PST[pstIndex]
                }

                val totalPieceScore = baseVal + pstBonus
                if (isWhite) {
                    whiteScore += totalPieceScore
                } else {
                    blackScore += totalPieceScore
                }
            }
        }

        return whiteScore - blackScore
    }

    fun analyze(game: ChessGame, depth: Int = 3): EvalResult {
        val legalMoves = game.generateLegalMoves()
        if (legalMoves.isEmpty()) {
            return if (game.isInCheck()) {
                val mateScore = if (game.activeColor == PieceColor.WHITE) -30000 else 30000
                EvalResult(scoreCentipawns = mateScore, mateInMoves = if (game.activeColor == PieceColor.WHITE) -1 else 1)
            } else {
                EvalResult(scoreCentipawns = 0)
            }
        }

        val isWhite = game.activeColor == PieceColor.WHITE
        var bestScore = if (isWhite) -999999 else 999999
        var bestMove: Move? = null
        val pv = mutableListOf<Move>()

        for (move in legalMoves) {
            game.makeMove(move, updateSan = false)
            val score = alphaBeta(game, depth - 1, -999999, 999999, !isWhite)
            game.undoMove()

            if (isWhite) {
                if (score > bestScore) {
                    bestScore = score
                    bestMove = move
                }
            } else {
                if (score < bestScore) {
                    bestScore = score
                    bestMove = move
                }
            }
        }

        if (bestMove != null) {
            pv.add(bestMove)
        }

        val mateInMoves = when {
            bestScore > 25000 -> (30000 - bestScore + 1) / 2
            bestScore < -25000 -> -((30000 + bestScore + 1) / 2)
            else -> null
        }

        return EvalResult(
            scoreCentipawns = bestScore.coerceIn(-29999, 29999),
            mateInMoves = mateInMoves,
            bestMove = bestMove,
            principalVariation = pv
        )
    }

    private fun alphaBeta(
        game: ChessGame,
        depth: Int,
        alphaInit: Int,
        betaInit: Int,
        isMaximizing: Boolean
    ): Int {
        if (depth == 0) {
            return evaluatePosition(game)
        }

        val legalMoves = game.generateLegalMoves()
        if (legalMoves.isEmpty()) {
            return if (game.isInCheck()) {
                if (isMaximizing) -30000 + (3 - depth) else 30000 - (3 - depth)
            } else {
                0
            }
        }

        var alpha = alphaInit
        var beta = betaInit

        if (isMaximizing) {
            var maxEval = -999999
            for (move in legalMoves) {
                game.makeMove(move, updateSan = false)
                val eval = alphaBeta(game, depth - 1, alpha, beta, false)
                game.undoMove()
                maxEval = max(maxEval, eval)
                alpha = max(alpha, eval)
                if (beta <= alpha) break
            }
            return maxEval
        } else {
            var minEval = 999999
            for (move in legalMoves) {
                game.makeMove(move, updateSan = false)
                val eval = alphaBeta(game, depth - 1, alpha, beta, true)
                game.undoMove()
                minEval = min(minEval, eval)
                beta = min(beta, eval)
                if (beta <= alpha) break
            }
            return minEval
        }
    }

    fun classifyMoveQuality(
        evalBefore: EvalResult,
        evalAfter: EvalResult,
        isWhiteTurn: Boolean,
        isPieceSacrifice: Boolean = false
    ): MoveQuality {
        val deltaCp = if (isWhiteTurn) {
            evalBefore.scoreCentipawns - evalAfter.scoreCentipawns
        } else {
            evalAfter.scoreCentipawns - evalBefore.scoreCentipawns
        }

        return when {
            isPieceSacrifice && deltaCp <= 10 -> MoveQuality.BRILLIANT
            deltaCp <= 8 -> MoveQuality.BEST
            deltaCp <= 35 -> MoveQuality.EXCELLENT
            deltaCp <= 80 -> MoveQuality.GOOD
            deltaCp <= 160 -> MoveQuality.INACCURACY
            deltaCp <= 320 -> MoveQuality.MISTAKE
            else -> MoveQuality.BLUNDER
        }
    }
}
