package com.season.imagetofile.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.season.camera.capture.navigation.captureScreen
import com.season.camera.capture.navigation.navigateToCapture
import com.season.camera.crop.navigation.cropImageScreen
import com.season.camera.crop.navigation.navigateToCropImage
import com.season.camera.edit.navigation.editImageScreen
import com.season.camera.edit.navigation.navigateToEditImage
import com.season.camera.permission.navigation.permissionRoute
import com.season.camera.permission.navigation.permissionScreen
import com.season.camera.preview.navigation.navigateToPreviewImage
import com.season.camera.preview.navigation.previewImageScreen
import com.season.exportresult.navigation.navigateToExportResult
import com.season.exportresult.navigation.exportResultScreen

@Composable
fun ImageToFileNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = permissionRoute,
    onNavigateToEditImage: (String) -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        permissionScreen(
            onNavigateToCapture = navController::navigateToCapture
        )

        captureScreen(
            onNavigateToPreviewImage = {
                navController.navigateToPreviewImage(Uri.encode(it))
            }
        )

        previewImageScreen(
            onNavigateToCapture = { navController.popBackStack() },
            onNavigateToCropImage = {
                navController.navigateToCropImage(it)
            },
            onNavigateToEdit = {
                navController.navigateToEditImage(it)
            },
            onNavigateToExport = { sessionId, isMarginOn ->
                navController.navigateToExportResult(sessionId, isMarginOn)
            }
        )

        cropImageScreen(
            onNavigateToPreview = { navController.popBackStack() },
        )

        editImageScreen(
            onNavigateToEdit = onNavigateToEditImage,
        )

        exportResultScreen()
    }
}
