package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chess.auth.AuthManager
import com.example.chess.ui.screens.AnalysisScreen
import com.example.chess.ui.screens.AuthScreen
import com.example.chess.ui.screens.ChallengesScreen
import com.example.chess.ui.screens.DashboardScreen
import com.example.chess.ui.screens.OpeningsScreen
import com.example.chess.ui.screens.StatisticsScreen
import com.example.chess.ui.screens.TacticsScreen
import com.example.chess.ui.viewmodel.AppScreen
import com.example.chess.ui.viewmodel.ChessViewModel
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.OutfitFontFamily
import com.example.ui.theme.TextSecondary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ChessMasterApp()
            }
        }
    }
}

@Composable
fun ChessMasterApp(
    viewModel: ChessViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    val currentScreen by viewModel.currentScreen.collectAsState()
    val userStats by viewModel.userStats.collectAsState()
    val allSessions by viewModel.allSessions.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var showAuthScreen by remember { mutableStateOf(false) }

    // Silent auto-sign-in on app startup
    LaunchedEffect(Unit) {
        AuthManager.attemptAutoSignIn(
            context = context,
            credentialManager = credentialManager,
            onAuthSuccess = { /* Automatically updated via currentUser flow */ },
            onUnauthenticated = { /* Continue in local mode */ },
            scope = scope
        )
    }

    // Handle back button: return to dashboard if on another screen or close auth overlay
    BackHandler(enabled = showAuthScreen || currentScreen != AppScreen.DASHBOARD) {
        if (showAuthScreen) {
            showAuthScreen = false
        } else {
            viewModel.navigateTo(AppScreen.DASHBOARD)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = DarkBackground,
        bottomBar = {
            if (!showAuthScreen) {
                NavigationBar(
                    containerColor = Color(0xFF1B1714),
                    contentColor = TextSecondary,
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .testTag("bottom_nav_bar")
                ) {
                    val navItems = listOf(
                        Triple(AppScreen.DASHBOARD, "Inicio", Pair(Icons.Filled.Home, Icons.Outlined.Home)),
                        Triple(AppScreen.CHALLENGES, "Retos", Pair(Icons.Filled.Stars, Icons.Outlined.Stars)),
                        Triple(AppScreen.TACTICS, "Táctica", Pair(Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter)),
                        Triple(AppScreen.ANALYSIS, "Análisis", Pair(Icons.Filled.Search, Icons.Outlined.Search)),
                        Triple(AppScreen.STATS, "Progreso", Pair(Icons.Filled.AutoGraph, Icons.Outlined.AutoGraph))
                    )

                    navItems.forEach { (screen, label, icons) ->
                        val isSelected = currentScreen == screen
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.navigateTo(screen) },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) icons.first else icons.second,
                                    contentDescription = label
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontFamily = OutfitFontFamily,
                                    fontSize = 10.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF1E1402),
                                selectedTextColor = GoldPrimary,
                                indicatorColor = GoldPrimary,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary
                            ),
                            modifier = Modifier.testTag("nav_item_${screen.name.lowercase()}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBackground)
        ) {
            when (currentScreen) {
                AppScreen.DASHBOARD -> DashboardScreen(
                    userStats = userStats,
                    recentSessions = allSessions,
                    currentUser = currentUser,
                    onNavigate = { viewModel.navigateTo(it) },
                    onOpenAuth = { showAuthScreen = true }
                )
                AppScreen.CHALLENGES -> ChallengesScreen(
                    viewModel = viewModel,
                    onNavigate = { viewModel.navigateTo(it) }
                )
                AppScreen.TACTICS -> TacticsScreen(
                    viewModel = viewModel
                )
                AppScreen.OPENINGS -> OpeningsScreen(
                    viewModel = viewModel
                )
                AppScreen.ANALYSIS -> AnalysisScreen(
                    viewModel = viewModel
                )
                AppScreen.STATS -> StatisticsScreen(
                    viewModel = viewModel,
                    onOpenAuth = { showAuthScreen = true }
                )
            }

            // Auth Screen Overlay
            AnimatedVisibility(
                visible = showAuthScreen,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                AuthScreen(
                    onAuthSuccess = { showAuthScreen = false },
                    onContinueOffline = { showAuthScreen = false }
                )
            }
        }
    }
}
