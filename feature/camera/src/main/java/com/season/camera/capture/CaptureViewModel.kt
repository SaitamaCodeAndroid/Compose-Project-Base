package com.season.camera.capture

import android.graphics.Bitmap
import android.os.FileObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.season.data.repository.FileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CaptureViewModel @Inject constructor(
    private val fileRepository: FileRepository,
) : ViewModel() {

    val cameraSessionId = UUID.randomUUID().toString()

    val imageList = fileRepository.observeAllImagesInSession(
        sessionId = cameraSessionId,
        // When captured image is cached, the FileObserver.CREATE is fired, but image is actually
        // modified data and saved only when FileObserver.CLOSE_WRITE happens. So we listen
        // to this to make sure the preview captured image data is available to show to UI
        events = listOf(FileObserver.CLOSE_WRITE)
    )
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(0),
            initialValue = emptyList(),
        )

    init {
        viewModelScope.launch {
            fileRepository.startCameraSession()
        }
    }

    fun onImageCaptured(bitmap: Bitmap, rotation: Int) {
        viewModelScope.launch {
            fileRepository.cacheCapturedImage(cameraSessionId, bitmap, rotation)
        }
    }
}
