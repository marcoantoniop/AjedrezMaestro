package com.example.chess.engine

import com.example.chess.model.Move
import com.example.chess.model.Piece
import com.example.chess.model.PieceColor
import com.example.chess.model.PieceType
import com.example.chess.model.Square

enum class GameStatus {
    IN_PROGRESS,
    CHECK,
    CHECKMATE,
    STALEMATE,
    DRAW_INSUFFICIENT_MATERIAL
}

data class CastlingRights(
    val whiteKingside: Boolean = true,
    val whiteQueenside: Boolean = true,
    val blackKingside: Boolean = true,
    val blackQueenside: Boolean = true
)

class ChessGame(fen: String = STARTING_FEN) {
    companion object {
        const val STARTING_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"
    }

    private val board = Array<Piece?>(64) { null }
    var activeColor: PieceColor = PieceColor.WHITE
        private set
    var castlingRights = CastlingRights()
        private set
    var enPassantSquare: Square? = null
        private set
    var halfmoveClock: Int = 0
        private set
    var fullmoveNumber: Int = 1
        private set

    private val moveHistory = mutableListOf<Move>()
    private val boardHistory = mutableListOf<Array<Piece?>>()
    private val stateHistory = mutableListOf<Triple<CastlingRights, Square?, Int>>()

    init {
        loadFen(fen)
    }

    fun getPiece(square: Square): Piece? = board[square.rank * 8 + square.file]
    fun getPiece(file: Int, rank: Int): Piece? = board[rank * 8 + file]

    private fun setPiece(square: Square, piece: Piece?) {
        board[square.rank * 8 + square.file] = piece
    }

    fun getBoardCopy(): Array<Piece?> = board.copyOf()
    fun getMoveHistory(): List<Move> = moveHistory.toList()

    fun loadFen(fen: String) {
        board.fill(null)
        val parts = fen.trim().split("\\s+".toRegex())
        val ranks = parts[0].split('/')

        for (r in 0..7) {
            val rankIndex = 7 - r
            var fileIndex = 0
            val rankStr = ranks[r]
            for (char in rankStr) {
                if (char.isDigit()) {
                    fileIndex += char.digitToInt()
                } else {
                    val color = if (char.isUpperCase()) PieceColor.WHITE else PieceColor.BLACK
                    val type = when (char.lowercaseChar()) {
                        'p' -> PieceType.PAWN
                        'n' -> PieceType.KNIGHT
                        'b' -> PieceType.BISHOP
                        'r' -> PieceType.ROOK
                        'q' -> PieceType.QUEEN
                        'k' -> PieceType.KING
                        else -> PieceType.PAWN
                    }
                    board[rankIndex * 8 + fileIndex] = Piece(type, color)
                    fileIndex++
                }
            }
        }

        activeColor = if (parts.size > 1 && parts[1] == "b") PieceColor.BLACK else PieceColor.WHITE

        if (parts.size > 2) {
            val castlingStr = parts[2]
            castlingRights = CastlingRights(
                whiteKingside = 'K' in castlingStr,
                whiteQueenside = 'Q' in castlingStr,
                blackKingside = 'k' in castlingStr,
                blackQueenside = 'q' in castlingStr
            )
        } else {
            castlingRights = CastlingRights()
        }

        enPassantSquare = if (parts.size > 3 && parts[3] != "-") {
            try {
                Square.fromNotation(parts[3])
            } catch (e: Exception) {
                null
            }
        } else null

        halfmoveClock = if (parts.size > 4) parts[4].toIntOrNull() ?: 0 else 0
        fullmoveNumber = if (parts.size > 5) parts[5].toIntOrNull() ?: 1 else 1

        moveHistory.clear()
        boardHistory.clear()
        stateHistory.clear()
    }

