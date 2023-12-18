package com.season.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ShutterAnimation(isCapturing: Boolean) {

    val animatedProgress = rememberInfiniteTransition(label = "")
        .animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 300), // Duration of the animation
                repeatMode = RepeatMode.Reverse
            ), label = ""
        )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black,
    ) {
        // Draw the animation using a Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 6.dp.toPx()
            val halfStroke = strokeWidth / 2
            val width = size.width
            val height = size.height

            val left = width * animatedProgress.value
            drawLine(
                color = Color.White,
                start = Offset(left - halfStroke, 0f),
                end = Offset(left - halfStroke, height),
                strokeWidth = strokeWidth
            )
            drawLine(
                color = Color.White,
                start = Offset(width - left + halfStroke, 0f),
                end = Offset(width - left + halfStroke, height),
                strokeWidth = strokeWidth
            )
        }
    }
}
