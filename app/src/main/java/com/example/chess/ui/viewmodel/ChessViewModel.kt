package com.example.chess.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.chess.data.AppDatabase
import com.example.chess.data.ChessRepository
import com.example.chess.data.dataset.OpeningsDataset
import com.example.chess.data.dataset.OpeningVariation
import com.example.chess.data.dataset.TacticalPuzzle
import com.example.chess.data.dataset.TacticsDataset
import com.example.chess.data.entity.SavedGameEntity
import com.example.chess.data.entity.TacticsProgressEntity
import com.example.chess.data.entity.TrainingSessionEntity
import com.example.chess.data.entity.UserStatsEntity
import com.example.chess.engine.ChessEvaluator
import com.example.chess.engine.ChessGame
import com.example.chess.model.EvalResult
import com.example.chess.model.Move
import com.example.chess.model.MoveQuality
import com.example.chess.model.PieceColor
import com.example.chess.model.Square
import com.example.chess.ui.components.TacticalArrow
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class AppScreen {
    DASHBOARD,
    TACTICS,
    CHALLENGES,
    OPENINGS,
    ANALYSIS,
    STATS
}

enum class PuzzleStatus {
    IN_PROGRESS,
    CORRECT_MOVE,
    SOLVED_SUCCESS,
    FAILED_TRY_AGAIN,
    SOLUTION_SHOWN
}

data class AnnotatedMove(
    val move: Move,
    val plyNumber: Int,
    val quality: MoveQuality? = null,
    val evalBefore: EvalResult? = null,
    val evalAfter: EvalResult? = null
)

class ChessViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ChessRepository
    private val firestoreSync: com.example.chess.data.firebase.FirestoreSyncService
    val currentUser = com.example.chess.auth.AuthManager.currentUser

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vm = application.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vm?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        application.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    init {
        val db = AppDatabase.getInstance(application)
        firestoreSync = com.example.chess.data.firebase.FirestoreSyncService(application)
        repository = ChessRepository(db.chessDao(), firestoreSync)
        viewModelScope.launch {
            repository.getOrCreateUserStats()
        }
    }

    val userStats: StateFlow<UserStatsEntity?> = repository.userStats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allSessions: StateFlow<List<TrainingSessionEntity>> = repository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedGames: StateFlow<List<SavedGameEntity>> = repository.savedGames
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProgress: StateFlow<List<TacticsProgressEntity>> = repository.allProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Navigation ---
    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    // --- TACTICS TRAINER STATE ---
    private val _selectedTacticsTheme = MutableStateFlow("Todos")
    val selectedTacticsTheme: StateFlow<String> = _selectedTacticsTheme.asStateFlow()

    private val _currentPuzzle = MutableStateFlow(TacticsDataset.puzzles.first())
    val currentPuzzle: StateFlow<TacticalPuzzle> = _currentPuzzle.asStateFlow()

    private val _tacticsGame = MutableStateFlow(ChessGame(_currentPuzzle.value.initialFen))
    val tacticsGame: StateFlow<ChessGame> = _tacticsGame.asStateFlow()

    private val _puzzleStatus = MutableStateFlow(PuzzleStatus.IN_PROGRESS)
    val puzzleStatus: StateFlow<PuzzleStatus> = _puzzleStatus.asStateFlow()

    private val _tacticsSelectedSquare = MutableStateFlow<Square?>(null)
    val tacticsSelectedSquare: StateFlow<Square?> = _tacticsSelectedSquare.asStateFlow()

    private val _tacticsLegalMoves = MutableStateFlow<List<Move>>(emptyList())
    val tacticsLegalMoves: StateFlow<List<Move>> = _tacticsLegalMoves.asStateFlow()

    private val _tacticsLastMove = MutableStateFlow<Move?>(null)
    val tacticsLastMove: StateFlow<Move?> = _tacticsLastMove.asStateFlow()

    private val _tacticsArrows = MutableStateFlow<List<TacticalArrow>>(emptyList())
    val tacticsArrows: StateFlow<List<TacticalArrow>> = _tacticsArrows.asStateFlow()

    private val _tacticsEloDelta = MutableStateFlow<Int?>(null)
    val tacticsEloDelta: StateFlow<Int?> = _tacticsEloDelta.asStateFlow()

    private var currentSolutionStep = 0
    private var puzzleAttempts = 0
    private var puzzleStartTime = System.currentTimeMillis()

    fun selectTacticsTheme(theme: String) {
        _selectedTacticsTheme.value = theme
        val filtered = if (theme == "Todos") TacticsDataset.puzzles else TacticsDataset.puzzles.filter { it.theme == theme }
        if (filtered.isNotEmpty()) {
            loadPuzzle(filtered.first())
        }
    }

    fun loadPuzzle(puzzle: TacticalPuzzle) {
        _currentPuzzle.value = puzzle
        val game = ChessGame(puzzle.initialFen)
        _tacticsGame.value = game
        _puzzleStatus.value = PuzzleStatus.IN_PROGRESS
        _tacticsSelectedSquare.value = null
        _tacticsLegalMoves.value = emptyList()
        _tacticsLastMove.value = null
        _tacticsArrows.value = emptyList()
        _tacticsEloDelta.value = null
        currentSolutionStep = 0
        puzzleAttempts = 0
        puzzleStartTime = System.currentTimeMillis()
    }

    fun nextPuzzle() {
        val currentList = if (_selectedTacticsTheme.value == "Todos") {
            TacticsDataset.puzzles
        } else {
            TacticsDataset.puzzles.filter { it.theme == _selectedTacticsTheme.value }
        }
        val currentIndex = currentList.indexOfFirst { it.id == _currentPuzzle.value.id }
        val nextIndex = if (currentIndex in 0 until currentList.size - 1) currentIndex + 1 else 0
        loadPuzzle(currentList[nextIndex])
    }

    fun onTacticsSquareClick(square: Square) {
        if (_puzzleStatus.value == PuzzleStatus.SOLVED_SUCCESS || _puzzleStatus.value == PuzzleStatus.SOLUTION_SHOWN) {
            return
        }

        val game = _tacticsGame.value
        val selected = _tacticsSelectedSquare.value

        if (selected == null) {
            val piece = game.getPiece(square)
            if (piece != null && piece.color == game.activeColor) {
                _tacticsSelectedSquare.value = square
                _tacticsLegalMoves.value = game.generateLegalMoves().filter { it.from == square }
            }
        } else {
            val candidateMove = _tacticsLegalMoves.value.find { it.to == square }
            if (candidateMove != null) {
                // Execute player move
                handlePlayerTacticsMove(candidateMove)
            } else {
                val piece = game.getPiece(square)
                if (piece != null && piece.color == game.activeColor) {
                    _tacticsSelectedSquare.value = square
                    _tacticsLegalMoves.value = game.generateLegalMoves().filter { it.from == square }
                } else {
                    _tacticsSelectedSquare.value = null
                    _tacticsLegalMoves.value = emptyList()
                }
            }
        }
    }

    private fun handlePlayerTacticsMove(move: Move) {
        val game = _tacticsGame.value
        val puzzle = _currentPuzzle.value
        val expectedUci = puzzle.solutionMoves.getOrNull(currentSolutionStep)

        game.makeMove(move)
        _tacticsLastMove.value = move
        _tacticsSelectedSquare.value = null
        _tacticsLegalMoves.value = emptyList()
        triggerHaptic()

        if (move.uci == expectedUci) {
            currentSolutionStep++
            _tacticsArrows.value = emptyList()

            if (currentSolutionStep >= puzzle.solutionMoves.size) {
                // Puzzle complete!
                _puzzleStatus.value = PuzzleStatus.SOLVED_SUCCESS
                triggerHapticSuccess()
                val elapsedSec = ((System.currentTimeMillis() - puzzleStartTime) / 1000).toInt()
                viewModelScope.launch {
                    val user = com.example.chess.auth.AuthManager.currentUser.value
                    val delta = repository.recordPuzzleResult(
                        puzzleId = puzzle.id,
                        solvedCorrectly = (puzzleAttempts == 0),
                        attempts = puzzleAttempts + 1,
                        timeSeconds = elapsedSec,
                        puzzleElo = puzzle.difficultyElo,
                        displayName = user?.displayName,
                        email = user?.email
                    )
                    _tacticsEloDelta.value = delta
                    repository.recordTrainingSession(
                        sessionType = "TÁCTICA",
                        category = puzzle.theme,
                        solvedCount = 1,
                        totalCount = 1,
                        eloChange = delta,
                        durationSeconds = elapsedSec
                    )
                }
            } else {
                // Opponent automated response move in puzzle
                _puzzleStatus.value = PuzzleStatus.CORRECT_MOVE
                viewModelScope.launch {
                    delay(450)
                    val opponentUci = puzzle.solutionMoves.getOrNull(currentSolutionStep)
                    if (opponentUci != null) {
                        val legal = game.generateLegalMoves()
                        val opponentMove = legal.find { it.uci == opponentUci }
                        if (opponentMove != null) {
                            game.makeMove(opponentMove)
                            _tacticsLastMove.value = opponentMove
                            currentSolutionStep++
                            triggerHaptic()
                        }
                    }
                    _puzzleStatus.value = PuzzleStatus.IN_PROGRESS
                }
            }
        } else {
            // Incorrect move
            puzzleAttempts++
            _puzzleStatus.value = PuzzleStatus.FAILED_TRY_AGAIN
            viewModelScope.launch {
                delay(600)
                game.undoMove()
                _tacticsLastMove.value = null
                _puzzleStatus.value = PuzzleStatus.IN_PROGRESS
            }
        }
    }

    fun requestHint() {
        val puzzle = _currentPuzzle.value
        val expectedUci = puzzle.solutionMoves.getOrNull(currentSolutionStep) ?: return
        try {
            val from = Square.fromNotation(expectedUci.substring(0, 2))
            val to = Square.fromNotation(expectedUci.substring(2, 4))
            _tacticsArrows.value = listOf(TacticalArrow(from, to, Color(0xCCE5A93C)))
        } catch (_: Exception) {}
    }

    fun showSolution() {
        val puzzle = _currentPuzzle.value
        _puzzleStatus.value = PuzzleStatus.SOLUTION_SHOWN
        val arrows = mutableListOf<TacticalArrow>()
        for (i in puzzle.solutionMoves.indices step 2) {
            val uci = puzzle.solutionMoves[i]
            try {
                val f = Square.fromNotation(uci.substring(0, 2))
                val t = Square.fromNotation(uci.substring(2, 4))
                arrows.add(TacticalArrow(f, t, Color(0xCC4CAF50)))
            } catch (_: Exception) {}
        }
        _tacticsArrows.value = arrows
    }

    // --- OPENINGS TRAINER STATE ---
    private val _currentOpening = MutableStateFlow(OpeningsDataset.openings.first())
    val currentOpening: StateFlow<OpeningVariation> = _currentOpening.asStateFlow()

    private val _openingsGame = MutableStateFlow(ChessGame(_currentOpening.value.initialFen))
    val openingsGame: StateFlow<ChessGame> = _openingsGame.asStateFlow()

    private val _openingStepIndex = MutableStateFlow(0)
    val openingStepIndex: StateFlow<Int> = _openingStepIndex.asStateFlow()

    private val _openingArrows = MutableStateFlow<List<TacticalArrow>>(emptyList())
    val openingArrows: StateFlow<List<TacticalArrow>> = _openingArrows.asStateFlow()

    fun selectOpening(opening: OpeningVariation) {
        _currentOpening.value = opening
        resetOpening()
    }

    fun resetOpening() {
        _openingStepIndex.value = 0
        val game = ChessGame(_currentOpening.value.initialFen)
        _openingsGame.value = game
        updateOpeningArrow()
    }

    fun nextOpeningMove() {
        val opening = _currentOpening.value
        val step = _openingStepIndex.value
        if (step < opening.movesUci.size) {
            val uci = opening.movesUci[step]
            val game = _openingsGame.value
            val legal = game.generateLegalMoves()
            val move = legal.find { it.uci == uci }
            if (move != null) {
                game.makeMove(move)
                _openingStepIndex.value = step + 1
                triggerHaptic()
                updateOpeningArrow()
            }
        }
    }

    fun prevOpeningMove() {
        val step = _openingStepIndex.value
        if (step > 0) {
            val game = _openingsGame.value
            game.undoMove()
            _openingStepIndex.value = step - 1
            triggerHaptic()
            updateOpeningArrow()
        }
    }

    private fun updateOpeningArrow() {
        val opening = _currentOpening.value
        val step = _openingStepIndex.value
        if (step < opening.movesUci.size) {
            val nextUci = opening.movesUci[step]
            try {
                val f = Square.fromNotation(nextUci.substring(0, 2))
                val t = Square.fromNotation(nextUci.substring(2, 4))
                val isWhiteMove = step % 2 == 0
                val color = if (isWhiteMove) Color(0xCC4CAF50) else Color(0xCC42A5F5)
                _openingArrows.value = listOf(TacticalArrow(f, t, color))
            } catch (_: Exception) {
                _openingArrows.value = emptyList()
            }
        } else {
            _openingArrows.value = emptyList()
        }
    }

    // --- ANALYSIS BOARD STATE ---
    private val _analysisGame = MutableStateFlow(ChessGame())
    val analysisGame: StateFlow<ChessGame> = _analysisGame.asStateFlow()

    private val _analysisEval = MutableStateFlow(EvalResult(scoreCentipawns = 25))
    val analysisEval: StateFlow<EvalResult> = _analysisEval.asStateFlow()

    private val _analysisSelectedSquare = MutableStateFlow<Square?>(null)
    val analysisSelectedSquare: StateFlow<Square?> = _analysisSelectedSquare.asStateFlow()

    private val _analysisLegalMoves = MutableStateFlow<List<Move>>(emptyList())
    val analysisLegalMoves: StateFlow<List<Move>> = _analysisLegalMoves.asStateFlow()

    private val _analysisLastMove = MutableStateFlow<Move?>(null)
    val analysisLastMove: StateFlow<Move?> = _analysisLastMove.asStateFlow()

    private val _analysisArrows = MutableStateFlow<List<TacticalArrow>>(emptyList())
    val analysisArrows: StateFlow<List<TacticalArrow>> = _analysisArrows.asStateFlow()

    private val _annotatedMoves = MutableStateFlow<List<AnnotatedMove>>(emptyList())
    val annotatedMoves: StateFlow<List<AnnotatedMove>> = _annotatedMoves.asStateFlow()

    private val _isEngineThinking = MutableStateFlow(false)
    val isEngineThinking: StateFlow<Boolean> = _isEngineThinking.asStateFlow()

    private val _showEngineArrows = MutableStateFlow(true)
    val showEngineArrows: StateFlow<Boolean> = _showEngineArrows.asStateFlow()

    private val _isBoardFlipped = MutableStateFlow(false)
    val isBoardFlipped: StateFlow<Boolean> = _isBoardFlipped.asStateFlow()

    private var evalJob: Job? = null

    init {
        runAnalysisEngine()
    }

    fun toggleBoardFlip() {
        _isBoardFlipped.value = !_isBoardFlipped.value
    }

    fun toggleEngineArrows() {
        _showEngineArrows.value = !_showEngineArrows.value
        if (!_showEngineArrows.value) {
            _analysisArrows.value = emptyList()
        } else {
            val best = _analysisEval.value.bestMove
            if (best != null) {
                _analysisArrows.value = listOf(TacticalArrow(best.from, best.to, Color(0xCC4CAF50)))
            }
        }
    }

    fun onAnalysisSquareClick(square: Square) {
        val game = _analysisGame.value
        val selected = _analysisSelectedSquare.value

        if (selected == null) {
            val piece = game.getPiece(square)
            if (piece != null && piece.color == game.activeColor) {
                _analysisSelectedSquare.value = square
                _analysisLegalMoves.value = game.generateLegalMoves().filter { it.from == square }
            }
        } else {
            val candidate = _analysisLegalMoves.value.find { it.to == square }
            if (candidate != null) {
                makeAnalysisMove(candidate)
            } else {
                val piece = game.getPiece(square)
                if (piece != null && piece.color == game.activeColor) {
                    _analysisSelectedSquare.value = square
                    _analysisLegalMoves.value = game.generateLegalMoves().filter { it.from == square }
                } else {
                    _analysisSelectedSquare.value = null
                    _analysisLegalMoves.value = emptyList()
                }
            }
        }
    }

    private fun makeAnalysisMove(move: Move) {
        val game = _analysisGame.value
        val evalBefore = _analysisEval.value
        val wasWhiteTurn = game.activeColor == PieceColor.WHITE

        game.makeMove(move)
        _analysisLastMove.value = move
        _analysisSelectedSquare.value = null
        _analysisLegalMoves.value = emptyList()
        triggerHaptic()

        runAnalysisEngine { evalAfter ->
            val quality = ChessEvaluator.classifyMoveQuality(
                evalBefore = evalBefore,
                evalAfter = evalAfter,
                isWhiteTurn = wasWhiteTurn
            )
            val annotated = AnnotatedMove(
                move = move,
                plyNumber = game.getMoveHistory().size,
                quality = quality,
                evalBefore = evalBefore,
                evalAfter = evalAfter
            )
            _annotatedMoves.value = _annotatedMoves.value + annotated
        }
    }

    fun undoAnalysisMove() {
        val game = _analysisGame.value
        if (game.undoMove()) {
            _analysisLastMove.value = game.getMoveHistory().lastOrNull()
            _analysisSelectedSquare.value = null
            _analysisLegalMoves.value = emptyList()
            if (_annotatedMoves.value.isNotEmpty()) {
                _annotatedMoves.value = _annotatedMoves.value.dropLast(1)
            }
            triggerHaptic()
            runAnalysisEngine()
        }
    }

    fun resetAnalysisBoard() {
        val game = ChessGame()
        _analysisGame.value = game
        _analysisLastMove.value = null
        _analysisSelectedSquare.value = null
        _analysisLegalMoves.value = emptyList()
        _annotatedMoves.value = emptyList()
        runAnalysisEngine()
    }

    fun playEngineMoveInAnalysis() {
        val best = _analysisEval.value.bestMove
        if (best != null) {
            makeAnalysisMove(best)
        }
    }

    fun loadCustomFen(fen: String): Boolean {
        return try {
            val game = ChessGame(fen)
            _analysisGame.value = game
            _analysisLastMove.value = null
            _analysisSelectedSquare.value = null
            _analysisLegalMoves.value = emptyList()
            _annotatedMoves.value = emptyList()
            runAnalysisEngine()
            true
        } catch (_: Exception) {
            false
        }
    }

    fun saveCurrentAnalysis(title: String, notes: String) {
        val game = _analysisGame.value
        val movesStr = game.getMoveHistory().joinToString(" ") { it.san }
        val evalStr = _analysisEval.value.formattedScore
        viewModelScope.launch {
            repository.saveGameAnalysis(
                title = title.ifBlank { "Partida ${System.currentTimeMillis() % 10000}" },
                pgnOrMoves = movesStr,
                fen = game.toFen(),
                evaluation = evalStr,
                notes = notes
            )
        }
    }

    fun deleteGame(id: Long) {
        viewModelScope.launch {
            repository.deleteSavedGame(id)
        }
    }

    private fun runAnalysisEngine(onComplete: ((EvalResult) -> Unit)? = null) {
        evalJob?.cancel()
        _isEngineThinking.value = true
        evalJob = viewModelScope.launch(Dispatchers.Default) {
            val gameCopy = ChessGame(_analysisGame.value.toFen())
            val result = ChessEvaluator.analyze(gameCopy, depth = 3)
            withContext(Dispatchers.Main) {
                _analysisEval.value = result
                _isEngineThinking.value = false
                if (_showEngineArrows.value && result.bestMove != null) {
                    _analysisArrows.value = listOf(TacticalArrow(result.bestMove.from, result.bestMove.to, Color(0xCC4CAF50)))
                } else {
                    _analysisArrows.value = emptyList()
                }
                onComplete?.invoke(result)
            }
        }
    }

    private fun triggerHaptic() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(20)
            }
        } catch (_: Exception) {}
    }

    private fun triggerHapticSuccess() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 40, 60, 80), -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(100)
            }
        } catch (_: Exception) {}
    }
}
