package com.example.chess.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.chess.model.MoveQuality
import com.example.chess.ui.components.ChessBoardView
import com.example.chess.ui.components.EvaluationBarView
import com.example.chess.ui.viewmodel.ChessViewModel
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.MoveBest
import com.example.ui.theme.MoveBlunder
import com.example.ui.theme.MoveBrilliant
import com.example.ui.theme.MoveExcellent
import com.example.ui.theme.MoveGood
import com.example.ui.theme.MoveInaccuracy
import com.example.ui.theme.MoveMistake
import com.example.ui.theme.OutfitFontFamily
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AnalysisScreen(
    viewModel: ChessViewModel,
    modifier: Modifier = Modifier
) {
    val game by viewModel.analysisGame.collectAsState()
    val eval by viewModel.analysisEval.collectAsState()
    val selectedSquare by viewModel.analysisSelectedSquare.collectAsState()
    val legalMoves by viewModel.analysisLegalMoves.collectAsState()
    val lastMove by viewModel.analysisLastMove.collectAsState()
    val arrows by viewModel.analysisArrows.collectAsState()
    val annotatedMoves by viewModel.annotatedMoves.collectAsState()
    val isThinking by viewModel.isEngineThinking.collectAsState()
    val showArrows by viewModel.showEngineArrows.collectAsState()
    val isFlipped by viewModel.isBoardFlipped.collectAsState()

    var showSaveDialog by remember { mutableStateOf(false) }
    var showFenDialog by remember { mutableStateOf(false) }
    var saveTitle by remember { mutableStateOf("") }
    var saveNotes by remember { mutableStateOf("") }
    var customFen by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(6.dp))

        // Evaluation & Engine Top Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF382E27))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(GoldPrimary)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = eval.formattedScore,
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF1E1402)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        val bestMoveSan = eval.bestMove?.san?.ifEmpty { eval.bestMove?.uci } ?: "Analizando..."
                        Text(
                            text = "Mejor: $bestMoveSan",
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = if (isThinking) "Calculando variantes..." else "Profundidad 3 • Motor Activo",
                            fontFamily = OutfitFontFamily,
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Turn Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF2C241E))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (game.activeColor == com.example.chess.model.PieceColor.WHITE) "Blancas" else "Negras",
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        color = GoldPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Board + Evaluation Bar Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EvaluationBarView(
                evalResult = eval,
                isFlipped = isFlipped,
                modifier = Modifier.height(310.dp)
            )

            ChessBoardView(
                game = game,
                selectedSquare = selectedSquare,
                legalMovesForSelected = legalMoves,
                lastMove = lastMove,
                isFlipped = isFlipped,
                arrows = arrows,
                onSquareClick = { viewModel.onAnalysisSquareClick(it) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Analysis Controls Toolbar
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF382E27))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.undoAnalysisMove() },
                    modifier = Modifier.testTag("btn_undo_move")
                ) {
                    Icon(imageVector = Icons.Default.Undo, contentDescription = "Deshacer", tint = GoldPrimary)
                }

                IconButton(
                    onClick = { viewModel.playEngineMoveInAnalysis() },
                    modifier = Modifier.testTag("btn_engine_play")
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Jugar Motor", tint = MoveBest)
                }

                IconButton(
                    onClick = { viewModel.toggleEngineArrows() },
                    modifier = Modifier.testTag("btn_toggle_arrows")
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Flechas",
                        tint = if (showArrows) GoldPrimary else Color(0xFF5A4C40)
                    )
                }

                IconButton(
                    onClick = { viewModel.toggleBoardFlip() },
                    modifier = Modifier.testTag("btn_flip_board")
                ) {
                    Icon(imageVector = Icons.Default.SwapVert, contentDescription = "Voltear", tint = TextSecondary)
                }

                IconButton(
                    onClick = { showFenDialog = true },
                    modifier = Modifier.testTag("btn_load_fen")
                ) {
                    Icon(imageVector = Icons.Default.Upload, contentDescription = "Cargar FEN", tint = TextSecondary)
                }

                IconButton(
                    onClick = { showSaveDialog = true },
                    modifier = Modifier.testTag("btn_save_analysis")
                ) {
                    Icon(imageVector = Icons.Default.BookmarkBorder, contentDescription = "Guardar", tint = GoldPrimary)
                }

                IconButton(
                    onClick = { viewModel.resetAnalysisBoard() },
                    modifier = Modifier.testTag("btn_reset_analysis")
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reiniciar", tint = MoveBlunder)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Annotated Moves Strip
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF382E27))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "HISTORIAL Y CALIDAD DE JUGADAS",
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (annotatedMoves.isEmpty()) {
                    Text(
                        text = "Mueve las piezas en el tablero libre para recibir análisis táctico en vivo.",
                        fontFamily = OutfitFontFamily,
                        fontSize = 12.sp,
                        color = Color(0xFF8A7F74)
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        for (item in annotatedMoves) {
                            MoveQualityChip(item.move.san, item.quality)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Save Game Dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Guardar Análisis en Base de Datos", color = GoldPrimary, fontFamily = OutfitFontFamily, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Guarda esta posición y secuencia en tu base de datos local Room.", fontSize = 12.sp, color = TextSecondary)
                    OutlinedTextField(
                        value = saveTitle,
                        onValueChange = { saveTitle = it },
                        label = { Text("Título de la partida") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary)
                    )
                    OutlinedTextField(
                        value = saveNotes,
                        onValueChange = { saveNotes = it },
                        label = { Text("Notas estratégicas") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveCurrentAnalysis(saveTitle, saveNotes)
                        showSaveDialog = false
                        saveTitle = ""
                        saveNotes = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text("Guardar", color = Color(0xFF1E1402), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancelar", color = TextSecondary)
                }
            },
            containerColor = Color(0xFF26201B)
        )
    }

    // Load FEN Dialog
    if (showFenDialog) {
        AlertDialog(
            onDismissRequest = { showFenDialog = false },
            title = { Text("Cargar Posición FEN", color = GoldPrimary, fontFamily = OutfitFontFamily, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Pega la cadena FEN para analizar cualquier posición:", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = customFen,
                        onValueChange = { customFen = it },
                        placeholder = { Text("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldPrimary)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customFen.isNotBlank()) {
                            viewModel.loadCustomFen(customFen)
                        }
                        showFenDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text("Cargar", color = Color(0xFF1E1402), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFenDialog = false }) {
                    Text("Cancelar", color = TextSecondary)
                }
            },
            containerColor = Color(0xFF26201B)
        )
    }
}

@Composable
private fun MoveQualityChip(san: String, quality: MoveQuality?) {
    val (bgColor, textColor) = when (quality) {
        MoveQuality.BRILLIANT -> Pair(MoveBrilliant.copy(alpha = 0.2f), MoveBrilliant)
        MoveQuality.BEST -> Pair(MoveBest.copy(alpha = 0.2f), MoveBest)
        MoveQuality.EXCELLENT -> Pair(MoveExcellent.copy(alpha = 0.2f), MoveExcellent)
        MoveQuality.GOOD -> Pair(MoveGood.copy(alpha = 0.2f), MoveGood)
        MoveQuality.INACCURACY -> Pair(MoveInaccuracy.copy(alpha = 0.2f), MoveInaccuracy)
        MoveQuality.MISTAKE -> Pair(MoveMistake.copy(alpha = 0.2f), MoveMistake)
        MoveQuality.BLUNDER -> Pair(MoveBlunder.copy(alpha = 0.2f), MoveBlunder)
        null -> Pair(Color(0xFF2C241E), TextSecondary)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = san,
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = TextPrimary
            )
            if (quality != null && quality.symbol.isNotEmpty()) {
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = quality.symbol,
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = textColor
                )
            }
        }
    }
}
