package com.season.camera.preview.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.season.camera.preview.PreviewImageRoute
import com.season.common.decoder.StringDecoder

internal const val cameraSessionIdArg = "cameraSessionId"

data class PreviewImageArgs(val cameraSessionId: String) {

    constructor(savedStateHandle: SavedStateHandle, stringDecoder: StringDecoder) : this(
        stringDecoder.decodeString(checkNotNull(savedStateHandle[cameraSessionIdArg]))
    )

}

fun NavController.navigateToPreviewImage(cameraSessionId: String) {
    this.navigate("previewImageRoute/$cameraSessionId") {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.previewImageScreen(
    onNavigateToCapture: () -> Unit,
    onNavigateToCropImage: (String) -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onNavigateToExport: (String, Boolean) -> Unit,
) {
    composable(
        route = "previewImageRoute/{$cameraSessionIdArg}",
        arguments = listOf(
            navArgument(cameraSessionIdArg) { type = NavType.StringType },
        ),
    ) {
        PreviewImageRoute(
            onNavigateToCapture = onNavigateToCapture,
            onNavigateToCropImage = onNavigateToCropImage,
            onNavigateToEditImage = onNavigateToEdit,
            onNavigateToExport = onNavigateToExport,
        )
    }
}
