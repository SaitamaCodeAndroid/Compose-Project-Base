package com.season.camera.permission.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.season.camera.permission.PermissionRoute

const val permissionRoute = "camera_permission_route"

fun NavGraphBuilder.permissionScreen(
    onNavigateToCapture: () -> Unit,
) {
    composable(route = permissionRoute) {
        PermissionRoute(
            onNavigateToCapture = onNavigateToCapture
        )
    }
}
