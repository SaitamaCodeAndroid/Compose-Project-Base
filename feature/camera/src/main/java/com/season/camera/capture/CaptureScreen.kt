package com.season.camera.capture

import android.app.Activity
import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.season.designsystem.component.DynamicAsyncImage
import com.season.designsystem.component.NiaBackground
import com.season.designsystem.component.NiaTextButton
import com.season.designsystem.icon.NiaIcons
import com.season.designsystem.theme.NiaTheme
import com.season.model.DisplayImageItem
import com.season.ui.AnimatedCaptureButton
import com.season.ui.R

@Composable
internal fun CaptureRoute(
    viewModel: CaptureViewModel = hiltViewModel(),
    onNavigateToPreviewImage: (String) -> Unit,
) {
    val context = LocalContext.current

    val imageList by viewModel.imageList.collectAsStateWithLifecycle(
        minActiveState = Lifecycle.State.RESUMED
    )

    CaptureScreen(
        cameraSessionId = viewModel.cameraSessionId,
        onImageCaptured = viewModel::onImageCaptured,
        imageList = imageList,
        onNavigateToCropImage = {
            onNavigateToPreviewImage(viewModel.cameraSessionId)
        },
        onExitCameraSession = {
            (context as Activity).finish()
        }
    )
}

@Composable
internal fun CaptureScreen(
    isPreview: Boolean = false,
    cameraSessionId: String,
    onImageCaptured: (Bitmap, Int) -> Unit,
    imageList: List<DisplayImageItem>,
    onNavigateToCropImage: () -> Unit,
    onExitCameraSession: () -> Unit,
) {

    val cameraX = rememberCameraX(cameraSessionId)

    val isFlashAvailable by cameraX.isFlashAvailable.collectAsStateWithLifecycle()

    val isFlashOn by cameraX.isFlashEnabled.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        if (isPreview.not()) {
            AndroidView(
                factory = {
                    cameraX.startCameraPreviewView()
                },
                modifier = Modifier.fillMaxSize(),
            )
        }

        IconButton(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 32.dp),
            onClick = onExitCameraSession
        ) {
            Icon(
                modifier = Modifier.size(32.dp),
                imageVector = NiaIcons.Close,
                contentDescription = "Take picture",
                tint = Color.White,
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            Box(modifier = Modifier.weight(1f)) {
                if (isFlashAvailable) {
                    IconToggleButton(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .wrapContentSize()
                            .background(Color.Transparent)
                            .border(
                                width = 0.5.dp,
                                color = Color.White,
                                shape = CircleShape
                            ),
                        checked = isFlashOn,
                        onCheckedChange = {
                            cameraX.onFlashChanged(it)
                        }
                    ) {
                        Icon(
                            imageVector = if (isFlashOn) {
                                Icons.Filled.FlashOff
                            } else {
                                Icons.Filled.FlashOn
                            },
                            contentDescription = "Icon",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }
            }

            AnimatedCaptureButton(
                modifier = Modifier
            ) { isCapturing ->
                if (isCapturing) {
                    cameraX.takePhoto(onImageCaptured)
                }
            }

            Box(
                modifier = Modifier.weight(1f)
            ) {
                if (imageList.isNotEmpty()) {

                    Row(
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            DynamicAsyncImage(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .size(48.dp)
                                    .clip(CircleShape),
                                imageUrl = imageList.last().url,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(R.drawable.img_placeholder_4_3),
                            )

                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        color = Color.Red,
                                        shape = CircleShape
                                    )
                                    .align(Alignment.TopEnd),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = imageList.size.toString(),
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }
        }

        NiaTextButton(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 32.dp)
                .align(Alignment.TopEnd),
            onClick = onNavigateToCropImage,
            enabled = imageList.isNotEmpty()
        ) {
            Text(
                text = "Next",
                color = Color.White
            )
        }
    }
}

@Preview
@Composable
fun PreviewCaptureScreen() {
    NiaTheme {
        NiaBackground {
            CaptureScreen(
                isPreview = true,
                cameraSessionId = "123",
                onImageCaptured = { _, _ -> },
                imageList = emptyList(),
                onNavigateToCropImage = {},
                onExitCameraSession = {}
            )
        }
    }
}
