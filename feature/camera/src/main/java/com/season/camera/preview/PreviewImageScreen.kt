package com.season.camera.preview

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.season.camera.R
import com.season.designsystem.component.NiaBackground
import com.season.designsystem.component.NiaButton
import com.season.designsystem.component.NiaLoadingWheel
import com.season.designsystem.component.NiaTopAppBar
import com.season.designsystem.icon.NiaIcons
import com.season.designsystem.theme.NiaTheme
import com.season.model.DisplayImageItem
import com.season.ui.DocPreviewPager

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun PreviewImageRoute(
    viewModel: PreviewImageViewModel = hiltViewModel(),
    onNavigateToCapture: () -> Unit,
    onNavigateToCropImage: (String) -> Unit,
    onNavigateToEditImage: (String) -> Unit,
    onNavigateToExport: (String, Boolean) -> Unit,
) {

    val pagerState = rememberPagerState()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle(
        minActiveState = Lifecycle.State.RESUMED,
    )

    PreviewImageScreen(
        pagerState = pagerState,
        uiState = uiState,
        onNavigateUpClicked = onNavigateToCapture,
        onExportClicked = viewModel::onExportClicked,
        onDeleteClicked = { item, shouldBackToCapture ->
            viewModel.onDeleteClicked(item.name)
            if (shouldBackToCapture) {
                onNavigateToCapture()
            }
        },
        onCropClicked = { item -> onNavigateToCropImage(item.name) },
        onEditClicked = { item -> onNavigateToEditImage(item.name) },
        onMarginPageToggled = viewModel::onMarginToggled,
        onConfirmDialogDismissed = viewModel::onExportDialogDismissed,
        onExportDialogConfirmed = { viewModel.onExportDialogConfirmed(onNavigateToExport) }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun PreviewImageScreen(
    pagerState: PagerState,
    uiState: PreviewImageUiState,
    onNavigateUpClicked: () -> Unit,
    onExportClicked: () -> Unit,
    onDeleteClicked: (DisplayImageItem, Boolean) -> Unit,
    onCropClicked: (DisplayImageItem) -> Unit,
    onEditClicked: (DisplayImageItem) -> Unit,
    onMarginPageToggled: () -> Unit,
    onConfirmDialogDismissed: () -> Unit,
    onExportDialogConfirmed: () -> Unit,
) {
    return when (uiState) {
        PreviewImageUiState.Loading -> {
            PreviewImageLoadingScreen(
                onNavigateToCapture = onNavigateUpClicked
            )
        }

        is PreviewImageUiState.Shown -> {
            PreviewImageShownScreen(
                pagerState = pagerState,
                uiState = uiState,
                onNavigateUpClicked = onNavigateUpClicked,
                onMarginPageToggled = onMarginPageToggled,
                onExportClicked = onExportClicked,
                onDeleteClicked = onDeleteClicked,
                onCropClicked = onCropClicked,
                onEditClicked = onEditClicked,
                onConfirmDialogDismissed = onConfirmDialogDismissed,
                onExportDialogConfirmed = onExportDialogConfirmed,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PreviewImageLoadingScreen(
    onNavigateToCapture: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        NiaTopAppBar(
            titleRes = R.string.preview,
            navigationIcon = NiaIcons.ArrowBack,
            navigationIconContentDescription = stringResource(R.string.content_desc_navigate_up),
            onNavigationClick = onNavigateToCapture,
            actionIcon = null,
            actionIconContentDescription = null,
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            NiaLoadingWheel(
                contentDesc = stringResource(R.string.content_desc_loading),
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun PreviewImageShownScreen(
    pagerState: PagerState,
    uiState: PreviewImageUiState.Shown,
    onNavigateUpClicked: () -> Unit,
    onMarginPageToggled: () -> Unit,
    onDeleteClicked: (DisplayImageItem, Boolean) -> Unit,
    onCropClicked: (DisplayImageItem) -> Unit,
    onEditClicked: (DisplayImageItem) -> Unit,
    onExportClicked: () -> Unit,
    onConfirmDialogDismissed: () -> Unit,
    onExportDialogConfirmed: () -> Unit,
) {

    val imageList = uiState.imageList

    val isPageMarginOn = uiState.isPageMarginOn

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        NiaTopAppBar(
            titleRes = R.string.preview,
            navigationIcon = NiaIcons.ArrowBack,
            navigationIconContentDescription = stringResource(id = R.string.content_desc_navigate_up),
            actionIcon = if (isPageMarginOn) {
                NiaIcons.MarginOn
            } else {
                NiaIcons.MarginOff
            },
            actionIconContentDescription = stringResource(R.string.content_desc_toggle_page_margin),
            onNavigationClick = onNavigateUpClicked,
            onActionClick = onMarginPageToggled,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {

            DocPreviewPager(
                modifier = Modifier,
                pagerState = pagerState,
                imageList = imageList,
                isPageMarginOn = isPageMarginOn,
            )

            PageIndicator(
                modifier = Modifier.align(Alignment.BottomCenter),
                currentPage = pagerState.currentPage + 1,
                totalPage = imageList.size,
            )
        }

        BottomController(
            onDeleteClicked = {
                onDeleteClicked(
                    imageList[pagerState.currentPage],
                    imageList.size == 1
                )
            },
            onCropClicked = {
                onCropClicked(
                    imageList[pagerState.currentPage]
                )
            },
            onEditClicked = {
                onEditClicked(
                    imageList[pagerState.currentPage]
                )
            },
            onExportClicked = onExportClicked
        )
    }

    if (uiState.isExportConfirmDialogVisible) {
        AlertDialog(
            onDismissRequest = { onConfirmDialogDismissed() },
            text = {
                Text(text = "Export ${imageList.size} pages PDF ?")
            },
            confirmButton = {
                NiaButton(
                    onClick = onExportDialogConfirmed
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                NiaButton(
                    onClick = onConfirmDialogDismissed
                ) {
                    Text("Cancel")
                }
            },
            properties = DialogProperties(dismissOnBackPress = false)
        )
    }
}

@Composable
private fun PageIndicator(
    modifier: Modifier = Modifier,
    currentPage: Int,
    totalPage: Int,
) {
    Row(
        modifier = modifier.padding(bottom = 32.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = Color.Gray.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp),
        ) {
            Text(
                text = "$currentPage/$totalPage",
                color = Color.DarkGray
            )
        }
    }
}

@Composable
private fun BottomController(
    onDeleteClicked: () -> Unit,
    onCropClicked: () -> Unit,
    onEditClicked: () -> Unit,
    onExportClicked: () -> Unit,
) {
    BottomAppBar(
        containerColor = Color.White,
        actions = {
            IconButton(onClick = onDeleteClicked) {
                Icon(
                    imageVector = NiaIcons.Delete,
                    contentDescription = null
                )
            }
            IconButton(onClick = onCropClicked) {
                Icon(
                    imageVector = NiaIcons.Crop,
                    contentDescription = null
                )
            }
            IconButton(onClick = onEditClicked) {
                Icon(
                    imageVector = NiaIcons.Edit,
                    contentDescription = null
                )
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                containerColor = MaterialTheme.colorScheme.secondary,
                text = {
                    Text(
                        text = stringResource(R.string.export),
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                },
                icon = {
                    Icon(
                        imageVector = NiaIcons.Document,
                        contentDescription = stringResource(R.string.content_desc_export_document),
                    )
                },
                expanded = true,
                onClick = onExportClicked,
                shape = RoundedCornerShape(12.dp),
            )
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
private fun PreviewPreviewImageScreen() {
    NiaTheme {
        NiaBackground {
            PreviewImageScreen(
                pagerState = rememberPagerState(),
                uiState = PreviewImageUiState.Shown(
                    imageList = listOf(
                        DisplayImageItem(
                            name = "abc.jpg",
                            url = "images/Abc.jpg"
                        ),
                    ),
                    isPageMarginOn = false,
                    isExportConfirmDialogVisible = false,
                ),
                onNavigateUpClicked = {},
                onExportClicked = {},
                onDeleteClicked = { _, _ -> },
                onCropClicked = {},
                onEditClicked = {},
                onMarginPageToggled = {},
                onConfirmDialogDismissed = {},
                onExportDialogConfirmed = {},
            )
        }
    }
}
