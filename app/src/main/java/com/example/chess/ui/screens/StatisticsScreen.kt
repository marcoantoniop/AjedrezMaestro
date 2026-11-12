package com.example.chess.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import com.example.chess.auth.AuthManager
import com.example.chess.data.entity.SavedGameEntity
import com.example.chess.data.entity.TrainingSessionEntity
import com.example.chess.ui.viewmodel.ChessViewModel
import com.example.ui.theme.CinzelFontFamily
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.MoveBest
import com.example.ui.theme.MoveBlunder
import com.example.ui.theme.OutfitFontFamily
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StatisticsScreen(
    viewModel: ChessViewModel,
    onOpenAuth: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }
    val currentUser by viewModel.currentUser.collectAsState()
    val userStats by viewModel.userStats.collectAsState()
    val allSessions by viewModel.allSessions.collectAsState()
    val savedGames by viewModel.savedGames.collectAsState()

    var showSignOutDialog by remember { mutableStateOf(false) }

    val elo = userStats?.currentElo ?: 1200
    val maxElo = userStats?.highestElo ?: 1200
    val solved = userStats?.puzzlesSolved ?: 0
    val failed = userStats?.puzzlesFailed ?: 0
    val total = solved + failed
    val accuracy = if (total > 0) (solved.toFloat() / total * 100f).toInt() else 100
    val streak = userStats?.streakDays ?: 1

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "ESTADÍSTICAS Y PROGRESO",
                fontFamily = CinzelFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = GoldPrimary,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "Registro histórico de rendimiento táctico y partidas",
                fontFamily = OutfitFontFamily,
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        // User Profile & Cloud Identity Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF382E27))
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
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(if (currentUser != null) Color(0xFF2C3E2D) else Color(0xFF382F26)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (currentUser != null) (currentUser?.displayName?.take(1)?.uppercase() ?: "G") else "?",
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = if (currentUser != null) Color(0xFF81C784) else GoldPrimary
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = if (currentUser != null) (currentUser?.displayName ?: "Usuario Google") else "Usuario Invitado",
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = if (currentUser != null) (currentUser?.email ?: "Sincronización en la nube activa") else "Progreso guardado sólo en este dispositivo",
                                fontFamily = OutfitFontFamily,
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    if (currentUser != null) {
                        IconButton(
                            onClick = { showSignOutDialog = true },
                            modifier = Modifier.testTag("btn_sign_out")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Cerrar sesión",
                                tint = Color(0xFFEF5350),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        Button(
                            onClick = onOpenAuth,
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("btn_connect_google")
                        ) {
                            Text("Conectar", color = Color(0xFF1E1402), fontFamily = OutfitFontFamily, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // 4 KPI Cards Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KpiCard(
                        title = "Elo Actual",
                        value = "$elo",
                        subtitle = "Máx: $maxElo",
                        icon = Icons.Default.EmojiEvents,
                        accentColor = GoldPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "Precisión Táctica",
                        value = "$accuracy%",
                        subtitle = "$solved de $total correctos",
                        icon = Icons.Default.CheckCircle,
                        accentColor = MoveBest,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KpiCard(
                        title = "Racha Activa",
                        value = "$streak días",
                        subtitle = "Entrenamiento continuo",
                        icon = Icons.Default.LocalFireDepartment,
                        accentColor = Color(0xFFFF9800),
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "Sesiones Totales",
                        value = "${allSessions.size}",
                        subtitle = "${savedGames.size} partidas guardadas",
                        icon = Icons.Default.History,
                        accentColor = Color(0xFF64B5F6),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Theme Proficiency Breakdown
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF382E27))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "EFECTIVIDAD POR MOTIVOS TÁCTICOS",
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val themes = listOf(
                        Pair("Tenedores y Horquillas", 0.88f),
                        Pair("Clavadas Absolutas y Relativas", 0.76f),
                        Pair("Ataques a la Descubierta", 0.82f),
                        Pair("Redes de Jaque Mate", 0.94f),
                        Pair("Sacrificios Posicionales", 0.68f),
                        Pair("Finales de Peones y Torres", 0.72f)
                    )

                    themes.forEach { (themeName, pct) ->
                        ThemeProgressRow(themeName, pct)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        // Saved Games from Analysis
        item {
            Text(
                text = "PARTIDAS Y POSICIONES GUARDADAS",
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = TextSecondary,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (savedGames.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(18.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Aún no has guardado partidas desde el Tablero de Análisis.",
                            fontFamily = OutfitFontFamily,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        } else {
            items(savedGames) { game ->
                SavedGameItemRow(game = game, onDelete = { viewModel.deleteGame(game.id) })
            }
        }

        // All Training Sessions Log
        item {
            Text(
                text = "HISTORIAL COMPLETO DE SESIONES",
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = TextSecondary,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 6.dp)
            )
        }

        if (allSessions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(18.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No hay registros de entrenamiento en la base de datos.",
                            fontFamily = OutfitFontFamily,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        } else {
            items(allSessions) { session ->
                SessionItemRow(session)
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = {
                Text(
                    text = "Cerrar sesión de Google",
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary
                )
            },
            text = {
                Text(
                    text = "¿Deseas cerrar tu sesión? Tu progreso local se mantendrá disponible en este dispositivo.",
                    fontFamily = OutfitFontFamily,
                    fontSize = 13.sp,
                    color = TextPrimary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSignOutDialog = false
                        AuthManager.signOut(credentialManager, scope)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350))
                ) {
                    Text("Cerrar Sesión", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) {
                    Text("Cancelar", color = TextSecondary)
                }
            },
            containerColor = Color(0xFF26201B)
        )
    }
}

@Composable
private fun KpiCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF382E27))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontFamily = OutfitFontFamily,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                fontFamily = OutfitFontFamily,
                fontSize = 10.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun ThemeProgressRow(themeName: String, pct: Float) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = themeName,
                fontFamily = OutfitFontFamily,
                fontSize = 12.sp,
                color = TextPrimary
            )
            Text(
                text = "${(pct * 100).toInt()}%",
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = GoldPrimary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { pct },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = GoldPrimary,
            trackColor = Color(0xFF2C241E)
        )
    }
}

@Composable
private fun SavedGameItemRow(game: SavedGameEntity, onDelete: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF332B25))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = game.title,
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
                Text(
                    text = "${dateFormat.format(Date(game.timestamp))} • Eval: ${game.evaluation}",
                    fontFamily = OutfitFontFamily,
                    fontSize = 11.sp,
                    color = GoldPrimary
                )
                if (game.userNotes.isNotEmpty()) {
                    Text(
                        text = game.userNotes,
                        fontFamily = OutfitFontFamily,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.testTag("btn_delete_saved_game_${game.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MoveBlunder,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
