package com.example.chess.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chess.data.entity.TrainingSessionEntity
import com.example.chess.data.entity.UserStatsEntity
import com.example.chess.ui.viewmodel.AppScreen
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
fun DashboardScreen(
    userStats: UserStatsEntity?,
    recentSessions: List<TrainingSessionEntity>,
    currentUser: com.google.firebase.auth.FirebaseUser?,
    onNavigate: (AppScreen) -> Unit,
    onOpenAuth: () -> Unit,
    modifier: Modifier = Modifier
) {
    val elo = userStats?.currentElo ?: 1200
    val maxElo = userStats?.highestElo ?: 1200
    val solved = userStats?.puzzlesSolved ?: 0
    val streak = userStats?.streakDays ?: 1
    val accuracy = userStats?.accuracyPercent ?: 100f

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))

            // Cloud Auth Status Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenAuth() }
                    .testTag("auth_status_banner"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF221C18)),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (currentUser != null) Color(0xFF4CAF50).copy(alpha = 0.5f) else Color(0xFFE5A93C).copy(alpha = 0.5f)
                )
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
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(if (currentUser != null) Color(0xFF1E2D1F) else Color(0xFF382F26)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (currentUser != null) "G" else "☁",
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (currentUser != null) Color(0xFF4CAF50) else GoldPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (currentUser != null) (currentUser.displayName ?: currentUser.email ?: "Usuario Google") else "Modo Local / Invitado",
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = if (currentUser != null) "Sincronizado con Cloud Firestore" else "Toca para sincronizar con Google",
                                fontFamily = OutfitFontFamily,
                                fontSize = 10.sp,
                                color = if (currentUser != null) Color(0xFF81C784) else GoldPrimary
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF2E2620))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (currentUser != null) "Nube Activa" else "Conectar",
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = if (currentUser != null) Color(0xFF81C784) else GoldPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Header Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "AJEDREZ MAESTRO",
                        fontFamily = CinzelFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = GoldPrimary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Entrenador Táctico y Analizador",
                        fontFamily = OutfitFontFamily,
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2C241E))
                        .border(1.5.dp, GoldPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Trophy",
                        tint = GoldPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }

        // Elo Rating Hero Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("elo_hero_card"),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3F352E))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "NIVEL DE HABILIDAD",
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp,
                                color = TextSecondary,
                                letterSpacing = 1.sp
                            )
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "$elo",
                                    fontFamily = CinzelFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 38.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = " ELO",
                                    fontFamily = OutfitFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = GoldPrimary,
                                    modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                                )
                            }
                        }

                        // Category Title Badge
                        val rankTitle = when {
                            elo >= 2000 -> "Maestro FIDE"
                            elo >= 1800 -> "Candidato Maestro"
                            elo >= 1600 -> "Experto Táctico"
                            elo >= 1400 -> "Jugador de Club"
                            else -> "Aficionado Avanzado"
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFF382F26))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = rankTitle,
                                fontFamily = OutfitFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = GoldPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3 stats pillars
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1B1714))
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatMiniItem(icon = Icons.Default.AutoGraph, value = "$maxElo", label = "Máx Elo")
                        Box(modifier = Modifier.width(1.dp).height(28.dp).background(Color(0xFF332B25)))
                        StatMiniItem(icon = Icons.Default.Psychology, value = "$solved", label = "Resueltos")
                        Box(modifier = Modifier.width(1.dp).height(28.dp).background(Color(0xFF332B25)))
                        StatMiniItem(icon = Icons.Default.LocalFireDepartment, value = "$streak d", label = "Racha")
                    }
                }
            }
        }

        // Training Launchers Section
        item {
            Text(
                text = "MÓDULOS DE ENTRENAMIENTO",
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = TextSecondary,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
        }

        item {
            TrainingModuleCard(
                title = "Escalera de Retos (1000 - 2400 Elo)",
                subtitle = "6 niveles de dificultad con progresión oficial y medallas de maestría",
                icon = Icons.Default.Stars,
                badge = "6 Niveles",
                accentColor = Color(0xFFFFB300),
                onClick = { onNavigate(AppScreen.CHALLENGES) },
                testTag = "btn_nav_challenges"
            )
        }

        item {
            TrainingModuleCard(
                title = "Entrenador Táctico por Temas",
                subtitle = "Clavadas, tenedores, jaques dobles, redes de mate y cálculo de variantes",
                icon = Icons.Default.FitnessCenter,
                badge = "21 Retos",
                accentColor = GoldPrimary,
                onClick = { onNavigate(AppScreen.TACTICS) },
                testTag = "btn_nav_tactics"
            )
        }

        item {
            TrainingModuleCard(
                title = "Repertorio de Aperturas",
                subtitle = "Domina la Siciliana, Española Ruy López, Francesa y Gambito de Dama",
                icon = Icons.Default.MenuBook,
                badge = "6 Aperturas",
                accentColor = Color(0xFF64B5F6),
                onClick = { onNavigate(AppScreen.OPENINGS) },
                testTag = "btn_nav_openings"
            )
        }

        item {
            TrainingModuleCard(
                title = "Tablero de Análisis con Motor",
                subtitle = "Evaluación posicional en centipeones, barra de ventaja y flechas dinámicas",
                icon = Icons.Default.Search,
                badge = "Motor Activo",
                accentColor = Color(0xFF81C784),
                onClick = { onNavigate(AppScreen.ANALYSIS) },
                testTag = "btn_nav_analysis"
            )
        }

        // Recent Activity Sessions
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "HISTORIAL RECIENTE",
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Ver todo",
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = GoldPrimary,
                    modifier = Modifier.clickable { onNavigate(AppScreen.STATS) }
                )
            }
        }

        if (recentSessions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Comienza tu primer entrenamiento de táctica para registrar tus estadísticas.",
                            fontFamily = OutfitFontFamily,
                            fontSize = 13.sp,
                            color = TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(recentSessions.take(4)) { session ->
                SessionItemRow(session)
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatMiniItem(icon: ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = value,
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimary
            )
        }
        Text(
            text = label,
            fontFamily = OutfitFontFamily,
            fontSize = 10.sp,
            color = TextSecondary
        )
    }
}

@Composable
private fun TrainingModuleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badge: String,
    accentColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF382E27))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.15f))
                    .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextPrimary
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF2C241E))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badge,
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.Medium,
                            fontSize = 10.sp,
                            color = accentColor
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontFamily = OutfitFontFamily,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun SessionItemRow(session: TrainingSessionEntity) {
    val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
    val isPositive = session.eloChange >= 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceCard),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF332B25))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${session.sessionType}: ${session.category}",
                    fontFamily = OutfitFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
                Text(
                    text = "${dateFormat.format(Date(session.timestamp))} • Precisión ${session.accuracyPercent}%",
                    fontFamily = OutfitFontFamily,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            Text(
                text = if (isPositive) "+${session.eloChange} Elo" else "${session.eloChange} Elo",
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (isPositive) MoveBest else MoveBlunder
            )
        }
    }
}
