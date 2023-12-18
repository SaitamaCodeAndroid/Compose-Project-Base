@file:OptIn(ExperimentalFoundationApi::class)

package com.season.browserfiles

import android.Manifest
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.season.designsystem.component.NiaBackground
import com.season.designsystem.component.NiaLoadingWheel
import com.season.designsystem.icon.NiaIcons
import com.season.designsystem.theme.NiaTheme
import com.season.model.FileFolder
import com.season.ui.FileCardListWithTab
import com.season.ui.MessageActionItem

@OptIn(ExperimentalPermissionsApi::class)
@Composable
internal fun BrowserFilesRoute(
    modifier: Modifier = Modifier,
    viewModel: BrowserFilesViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val permissionState = rememberPermissionState(
        permission = Manifest.permission.READ_EXTERNAL_STORAGE
    ) { hasAccess ->
        if (hasAccess) {
            viewModel.onPermissionGranted()
        } else {
            viewModel.onPermissionDenied()
        }
    }

    val pagerState = rememberPagerState()

    val lazyListState = rememberLazyListState()

    LaunchedEffect(Unit) {
        when {
            permissionState.status.shouldShowRationale -> viewModel.onShouldShowRationale()
            permissionState.status.isGranted -> viewModel.onPermissionGranted()
            else -> permissionState.launchPermissionRequest()
        }
    }

    BrowserFilesScreen(
        modifier = modifier,
        uiState = uiState,
        onTabSelected = viewModel::onTabSelected,
        onEnableStoragePermission = { permissionState.launchPermissionRequest() },
        pagerState = pagerState,
        lazyListState = lazyListState,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun BrowserFilesScreen(
    modifier: Modifier = Modifier,
    uiState: BrowserFilesUiState,
    onTabSelected: (Int) -> Unit,
    onEnableStoragePermission: () -> Unit,
    pagerState: PagerState = rememberPagerState(),
    lazyListState: LazyListState = rememberLazyListState(),
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (uiState) {

            BrowserFilesUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    NiaLoadingWheel(
                        modifier = modifier,
                        contentDesc = stringResource(id = R.string.loading),
                    )
                }
            }

            BrowserFilesUiState.LoadFailed -> {
                MessageActionItem(
                    modifier = Modifier.fillMaxSize(),
                    icon = NiaIcons.FileOpen,
                    message = R.string.storage_access_required_message,
                    actionButton = R.string.enable_permission
                ) {
                    onEnableStoragePermission()
                }
            }

            BrowserFilesUiState.NotShown -> {
                MessageActionItem(
                    modifier = Modifier.fillMaxSize(),
                    icon = NiaIcons.NoImages,
                    message = R.string.no_images_detected,
                    actionButton = R.string.take_a_photo
                ) {
                    // TODO open camera
                }
            }

            is BrowserFilesUiState.Shown -> {
                FileCardListWithTab(
                    selectedTabIndex = uiState.selectedTabIndex,
                    tabs = uiState.folderTabs,
                    onTabSelected = onTabSelected,
                    pagerState = pagerState,
                    lazyListState = lazyListState,
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewScreenLoading() {
    NiaTheme {
        NiaBackground {
            BrowserFilesScreen(
                uiState = BrowserFilesUiState.Loading,
                onTabSelected = {},
                onEnableStoragePermission = {}
            )
        }
    }
}

@Preview
@Composable
fun PreviewScreenShown() {
    NiaTheme {
        NiaBackground {
            BrowserFilesScreen(
                uiState = BrowserFilesUiState.Shown(
                    selectedTabIndex = 0,
                    folderTabs = listOf(
                        FileFolder.All(),
                        FileFolder.Common("Images", ""),
                        FileFolder.Common("Messenger", ""),
                        FileFolder.Common("Telegram", ""),
                    ),
                ),
                onTabSelected = {},
                onEnableStoragePermission = {}
            )
        }
    }
}

@Preview
@Composable
fun PreviewScreenFailed() {
    NiaTheme {
        NiaBackground {
            BrowserFilesScreen(
                uiState = BrowserFilesUiState.LoadFailed,
                onTabSelected = {},
                onEnableStoragePermission = {}
            )
        }
    }
}

@Preview
@Composable
fun PreviewScreenNotShown() {
    NiaTheme {
        NiaBackground {
            BrowserFilesScreen(
                uiState = BrowserFilesUiState.NotShown,
                onTabSelected = {},
                onEnableStoragePermission = {}
            )
        }
    }
}