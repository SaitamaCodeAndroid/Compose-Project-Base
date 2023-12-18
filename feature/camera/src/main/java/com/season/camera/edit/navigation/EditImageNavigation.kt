package com.season.camera.edit.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.season.camera.edit.EditImageRoute

internal const val fileNameArg = "fileNameArg"

fun NavController.navigateToEditImage(fileName: String) {
    this.navigate("editImageRoute/$fileName") {
        launchSingleTop = true
    }
}

fun NavGraphBuilder.editImageScreen(
    onNavigateToEdit: (String) -> Unit,
) {
    composable(
        route = "editImageRoute/{$fileNameArg}",
        arguments = listOf(
            navArgument(fileNameArg) { type = NavType.StringType },
        ),
    ) {
        EditImageRoute(
            onNavigateToEdit = onNavigateToEdit
        )
    }
}
