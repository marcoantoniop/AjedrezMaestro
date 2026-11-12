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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chess.data.dataset.TacticalPuzzle
import com.example.chess.data.dataset.TacticsDataset
import com.example.chess.ui.viewmodel.AppScreen
import com.example.chess.ui.viewmodel.ChessViewModel
import com.example.ui.theme.CinzelFontFamily
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.MoveBest
import com.example.ui.theme.OutfitFontFamily
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ChallengesScreen(
    viewModel: ChessViewModel,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTierId by remember { mutableStateOf("tier_1") }
    val userStats by viewModel.userStats.collectAsState()
    val allProgress by viewModel.allProgress.collectAsState()

    val currentTier = TacticsDataset.eloTiers.find { it.id == selectedTierId } ?: TacticsDataset.eloTiers.first()
    val puzzlesInTier = TacticsDataset.puzzles.filter { it.eloTier == currentTier.eloRange.replace(" ", "") }

    val userElo = userStats?.currentElo ?: 1200
    val solvedIds = allProgress.filter { it.solvedCorrectly }.map { it.puzzleId }.toSet()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ESCALERA DE RETOS",
                        fontFamily = CinzelFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = GoldPrimary,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Progresión de maestría táctica de 1000 a 2400 Elo",
                        fontFamily = OutfitFontFamily,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF382F26))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "$userElo ELO",
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = GoldPrimary
                    )
                }
            }
        }

        // Tier Horizontal Carousel
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TacticsDataset.eloTiers.forEach { tier ->
                    val isSelected = tier.id == selectedTierId
                    val isCurrentPlayerTier = userElo in tier.minElo..tier.maxElo
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) GoldPrimary else Color(0xFF26201B))
                            .border(
                                1.5.dp,
                                if (isSelected) GoldPrimary else if (isCurrentPlayerTier) Color(0xFF81C784) else Color(0xFF3F352C),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { selectedTierId = tier.id }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = tier.eloRange,
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isSelected) Color(0xFF1E1402) else TextPrimary
                            )
                            Text(
                                text = tier.badgeLabel,
                                fontFamily = OutfitFontFamily,
                                fontSize = 10.sp,
                                color = if (isSelected) Color(0xFF3E2D0D) else TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Active Tier Info Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF382E27))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = currentTier.name.uppercase(),
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = GoldPrimary,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = currentTier.description,
                                fontFamily = OutfitFontFamily,
                                fontSize = 12.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        val solvedInTier = puzzlesInTier.count { it.id in solvedIds }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF2B231D))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "$solvedInTier / ${puzzlesInTier.size}",
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MoveBest
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val progressFraction = if (puzzlesInTier.isNotEmpty()) {
                        puzzlesInTier.count { it.id in solvedIds }.toFloat() / puzzlesInTier.size
                    } else 0f

                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = GoldPrimary,
                        trackColor = Color(0xFF2B231D)
                    )
                }
            }
        }

        // Puzzles List in Tier
        item {
            Text(
                text = "RETOS DISPONIBLES EN ESTE NIVEL",
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = TextSecondary,
                letterSpacing = 1.sp
            )
        }

        items(puzzlesInTier) { puzzle ->
            val isSolved = puzzle.id in solvedIds

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.loadPuzzle(puzzle)
                        onNavigate(AppScreen.TACTICS)
                    }
                    .testTag("challenge_puzzle_${puzzle.id}"),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSolved) MoveBest.copy(alpha = 0.5f) else Color(0xFF382E27)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(if (isSolved) MoveBest.copy(alpha = 0.15f) else Color(0xFF2E2620)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isSolved) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = if (isSolved) MoveBest else GoldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = puzzle.title,
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "${puzzle.difficultyElo} Elo • ${puzzle.theme}",
                                fontFamily = OutfitFontFamily,
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSolved) Color(0xFF1E2D1F) else Color(0xFF382F26))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isSolved) "Superado" else "Jugar",
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = if (isSolved) MoveBest else GoldPrimary
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
