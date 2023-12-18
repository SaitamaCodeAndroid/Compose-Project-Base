package com.season.browserfiles.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.season.browserfiles.BrowserFilesRoute

const val browserFilesRoute = "browser_files_route"

fun NavController.navigateToBrowserFiles(navOptions: NavOptions? = null) {
    this.navigate(browserFilesRoute, navOptions)
}

fun NavGraphBuilder.browserFilesScreen() {
    composable(route = browserFilesRoute) {
        BrowserFilesRoute()
    }
}

internal const val ARGUMENT_SELECTED_TAB = "BrowserFiles.selected_tab"