    fun toFen(): String {
        val fenBuilder = StringBuilder()
        for (r in 7 downTo 0) {
            var emptyCount = 0
            for (f in 0..7) {
                val piece = getPiece(f, r)
                if (piece == null) {
                    emptyCount++
                } else {
                    if (emptyCount > 0) {
                        fenBuilder.append(emptyCount)
                        emptyCount = 0
                    }
                    fenBuilder.append(piece.fenChar)
                }
            }
            if (emptyCount > 0) {
                fenBuilder.append(emptyCount)
            }
            if (r > 0) fenBuilder.append('/')
        }

        fenBuilder.append(if (activeColor == PieceColor.WHITE) " w " else " b ")

        val castling = buildString {
            if (castlingRights.whiteKingside) append('K')
            if (castlingRights.whiteQueenside) append('Q')
            if (castlingRights.blackKingside) append('k')
            if (castlingRights.blackQueenside) append('q')
        }
        fenBuilder.append(if (castling.isEmpty()) "-" else castling)
        fenBuilder.append(" ")
        fenBuilder.append(enPassantSquare?.notation ?: "-")
        fenBuilder.append(" $halfmoveClock $fullmoveNumber")
        return fenBuilder.toString()
    }

    fun getKingSquare(color: PieceColor): Square? {
        for (r in 0..7) {
            for (f in 0..7) {
                val piece = getPiece(f, r)
                if (piece != null && piece.color == color && piece.type == PieceType.KING) {
                    return Square(f, r)
                }
            }
        }
        return null
    }

    fun isSquareAttacked(square: Square, attackerColor: PieceColor): Boolean {
        // 1. Pawn attacks
        val pawnDir = if (attackerColor == PieceColor.WHITE) -1 else 1
        val pawnAttackingRank = square.rank + pawnDir
        if (pawnAttackingRank in 0..7) {
            for (df in listOf(-1, 1)) {
                val pf = square.file + df
                if (pf in 0..7) {
                    val p = getPiece(pf, pawnAttackingRank)
                    if (p != null && p.color == attackerColor && p.type == PieceType.PAWN) {
                        return true
                    }
                }
            }
        }

        // 2. Knight attacks
        val knightOffsets = listOf(
            Pair(1, 2), Pair(2, 1), Pair(2, -1), Pair(1, -2),
            Pair(-1, -2), Pair(-2, -1), Pair(-2, 1), Pair(-1, 2)
        )
        for ((df, dr) in knightOffsets) {
            val nf = square.file + df
            val nr = square.rank + dr
            if (nf in 0..7 && nr in 0..7) {
                val p = getPiece(nf, nr)
                if (p != null && p.color == attackerColor && p.type == PieceType.KNIGHT) {
                    return true
                }
            }
        }

        // 3. Bishop/Queen diagonal attacks
        val diagonalDirs = listOf(Pair(1, 1), Pair(1, -1), Pair(-1, 1), Pair(-1, -1))
        for ((df, dr) in diagonalDirs) {
            var curF = square.file + df
            var curR = square.rank + dr
            while (curF in 0..7 && curR in 0..7) {
                val p = getPiece(curF, curR)
                if (p != null) {
                    if (p.color == attackerColor && (p.type == PieceType.BISHOP || p.type == PieceType.QUEEN)) {
                        return true
                    }
                    break
                }
                curF += df
                curR += dr
            }
        }

        // 4. Rook/Queen straight attacks
        val straightDirs = listOf(Pair(1, 0), Pair(-1, 0), Pair(0, 1), Pair(0, -1))
        for ((df, dr) in straightDirs) {
            var curF = square.file + df
            var curR = square.rank + dr
            while (curF in 0..7 && curR in 0..7) {
                val p = getPiece(curF, curR)
                if (p != null) {
                    if (p.color == attackerColor && (p.type == PieceType.ROOK || p.type == PieceType.QUEEN)) {
                        return true
                    }
                    break
                }
                curF += df
                curR += dr
            }
        }

        // 5. King attacks
        for (df in -1..1) {
            for (dr in -1..1) {
                if (df == 0 && dr == 0) continue
                val kf = square.file + df
                val kr = square.rank + dr
                if (kf in 0..7 && kr in 0..7) {
                    val p = getPiece(kf, kr)
                    if (p != null && p.color == attackerColor && p.type == PieceType.KING) {
                        return true
                    }
                }
            }
        }

        return false
    }

    fun isInCheck(color: PieceColor = activeColor): Boolean {
        val kingSq = getKingSquare(color) ?: return false
        return isSquareAttacked(kingSq, color.opposite())
    }

