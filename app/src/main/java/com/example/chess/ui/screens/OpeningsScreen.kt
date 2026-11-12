package com.example.chess.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.chess.data.dataset.OpeningsDataset
import com.example.chess.ui.components.ChessBoardView
import com.example.chess.ui.viewmodel.ChessViewModel
import com.example.ui.theme.CinzelFontFamily
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.OutfitFontFamily
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun OpeningsScreen(
    viewModel: ChessViewModel,
    modifier: Modifier = Modifier
) {
    val opening by viewModel.currentOpening.collectAsState()
    val game by viewModel.openingsGame.collectAsState()
    val stepIndex by viewModel.openingStepIndex.collectAsState()
    val arrows by viewModel.openingArrows.collectAsState()

    val totalMoves = opening.movesSan.size

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(6.dp))

        // Openings Selector Carousel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OpeningsDataset.openings.forEach { op ->
                val isSelected = op.id == opening.id
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) GoldPrimary else Color(0xFF26201B))
                        .border(
                            1.dp,
                            if (isSelected) GoldPrimary else Color(0xFF3F352C),
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { viewModel.selectOpening(op) }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = op.name.substringBefore(" (").ifEmpty { op.name },
                        fontFamily = OutfitFontFamily,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp,
                        color = if (isSelected) Color(0xFF1E1402) else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Opening Info Header
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
                    Text(
                        text = opening.name,
                        fontFamily = CinzelFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = GoldPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF382F26))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = opening.eco,
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = GoldPrimary
                        )
                    }
                }

                Text(
                    text = opening.description,
                    fontFamily = OutfitFontFamily,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Board
        ChessBoardView(
            game = game,
            selectedSquare = null,
            legalMovesForSelected = emptyList(),
            lastMove = game.getMoveHistory().lastOrNull(),
            isFlipped = false,
            arrows = arrows,
            onSquareClick = { /* Read-only guided board in openings */ },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Stepper Controls
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF382E27))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.resetOpening() },
                    modifier = Modifier.testTag("btn_reset_opening")
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reiniciar", tint = TextSecondary)
                }

                IconButton(
                    onClick = { viewModel.prevOpeningMove() },
                    enabled = stepIndex > 0,
                    modifier = Modifier.testTag("btn_prev_opening_move")
                ) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Anterior", tint = if (stepIndex > 0) GoldPrimary else Color(0xFF4A3D34))
                }

                // Current Move Indicator
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val currentMoveSan = if (stepIndex > 0 && stepIndex <= opening.movesSan.size) {
                        opening.movesSan[stepIndex - 1]
                    } else "Inicio"
                    Text(
                        text = "Jugada $stepIndex / $totalMoves",
                        fontFamily = OutfitFontFamily,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = currentMoveSan,
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = GoldPrimary
                    )
                }

                IconButton(
                    onClick = { viewModel.nextOpeningMove() },
                    enabled = stepIndex < totalMoves,
                    modifier = Modifier.testTag("btn_next_opening_move")
                ) {
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Siguiente", tint = if (stepIndex < totalMoves) GoldPrimary else Color(0xFF4A3D34))
                }

                Box(modifier = Modifier.size(48.dp)) // Spacer alignment
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Master Strategic Plans Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF382E27))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "PLANES ESTRATÉGICOS",
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = GoldPrimary,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // White Plans
                Text(
                    text = "♔ Plan para las Blancas:",
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFFFAF6F0)
                )
                Text(
                    text = opening.plansWhite,
                    fontFamily = OutfitFontFamily,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
                )

                // Black Plans
                Text(
                    text = "♚ Plan para las Negras:",
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFFFAF6F0)
                )
                Text(
                    text = opening.plansBlack,
                    fontFamily = OutfitFontFamily,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
