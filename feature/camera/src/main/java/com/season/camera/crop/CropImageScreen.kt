package com.season.camera.crop

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.season.camera.R
import com.season.designsystem.component.NiaBackground
import com.season.designsystem.component.NiaButton
import com.season.designsystem.component.NiaLoadingWheel
import com.season.designsystem.component.NiaTopAppBar
import com.season.designsystem.icon.NiaIcons
import com.season.designsystem.theme.NiaTheme
import me.pqpo.smartcropperlib.view.CropImageView

@Composable
internal fun CropImageRoute(
    viewModel: CropImageViewModel = hiltViewModel(),
    onNavigateToPreview: () -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CropImageScreen(
        uiState = uiState,
        onNavigateToPreview = onNavigateToPreview,
        onConfirmCropClick = {
            viewModel.onConfirmCropClick { onNavigateToPreview() }
        },
        onCropPhotoClick = viewModel::onCropPhotoClick,
        onRevertCroppedPhotoClick = viewModel::onRevertCroppedPhotoClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CropImageScreen(
    uiState: CropImageUiState,
    onNavigateToPreview: () -> Unit,
    onConfirmCropClick: () -> Unit,
    onCropPhotoClick: (Bitmap) -> Unit = {},
    onRevertCroppedPhotoClick: () -> Unit = {},
) {

    val context = LocalContext.current

    val imageView = remember {
        CropImageView(context)
    }

    LaunchedEffect(uiState) {
        if (uiState is CropImageUiState.ShownImage) {
            imageView.setImageToCrop(uiState.image)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        NiaTopAppBar(
            titleRes = R.string.crop_image,
            navigationIcon = NiaIcons.ArrowBack,
            navigationIconContentDescription = "Re-take photo",
            actionIcon = if (uiState is CropImageUiState.ShownCropped) {
                NiaIcons.Check
            } else {
                null
            },
            actionIconContentDescription = "Finish crop",
            onNavigationClick = onNavigateToPreview,
            onActionClick = onConfirmCropClick,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .weight(1f)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(4.dp),
                )
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {

            when (uiState) {
                CropImageUiState.Loading -> {
                    NiaLoadingWheel(
                        contentDesc = "detect cropping image",
                    )
                }

                is CropImageUiState.ShownImage -> {
                    AndroidView(
                        factory = { imageView },
                        modifier = Modifier.aspectRatio(1f / 1.4142f)
                    )
                }

                is CropImageUiState.ShownCropped -> {
                    Image(
                        modifier = Modifier.aspectRatio(1f / 1.4142f),
                        painter = rememberAsyncImagePainter(uiState.image),
                        contentScale = ContentScale.Fit,
                        contentDescription = "some useful description",
                    )
                }
            }
        }

        Row(
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            when (uiState) {
                CropImageUiState.Loading -> {
                    // Empty
                }

                is CropImageUiState.ShownCropped -> {
                    NiaButton(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(48.dp),
                        onClick = { onRevertCroppedPhotoClick() }
                    ) {
                        Text(
                            text = "Revert original",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                is CropImageUiState.ShownImage -> {
                    NiaButton(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(48.dp),
                        onClick = { onCropPhotoClick(imageView.crop()) }
                    ) {
                        Text(
                            text = "Crop selected area",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewCropImageScreen() {
    NiaTheme {
        NiaBackground {
            CropImageScreen(
                uiState = CropImageUiState.Loading,
                onNavigateToPreview = {},
                onConfirmCropClick = {},
                onCropPhotoClick = {},
                onRevertCroppedPhotoClick = {},
            )
        }
    }
}
