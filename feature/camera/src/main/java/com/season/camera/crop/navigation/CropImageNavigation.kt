package com.season.camera.crop.navigation

import androidx.activity.compose.BackHandler
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.season.camera.crop.CropImageRoute
import com.season.common.decoder.StringDecoder

internal const val fileNameArg = "fileNameArg"

internal class CropImageArgs(val fileName: String) {
    constructor(savedStateHandle: SavedStateHandle, stringDecoder: StringDecoder) :
            this(
                stringDecoder.decodeString(
                    checkNotNull(
                        savedStateHandle[fileNameArg]
                    )
                )
            )
}

fun NavController.navigateToCropImage(fileName: String) {
    this.navigate("cropImageRoute/$fileName") {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.cropImageScreen(
    onNavigateToPreview: () -> Unit,
) {
    composable(
        route = "cropImageRoute/{$fileNameArg}",
        arguments = listOf(
            navArgument(fileNameArg) { type = NavType.StringType },
        ),
    ) {

        BackHandler(true) {
            // Disable swipe back to preview conflict with drag & crop image
        }

        CropImageRoute(
            onNavigateToPreview = onNavigateToPreview,
        )
    }
}
