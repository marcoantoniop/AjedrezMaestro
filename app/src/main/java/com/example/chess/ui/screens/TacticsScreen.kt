package com.example.chess.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chess.data.dataset.TacticsDataset
import com.example.chess.model.PieceColor
import com.example.chess.ui.components.ChessBoardView
import com.example.chess.ui.viewmodel.ChessViewModel
import com.example.chess.ui.viewmodel.PuzzleStatus
import com.example.ui.theme.CinzelFontFamily
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.MoveBest
import com.example.ui.theme.MoveBlunder
import com.example.ui.theme.OutfitFontFamily
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TacticsScreen(
    viewModel: ChessViewModel,
    modifier: Modifier = Modifier
) {
    val puzzle by viewModel.currentPuzzle.collectAsState()
    val game by viewModel.tacticsGame.collectAsState()
    val status by viewModel.puzzleStatus.collectAsState()
    val selectedSquare by viewModel.tacticsSelectedSquare.collectAsState()
    val legalMoves by viewModel.tacticsLegalMoves.collectAsState()
    val lastMove by viewModel.tacticsLastMove.collectAsState()
    val arrows by viewModel.tacticsArrows.collectAsState()
    val selectedTheme by viewModel.selectedTacticsTheme.collectAsState()
    val eloDelta by viewModel.tacticsEloDelta.collectAsState()

    val isFlipped = puzzle.turn == PieceColor.BLACK

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(6.dp))

        // Themes Horizontal Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TacticsDataset.getThemes().forEach { theme ->
                val isSelected = theme == selectedTheme
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) GoldPrimary else Color(0xFF26201B))
                        .border(
                            1.dp,
                            if (isSelected) GoldPrimary else Color(0xFF3F352C),
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { viewModel.selectTacticsTheme(theme) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = theme,
                        fontFamily = OutfitFontFamily,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp,
                        color = if (isSelected) Color(0xFF1E1402) else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Puzzle Header Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF382E27))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (puzzle.turn == PieceColor.WHITE) Color.White else Color(0xFF1E1712))
                                .border(1.dp, Color(0xFF8A7F74), CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (puzzle.turn == PieceColor.WHITE) "Juegan Blancas" else "Juegan Negras",
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = GoldPrimary
                        )
                    }

                    Text(
                        text = "${puzzle.difficultyElo} Elo • ${puzzle.theme}",
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = puzzle.title,
                    fontFamily = CinzelFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextPrimary
                )

                Text(
                    text = puzzle.description,
                    fontFamily = OutfitFontFamily,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Chess Board
        ChessBoardView(
            game = game,
            selectedSquare = selectedSquare,
            legalMovesForSelected = legalMoves,
            lastMove = lastMove,
            isFlipped = isFlipped,
            arrows = arrows,
            onSquareClick = { viewModel.onTacticsSquareClick(it) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Status Feedback Banners
        AnimatedVisibility(
            visible = status == PuzzleStatus.SOLVED_SUCCESS,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2D1F)),
                border = androidx.compose.foundation.BorderStroke(1.dp, MoveBest),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = MoveBest,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "¡Excelente Resolución!",
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MoveBest
                        )
                        Text(
                            text = "Has encontrado la maniobra táctica óptima (${eloDelta?.let { if (it >= 0) "+$it" else "$it" } ?: "+10"} Elo)",
                            fontFamily = OutfitFontFamily,
                            fontSize = 12.sp,
                            color = TextPrimary
                        )
                    }
                    Button(
                        onClick = { viewModel.nextPuzzle() },
                        colors = ButtonDefaults.buttonColors(containerColor = MoveBest),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("btn_next_puzzle_success")
                    ) {
                        Text("Siguiente", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = status == PuzzleStatus.FAILED_TRY_AGAIN,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF331D1C)),
                border = androidx.compose.foundation.BorderStroke(1.dp, MoveBlunder),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Esa jugada no es la más precisa. ¡Inténtalo de nuevo!",
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = Color(0xFFFF8A80)
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = status == PuzzleStatus.SOLUTION_SHOWN,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF29221C)),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Solución demostrada con flechas en el tablero.",
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = GoldPrimary
                    )
                    Text(
                        text = "Secuencia ganadora: ${puzzle.solutionSan.joinToString(" ")}",
                        fontFamily = OutfitFontFamily,
                        fontSize = 12.sp,
                        color = TextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.requestHint() },
                modifier = Modifier
                    .weight(1f)
                    .testTag("btn_hint"),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldPrimary),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4A3D32)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Pista", fontFamily = OutfitFontFamily, fontSize = 12.sp)
            }

            OutlinedButton(
                onClick = { viewModel.showSolution() },
                modifier = Modifier
                    .weight(1f)
                    .testTag("btn_solution"),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF81C784)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4A3D32)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Solución", fontFamily = OutfitFontFamily, fontSize = 12.sp)
            }

            Button(
                onClick = { viewModel.nextPuzzle() },
                modifier = Modifier
                    .weight(1f)
                    .testTag("btn_next_puzzle"),
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Saltar", color = Color(0xFF1E1402), fontFamily = OutfitFontFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = Color(0xFF1E1402), modifier = Modifier.size(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