    fun generateLegalMoves(): List<Move> {
        val pseudoMoves = generatePseudoLegalMoves()
        val legalMoves = mutableListOf<Move>()

        for (m in pseudoMoves) {
            makeMove(m, updateSan = false)
            val kingSafe = !isInCheck(activeColor.opposite())
            undoMove()
            if (kingSafe) {
                legalMoves.add(m)
            }
        }

        // Generate accurate SAN for all legal moves
        for (m in legalMoves) {
            m.san = generateSan(m, legalMoves)
        }

        return legalMoves
    }

    private fun generatePseudoLegalMoves(): List<Move> {
        val moves = mutableListOf<Move>()
        val color = activeColor
        val forwardDir = if (color == PieceColor.WHITE) 1 else -1
        val startRank = if (color == PieceColor.WHITE) 1 else 6
        val promotionRank = if (color == PieceColor.WHITE) 7 else 0

        for (r in 0..7) {
            for (f in 0..7) {
                val piece = getPiece(f, r) ?: continue
                if (piece.color != color) continue
                val from = Square(f, r)

                when (piece.type) {
                    PieceType.PAWN -> {
                        // Forward 1
                        val r1 = r + forwardDir
                        if (r1 in 0..7 && getPiece(f, r1) == null) {
                            if (r1 == promotionRank) {
                                listOf(PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT).forEach { promo ->
                                    moves.add(Move(from, Square(f, r1), promotion = promo))
                                }
                            } else {
                                moves.add(Move(from, Square(f, r1)))
                                // Forward 2 from start rank
                                val r2 = r + 2 * forwardDir
                                if (r == startRank && getPiece(f, r2) == null) {
                                    moves.add(Move(from, Square(f, r2)))
                                }
                            }
                        }

                        // Diagonal captures
                        for (df in listOf(-1, 1)) {
                            val capF = f + df
                            val capR = r + forwardDir
                            if (capF in 0..7 && capR in 0..7) {
                                val target = getPiece(capF, capR)
                                if (target != null && target.color != color) {
                                    if (capR == promotionRank) {
                                        listOf(PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT).forEach { promo ->
                                            moves.add(Move(from, Square(capF, capR), promotion = promo, isCapture = true))
                                        }
                                    } else {
                                        moves.add(Move(from, Square(capF, capR), isCapture = true))
                                    }
                                } else if (enPassantSquare != null && enPassantSquare == Square(capF, capR)) {
                                    moves.add(Move(from, Square(capF, capR), isCapture = true, isEnPassant = true))
                                }
                            }
                        }
                    }

                    PieceType.KNIGHT -> {
                        val knightOffsets = listOf(
                            Pair(1, 2), Pair(2, 1), Pair(2, -1), Pair(1, -2),
                            Pair(-1, -2), Pair(-2, -1), Pair(-2, 1), Pair(-1, 2)
                        )
                        for ((df, dr) in knightOffsets) {
                            val tf = f + df
                            val tr = r + dr
                            if (tf in 0..7 && tr in 0..7) {
                                val target = getPiece(tf, tr)
                                if (target == null) {
                                    moves.add(Move(from, Square(tf, tr)))
                                } else if (target.color != color) {
                                    moves.add(Move(from, Square(tf, tr), isCapture = true))
                                }
                            }
                        }
                    }

                    PieceType.BISHOP, PieceType.ROOK, PieceType.QUEEN -> {
                        val directions = mutableListOf<Pair<Int, Int>>()
                        if (piece.type == PieceType.BISHOP || piece.type == PieceType.QUEEN) {
                            directions.addAll(listOf(Pair(1, 1), Pair(1, -1), Pair(-1, 1), Pair(-1, -1)))
                        }
                        if (piece.type == PieceType.ROOK || piece.type == PieceType.QUEEN) {
                            directions.addAll(listOf(Pair(1, 0), Pair(-1, 0), Pair(0, 1), Pair(0, -1)))
                        }

                        for ((df, dr) in directions) {
                            var curF = f + df
                            var curR = r + dr
                            while (curF in 0..7 && curR in 0..7) {
                                val target = getPiece(curF, curR)
                                if (target == null) {
                                    moves.add(Move(from, Square(curF, curR)))
                                } else {
                                    if (target.color != color) {
                                        moves.add(Move(from, Square(curF, curR), isCapture = true))
                                    }
                                    break
                                }
                                curF += df
                                curR += dr
                            }
                        }
                    }

                    PieceType.KING -> {
                        for (df in -1..1) {
                            for (dr in -1..1) {
                                if (df == 0 && dr == 0) continue
                                val tf = f + df
                                val tr = r + dr
                                if (tf in 0..7 && tr in 0..7) {
                                    val target = getPiece(tf, tr)
                                    if (target == null) {
                                        moves.add(Move(from, Square(tf, tr)))
                                    } else if (target.color != color) {
                                        moves.add(Move(from, Square(tf, tr), isCapture = true))
                                    }
                                }
                            }
                        }

                        // Castling
                        val kingRank = if (color == PieceColor.WHITE) 0 else 7
                        if (r == kingRank && f == 4 && !isInCheck(color)) {
                            // Kingside
                            val canCastleKingside = if (color == PieceColor.WHITE) castlingRights.whiteKingside else castlingRights.blackKingside
                            if (canCastleKingside &&
                                getPiece(5, kingRank) == null &&
                                getPiece(6, kingRank) == null &&
                                !isSquareAttacked(Square(5, kingRank), color.opposite()) &&
                                !isSquareAttacked(Square(6, kingRank), color.opposite())
                            ) {
                                moves.add(Move(from, Square(6, kingRank), isCastlingKingside = true))
                            }

                            // Queenside
                            val canCastleQueenside = if (color == PieceColor.WHITE) castlingRights.whiteQueenside else castlingRights.blackQueenside
                            if (canCastleQueenside &&
                                getPiece(3, kingRank) == null &&
                                getPiece(2, kingRank) == null &&
                                getPiece(1, kingRank) == null &&
                                !isSquareAttacked(Square(3, kingRank), color.opposite()) &&
                                !isSquareAttacked(Square(2, kingRank), color.opposite())
                            ) {
                                moves.add(Move(from, Square(2, kingRank), isCastlingQueenside = true))
                            }
                        }
                    }
                }
            }
        }
        return moves
    }

