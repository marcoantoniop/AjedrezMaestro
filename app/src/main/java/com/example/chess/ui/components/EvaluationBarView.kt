package com.example.chess.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chess.model.EvalResult
import com.example.ui.theme.OutfitFontFamily

@Composable
fun EvaluationBarView(
    evalResult: EvalResult,
    modifier: Modifier = Modifier,
    isFlipped: Boolean = false
) {
    val whitePercentage = evalResult.whiteWinningPercent
    val targetBlackWeight = if (isFlipped) whitePercentage / 100f else (100f - whitePercentage) / 100f
    val animatedBlackWeight by animateFloatAsState(
        targetValue = targetBlackWeight.coerceIn(0.02f, 0.98f),
        animationSpec = tween(durationMillis = 400),
        label = "eval_anim"
    )

    val whiteScoreText = evalResult.formattedScore

    Box(
        modifier = modifier
            .width(26.dp)
            .clip(RoundedCornerShape(6.dp))
            .border(1.dp, Color(0xFF3E3630), RoundedCornerShape(6.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            // Black portion (top by default)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(animatedBlackWeight)
                    .background(Color(0xFF231E1B))
            )

            // White portion (bottom by default)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f - animatedBlackWeight)
                    .background(Color(0xFFEDE3D5))
            )
        }

        // Eval text overlay
        val textAtBottom = if (isFlipped) evalResult.scoreCentipawns < 0 else evalResult.scoreCentipawns >= 0
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(if (textAtBottom) Alignment.BottomCenter else Alignment.TopCenter)
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = whiteScoreText,
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                color = if (textAtBottom) Color(0xFF1E1A17) else Color(0xFFFAF6F0)
            )
        }
    }
}
