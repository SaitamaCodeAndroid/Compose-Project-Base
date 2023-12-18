package com.season.exportresult.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.season.common.decoder.StringDecoder
import com.season.exportresult.ExportResultRoute

internal const val cameraSessionIdArg = "cameraSessionIdArg"
internal const val isMarginOnArg = "isMarginOnArg"

internal class ExportResultArgs(val cameraSessionId: String, val isMarginOn: Boolean) {
    constructor(savedStateHandle: SavedStateHandle, stringDecoder: StringDecoder) :
            this(
                cameraSessionId = stringDecoder.decodeString(
                    checkNotNull(
                        savedStateHandle[cameraSessionIdArg]
                    )
                ),
                isMarginOn = savedStateHandle[isMarginOnArg] ?: false,
            )
}

fun NavController.navigateToExportResult(cameraSessionId: String, isMarginOn: Boolean) {
    this.navigate("exportResultRoute/$cameraSessionId?isMarginOn=$isMarginOn") {
        launchSingleTop = true
        popUpTo(graph.id) {
            inclusive = true
        }
    }
}

fun NavGraphBuilder.exportResultScreen() {
    composable(
        route = "exportResultRoute/{$cameraSessionIdArg}?isMarginOn={$isMarginOnArg}",
        arguments = listOf(
            navArgument(cameraSessionIdArg) { type = NavType.StringType },
            navArgument(isMarginOnArg) { type = NavType.BoolType }
        ),
    ) {
        ExportResultRoute()
    }
}
