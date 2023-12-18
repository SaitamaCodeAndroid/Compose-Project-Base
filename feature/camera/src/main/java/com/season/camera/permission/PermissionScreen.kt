@file:OptIn(ExperimentalPermissionsApi::class)

package com.season.camera.permission

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.season.camera.R
import com.season.designsystem.component.NiaLoadingWheel
import com.season.designsystem.icon.NiaIcons
import com.season.ui.MessageActionItem

@Composable
internal fun PermissionRoute(
    viewModel: PermissionViewModel = hiltViewModel(),
    onNavigateToCapture: () -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PermissionScreen(
        uiState = uiState,
        onNavigateToCapture = onNavigateToCapture,
        onPermissionGranted = viewModel::onPermissionGranted,
        onPermissionDenied = viewModel::onPermissionDenied,
    )
}

@Composable
internal fun PermissionScreen(
    uiState: PermissionUiState,
    onNavigateToCapture: () -> Unit,
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
) {

    var shouldShowRationale by remember {
        mutableStateOf(false)
    }

    val permissionState = rememberPermissionState(
        permission = Manifest.permission.CAMERA
    ) { hasAccess ->
        if (hasAccess) {
            onPermissionGranted()
        } else {
            onPermissionDenied()
        }
    }

    LaunchedEffect(Unit) {
        when {
            permissionState.status.shouldShowRationale -> shouldShowRationale = true
            permissionState.status.isGranted -> onPermissionGranted()
            else -> permissionState.launchPermissionRequest()
        }
    }

    if (shouldShowRationale) {
        // TODO show alert
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (uiState) {
            PermissionUiState.CheckingPermission -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    NiaLoadingWheel(
                        contentDesc = stringResource(id = R.string.loading),
                    )
                }
            }

            is PermissionUiState.PermissionChecked -> {
                if (uiState.isGranted) {
                    onNavigateToCapture()
                } else {
                    MessageActionItem(
                        modifier = Modifier.fillMaxSize(),
                        icon = NiaIcons.FileOpen,
                        message = R.string.camera_access_required_message,
                        actionButton = R.string.enable_permission
                    ) {
                        permissionState.launchPermissionRequest()
                    }
                }
            }
        }
    }
}
