package com.season.designsystem.util

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TabPosition
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.dp

/**
 * The modifier calculate and set up
 * - The tab indicator's width equal 67% the tab width and have round top corners.
 * - The indicator offset position and animation.
 */
fun Modifier.customTabIndicatorOffset(
    currentTabPosition: TabPosition,
): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "tabIndicatorOffset"
        value = currentTabPosition
    },
) {
    val currentTabWidth by animateDpAsState(
        targetValue = currentTabPosition.width,
        animationSpec = tween(
            durationMillis = 250,
            easing = FastOutSlowInEasing,
        ),
    )

    val indicatorWidth = currentTabWidth.times(0.67f)

    val indicatorOffset by animateDpAsState(
        targetValue = currentTabPosition.left + (currentTabWidth - indicatorWidth).div(2),
        animationSpec = tween(
            durationMillis = 250,
            easing = FastOutSlowInEasing,
        ),
    )

    fillMaxWidth()
        .wrapContentSize(Alignment.BottomStart)
        .offset(x = indicatorOffset)
        .width(indicatorWidth)
        .height(4.dp)
        .clip(
            RoundedCornerShape(
                topStart = 12.dp,
                topEnd = 12.dp,
            ),
        )
}