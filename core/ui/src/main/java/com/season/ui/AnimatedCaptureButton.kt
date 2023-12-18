package com.season.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.season.designsystem.icon.NiaIcons
import com.season.designsystem.theme.NiaTheme
import kotlinx.coroutines.delay

@Composable
fun AnimatedCaptureButton(
    modifier: Modifier = Modifier,
    onCaptureStateChanged: (Boolean) -> Unit,
) {
    var isCaptureButtonAnimating by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isCaptureButtonAnimating) 0.8f else 1.0f,
        animationSpec = tween(durationMillis = 200),
        label = "scale button"
    )

    LaunchedEffect(isCaptureButtonAnimating) {
        if (isCaptureButtonAnimating) {
            delay(200) // Wait for the animation to complete before resetting the state
            isCaptureButtonAnimating = false
            onCaptureStateChanged(false)
        }
    }

    Row(
        modifier = modifier
            .size(80.dp)
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isCaptureButtonAnimating = true
                onCaptureStateChanged(true)
            },
        content = {
            Icon(
                imageVector = NiaIcons.Lens,
                contentDescription = "Take picture",
                tint = Color.White,
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 2.dp,
                        color = Color.White,
                        shape = CircleShape
                    )
            )
        }
    )
}

@Preview
@Composable
private fun PreviewAnimatedCaptureButton() {
    NiaTheme {
        AnimatedCaptureButton(
            onCaptureStateChanged = {}
        )
    }
}
