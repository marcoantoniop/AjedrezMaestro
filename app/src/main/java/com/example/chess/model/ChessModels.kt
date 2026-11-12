package com.example.chess.model

enum class PieceColor {
    WHITE, BLACK;

    fun opposite(): PieceColor = if (this == WHITE) BLACK else WHITE
}

enum class PieceType(val symbolChar: Char, val baseValue: Int) {
    PAWN('p', 100),
    KNIGHT('n', 320),
    BISHOP('b', 330),
    ROOK('r', 500),
    QUEEN('q', 900),
    KING('k', 20000);

    val sanLetter: String
        get() = when (this) {
            PAWN -> ""
            KNIGHT -> "C" // Caballo (o N en inglés; en español ajedrecístico: C=Caballo, A=Alfil, T=Torre, D=Dama, R=Rey)
            BISHOP -> "A"
            ROOK -> "T"
            QUEEN -> "D"
            KING -> "R"
        }
}

data class Piece(
    val type: PieceType,
    val color: PieceColor
) {
    val fenChar: Char
        get() = if (color == PieceColor.WHITE) type.symbolChar.uppercaseChar() else type.symbolChar
}

data class Square(val file: Int, val rank: Int) {
    init {
        require(file in 0..7 && rank in 0..7) { "Square out of bounds: file=$file, rank=$rank" }
    }

    val notation: String
        get() = "${('a' + file)}${rank + 1}"

    val isLightSquare: Boolean
        get() = (file + rank) % 2 != 0

    override fun toString(): String = notation

    companion object {
        fun fromNotation(notation: String): Square {
            val file = notation[0].lowercaseChar() - 'a'
            val rank = notation[1].digitToInt() - 1
            return Square(file, rank)
        }

        fun fromIndex(index: Int): Square {
            return Square(file = index % 8, rank = index / 8)
        }
    }
}

data class Move(
    val from: Square,
    val to: Square,
    val promotion: PieceType? = null,
    val isCapture: Boolean = false,
    val isCastlingKingside: Boolean = false,
    val isCastlingQueenside: Boolean = false,
    val isEnPassant: Boolean = false,
    var san: String = ""
) {
    val uci: String
        get() = "${from.notation}${to.notation}${promotion?.symbolChar ?: ""}"

    override fun toString(): String = if (san.isNotEmpty()) san else uci
}

enum class MoveQuality(
    val labelEs: String,
    val symbol: String
) {
    BRILLIANT("Brillante", "!!"),
    BEST("La mejor", "★"),
    EXCELLENT("Excelente", "!"),
    GOOD("Buena", ""),
    INACCURACY("Imprecisión", "?!"),
    MISTAKE("Error", "?"),
    BLUNDER("Grave error", "??")
}

data class EvalResult(
    val scoreCentipawns: Int,
    val mateInMoves: Int? = null,
    val bestMove: Move? = null,
    val principalVariation: List<Move> = emptyList()
) {
    val formattedScore: String
        get() = when {
            mateInMoves != null -> if (mateInMoves > 0) "+M$mateInMoves" else "-M${-mateInMoves}"
            scoreCentipawns > 0 -> "+${String.format(java.util.Locale.US, "%.1f", scoreCentipawns / 100.0)}"
            scoreCentipawns < 0 -> String.format(java.util.Locale.US, "%.1f", scoreCentipawns / 100.0)
            else -> "0.0"
        }

    val whiteWinningPercent: Float
        get() {
            if (mateInMoves != null) {
                return if (mateInMoves > 0) 100f else 0f
            }
            // Sigmoid-like conversion for evaluation bar
            val winChance = 1.0 / (1.0 + Math.pow(10.0, -scoreCentipawns / 400.0))
            return (winChance * 100.0).toFloat().coerceIn(2f, 98f)
        }
}
