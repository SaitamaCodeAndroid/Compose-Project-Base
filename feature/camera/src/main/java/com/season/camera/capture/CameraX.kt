package com.season.camera.capture

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCapture.OnImageSavedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun rememberCameraX(cameraSessionId: String): CameraX {
    val context = LocalContext.current
    val owner = LocalLifecycleOwner.current

    val cameraX = remember {
        CameraX(context, owner, cameraSessionId)
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraX.cameraProvider?.unbindAll()
        }
    }

    return cameraX

}

class CameraX(
    private var context: Context,
    private var owner: LifecycleOwner,
    private var sessionId: String,
) {
    var cameraProvider: ProcessCameraProvider? = null
        private set

    private var imageCapture: ImageCapture = ImageCapture.Builder()
        .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
        .build()

    private val previewView = PreviewView(context)

    private var camera: Camera? = null

    var isFlashAvailable = MutableStateFlow(true)

    var isFlashEnabled = MutableStateFlow(false)

    fun startCameraPreviewView(): PreviewView {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val camSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        try {
            cameraProvider = cameraProviderFuture.get()
            cameraProvider?.apply {
                unbindAll()
                camera = bindToLifecycle(owner, camSelector, preview, imageCapture)

                owner.lifecycleScope.launch {
                    isFlashAvailable.emit(camera?.cameraInfo?.hasFlashUnit() == true)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return previewView
    }

    fun capturePhoto(onImageCaptured: (Uri) -> Unit) = owner.lifecycleScope.launch {
        val outputFile = File(
            "${context.cacheDir}/camera_session",
            "${sessionId}-${System.currentTimeMillis()}.jpg"
        )

        imageCapture.takePicture(
            ImageCapture.OutputFileOptions.Builder(outputFile).build(),
            ContextCompat.getMainExecutor(context),
            object : OnImageSavedCallback {

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Log.d(
                        "=======",
                        "onCaptureSuccess: Uri  ${outputFileResults.savedUri.toString()}"
                    )
                    outputFileResults.savedUri?.let {
                        onImageCaptured(it)
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("=======", "onCaptureSuccess: onError")
                }

            })
    }

    fun takePhoto(onImageCaptured: (Bitmap, Int) -> Unit) = owner.lifecycleScope.launch {
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(context),
            object : OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    onImageCaptured(image.toBitmap(), image.imageInfo.rotationDegrees)
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    Log.e("========", "onError capture Photo: ", exception)
                }
            }
        )
    }

    fun onFlashChanged(isFlashOn: Boolean) {
        owner.lifecycleScope.launch {
            isFlashEnabled.emit(isFlashOn)
            camera?.cameraControl?.enableTorch(isFlashOn)
        }
    }

}
