package com.example.chess.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.example.chess.model.Piece
import com.example.chess.model.PieceColor
import com.example.chess.model.PieceType

@Composable
fun ChessPieceView(
    piece: Piece,
    size: Dp,
    modifier: Modifier = Modifier
) {
    // Unicode chess glyphs
    val unicodeChar = when (piece.color) {
        PieceColor.WHITE -> when (piece.type) {
            PieceType.KING -> "♔"
            PieceType.QUEEN -> "♕"
            PieceType.ROOK -> "♖"
            PieceType.BISHOP -> "♗"
            PieceType.KNIGHT -> "♘"
            PieceType.PAWN -> "♙"
        }
        PieceColor.BLACK -> when (piece.type) {
            PieceType.KING -> "♚"
            PieceType.QUEEN -> "♛"
            PieceType.ROOK -> "♜"
            PieceType.BISHOP -> "♝"
            PieceType.KNIGHT -> "♞"
            PieceType.PAWN -> "♟"
        }
    }

    val isWhite = piece.color == PieceColor.WHITE
    // Rich contrast: White pieces in bright warm ivory with dark subtle shadow, Black pieces in deep obsidian with warm amber rim
    val textColor = if (isWhite) Color(0xFFFFFDF5) else Color(0xFF1E1712)
    val shadowColor = if (isWhite) Color(0x991E140A) else Color(0x66E5A93C)

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = unicodeChar,
            style = TextStyle(
                fontSize = (size.value * 0.76f).sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center,
                shadow = Shadow(
                    color = shadowColor,
                    blurRadius = if (isWhite) 4f else 3f
                )
            )
        )
    }
}
