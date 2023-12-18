package com.season.camera.capture.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.season.camera.capture.CaptureRoute

const val captureRoute = "capture_route"

fun NavController.navigateToCapture() {
    this.navigate(captureRoute) {
        popUpTo(graph.id) {
            inclusive = true
        }
        launchSingleTop = true
    }
}

fun NavGraphBuilder.captureScreen(
    onNavigateToPreviewImage: (String) -> Unit,
) {
    composable(route = captureRoute) {
        CaptureRoute(
            onNavigateToPreviewImage = onNavigateToPreviewImage,
        )
    }
}