    fun makeMove(move: Move, updateSan: Boolean = true): Boolean {
        val movingPiece = getPiece(move.from) ?: return false

        boardHistory.add(board.copyOf())
        stateHistory.add(Triple(castlingRights, enPassantSquare, halfmoveClock))

        var isCapture = move.isCapture || getPiece(move.to) != null

        // Handle En Passant capture
        if (movingPiece.type == PieceType.PAWN && move.to == enPassantSquare) {
            val capturedPawnRank = move.from.rank
            setPiece(Square(move.to.file, capturedPawnRank), null)
            isCapture = true
        }

        // Move piece
        setPiece(move.from, null)
        val finalPiece = if (move.promotion != null) Piece(move.promotion, movingPiece.color) else movingPiece
        setPiece(move.to, finalPiece)

        // Handle Castling rook move
        if (movingPiece.type == PieceType.KING) {
            if (move.from.file == 4 && move.to.file == 6) {
                // O-O
                val rFrom = Square(7, move.from.rank)
                val rTo = Square(5, move.from.rank)
                val rook = getPiece(rFrom)
                setPiece(rFrom, null)
                setPiece(rTo, rook)
            } else if (move.from.file == 4 && move.to.file == 2) {
                // O-O-O
                val rFrom = Square(0, move.from.rank)
                val rTo = Square(3, move.from.rank)
                val rook = getPiece(rFrom)
                setPiece(rFrom, null)
                setPiece(rTo, rook)
            }
        }

        // Update castling rights
        var newWhiteK = castlingRights.whiteKingside
        var newWhiteQ = castlingRights.whiteQueenside
        var newBlackK = castlingRights.blackKingside
        var newBlackQ = castlingRights.blackQueenside

        if (movingPiece.type == PieceType.KING) {
            if (movingPiece.color == PieceColor.WHITE) {
                newWhiteK = false
                newWhiteQ = false
            } else {
                newBlackK = false
                newBlackQ = false
            }
        }
        if (move.from == Square(0, 0) || move.to == Square(0, 0)) newWhiteQ = false
        if (move.from == Square(7, 0) || move.to == Square(7, 0)) newWhiteK = false
        if (move.from == Square(0, 7) || move.to == Square(0, 7)) newBlackQ = false
        if (move.from == Square(7, 7) || move.to == Square(7, 7)) newBlackK = false

        castlingRights = CastlingRights(newWhiteK, newWhiteQ, newBlackK, newBlackQ)

        // En passant square update
        enPassantSquare = if (movingPiece.type == PieceType.PAWN && Math.abs(move.to.rank - move.from.rank) == 2) {
            Square(move.from.file, (move.from.rank + move.to.rank) / 2)
        } else {
            null
        }

        if (movingPiece.type == PieceType.PAWN || isCapture) {
            halfmoveClock = 0
        } else {
            halfmoveClock++
        }

        if (activeColor == PieceColor.BLACK) {
            fullmoveNumber++
        }
        activeColor = activeColor.opposite()

        if (updateSan && move.san.isEmpty()) {
            move.san = move.uci
        }
        moveHistory.add(move)
        return true
    }

