package com.example.chess.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chess.engine.ChessGame
import com.example.chess.model.Move
import com.example.chess.model.Piece
import com.example.chess.model.PieceColor
import com.example.chess.model.PieceType
import com.example.chess.model.Square
import com.example.ui.theme.BoardDarkSquare
import com.example.ui.theme.BoardLastMoveSquare
import com.example.ui.theme.BoardLightSquare
import com.example.ui.theme.BoardSelectedSquare
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.OutfitFontFamily
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

data class TacticalArrow(
    val from: Square,
    val to: Square,
    val color: Color = Color(0xCC4CAF50) // Green default
)

@Composable
fun ChessBoardView(
    game: ChessGame,
    selectedSquare: Square?,
    legalMovesForSelected: List<Move>,
    lastMove: Move?,
    isFlipped: Boolean = false,
    arrows: List<TacticalArrow> = emptyList(),
    onSquareClick: (Square) -> Unit,
    modifier: Modifier = Modifier
) {
    val checkKingSquare = if (game.isInCheck()) game.getKingSquare(game.activeColor) else null

    BoxWithConstraints(
        modifier = modifier
            .aspectRatio(1f)
            .shadow(12.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF2C241E))
            .border(3.dp, Color(0xFF4A3D34), RoundedCornerShape(12.dp))
            .padding(4.dp)
            .testTag("chess_board")
    ) {
        val squareSize = maxWidth / 8

        Box(modifier = Modifier.fillMaxSize()) {
            // Draw 64 squares
            for (r in 0..7) {
                for (f in 0..7) {
                    val actualRank = if (isFlipped) r else 7 - r
                    val actualFile = if (isFlipped) 7 - f else f
                    val square = Square(actualFile, actualRank)
                    val isLight = square.isLightSquare

                    val isSelected = square == selectedSquare
                    val isLastMoveSquare = lastMove != null && (square == lastMove.from || square == lastMove.to)
                    val isCheckedKing = square == checkKingSquare

                    val legalMoveTarget = legalMovesForSelected.find { it.to == square }
                    val piece = game.getPiece(square)

                    val squareBg = when {
                        isCheckedKing -> Color(0xD0E53935)
                        isSelected -> BoardSelectedSquare
                        isLastMoveSquare -> BoardLastMoveSquare
                        isLight -> BoardLightSquare
                        else -> BoardDarkSquare
                    }

                    Box(
                        modifier = Modifier
                            .offset(x = squareSize * f, y = squareSize * r)
                            .size(squareSize)
                            .background(squareBg)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onSquareClick(square)
                            }
                            .testTag("square_${square.notation}"),
                        contentAlignment = Alignment.Center
                    ) {
                        // Border highlight if selected
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .border(2.5.dp, GoldPrimary)
                            )
                        }

                        // Piece
                        if (piece != null) {
                            ChessPieceView(
                                piece = piece,
                                size = squareSize
                            )
                        }

                        // Legal move hint
                        if (legalMoveTarget != null) {
                            if (piece == null) {
                                // Small dot for empty square
                                Box(
                                    modifier = Modifier
                                        .size(squareSize * 0.28f)
                                        .clip(CircleShape)
                                        .background(Color(0x701E1A17))
                                )
                            } else {
                                // Ring for capture target
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(3.dp)
                                        .border(3.5.dp, Color(0x991E1A17), CircleShape)
                                )
                            }
                        }

                        // Rank label on leftmost column (f == 0)
                        if (f == 0) {
                            Text(
                                text = "${actualRank + 1}",
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                color = if (isLight) BoardDarkSquare else BoardLightSquare,
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(start = 2.dp, top = 1.dp)
                            )
                        }

                        // File label on bottom row (r == 7)
                        if (r == 7) {
                            Text(
                                text = "${'a' + actualFile}",
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                color = if (isLight) BoardDarkSquare else BoardLightSquare,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(end = 2.dp, bottom = 1.dp)
                            )
                        }
                    }
                }
            }

            // Arrow Layer (Drawn over squares and pieces)
            if (arrows.isNotEmpty()) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    for (arrow in arrows) {
                        val fromCol = if (isFlipped) 7 - arrow.from.file else arrow.from.file
                        val fromRow = if (isFlipped) arrow.from.rank else 7 - arrow.from.rank
                        val toCol = if (isFlipped) 7 - arrow.to.file else arrow.to.file
                        val toRow = if (isFlipped) arrow.to.rank else 7 - arrow.to.rank

                        val sqPx = size.width / 8f
                        val start = Offset(
                            x = fromCol * sqPx + sqPx / 2f,
                            y = fromRow * sqPx + sqPx / 2f
                        )
                        val end = Offset(
                            x = toCol * sqPx + sqPx / 2f,
                            y = toRow * sqPx + sqPx / 2f
                        )

                        val dx = end.x - start.x
                        val dy = end.y - start.y
                        val angle = atan2(dy, dx)
                        val arrowHeadSize = sqPx * 0.42f

                        // Shorten end slightly so arrowhead centers nicely
                        val shortenedEnd = Offset(
                            x = end.x - (arrowHeadSize * 0.35f * cos(angle).toFloat()),
                            y = end.y - (arrowHeadSize * 0.35f * sin(angle).toFloat())
                        )

                        // Draw shaft
                        drawLine(
                            color = arrow.color,
                            start = start,
                            end = shortenedEnd,
                            strokeWidth = sqPx * 0.18f,
                            cap = StrokeCap.Round
                        )

                        // Draw arrowhead
                        val path = Path().apply {
                            val tipX = end.x
                            val tipY = end.y
                            moveTo(tipX, tipY)
                            lineTo(
                                (tipX - arrowHeadSize * cos(angle - Math.PI / 6.0)).toFloat(),
                                (tipY - arrowHeadSize * sin(angle - Math.PI / 6.0)).toFloat()
                            )
                            lineTo(
                                (tipX - arrowHeadSize * cos(angle + Math.PI / 6.0)).toFloat(),
                                (tipY - arrowHeadSize * sin(angle + Math.PI / 6.0)).toFloat()
                            )
                            close()
                        }
                        drawPath(path, color = arrow.color)
                    }
                }
            }
        }
    }
}