    fun undoMove(): Boolean {
        if (boardHistory.isEmpty() || stateHistory.isEmpty() || moveHistory.isEmpty()) {
            return false
        }

        val prevBoard = boardHistory.removeAt(boardHistory.lastIndex)
        val (prevCastling, prevEp, prevHalfmove) = stateHistory.removeAt(stateHistory.lastIndex)
        moveHistory.removeAt(moveHistory.lastIndex)

        System.arraycopy(prevBoard, 0, board, 0, 64)
        castlingRights = prevCastling
        enPassantSquare = prevEp
        halfmoveClock = prevHalfmove

        activeColor = activeColor.opposite()
        if (activeColor == PieceColor.BLACK) {
            fullmoveNumber--
        }
        return true
    }

    fun getGameStatus(): GameStatus {
        val legalMoves = generateLegalMoves()
        val inCheck = isInCheck(activeColor)

        return when {
            legalMoves.isEmpty() && inCheck -> GameStatus.CHECKMATE
            legalMoves.isEmpty() && !inCheck -> GameStatus.STALEMATE
            inCheck -> GameStatus.CHECK
            else -> GameStatus.IN_PROGRESS
        }
    }

    private fun generateSan(move: Move, allLegalMoves: List<Move>): String {
        if (move.isCastlingKingside || (getPiece(move.from)?.type == PieceType.KING && move.to.file - move.from.file == 2)) {
            return "O-O"
        }
        if (move.isCastlingQueenside || (getPiece(move.from)?.type == PieceType.KING && move.from.file - move.to.file == 2)) {
            return "O-O-O"
        }

        val piece = getPiece(move.from) ?: return move.uci
        val builder = StringBuilder()

        if (piece.type == PieceType.PAWN) {
            if (move.isCapture || move.from.file != move.to.file) {
                builder.append(('a' + move.from.file))
                builder.append('x')
            }
            builder.append(move.to.notation)
            if (move.promotion != null) {
                builder.append('=').append(move.promotion.sanLetter)
            }
        } else {
            builder.append(piece.type.sanLetter)

            // Disambiguation
            val similarMoves = allLegalMoves.filter {
                it != move && it.to == move.to && getPiece(it.from)?.type == piece.type && getPiece(it.from)?.color == piece.color
            }
            if (similarMoves.isNotEmpty()) {
                val sameFile = similarMoves.any { it.from.file == move.from.file }
                val sameRank = similarMoves.any { it.from.rank == move.from.rank }
                if (!sameFile) {
                    builder.append(('a' + move.from.file))
                } else if (!sameRank) {
                    builder.append(move.from.rank + 1)
                } else {
                    builder.append(move.from.notation)
                }
            }

            if (move.isCapture || getPiece(move.to) != null) {
                builder.append('x')
            }
            builder.append(move.to.notation)
        }

        return builder.toString()
    }
}
