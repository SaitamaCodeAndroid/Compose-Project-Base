package com.season.camera.crop

import android.graphics.Bitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.season.camera.crop.navigation.CropImageArgs
import com.season.common.decoder.StringDecoder
import com.season.data.repository.FileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CropImageViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    stringDecoder: StringDecoder,
    private val fileRepository: FileRepository,
) : ViewModel() {

    private val args: CropImageArgs = CropImageArgs(savedStateHandle, stringDecoder)

    private val fileName = args.fileName

    private val image = MutableStateFlow<Bitmap?>(null)

    private val croppedImage = MutableStateFlow<Bitmap?>(null)

    val uiState = combine(image, croppedImage) { image, croppedImage ->
        when {
            croppedImage != null -> CropImageUiState.ShownCropped(croppedImage)
            image != null -> CropImageUiState.ShownImage(image)
            else -> CropImageUiState.Loading
        }
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CropImageUiState.Loading
    )

    init {
        viewModelScope.launch {
            image.emit(fileRepository.getImageBitmapByName(fileName = fileName))
        }
    }

    fun onCropPhotoClick(croppedBitmap: Bitmap) {
        viewModelScope.launch {
            croppedImage.emit(croppedBitmap)
        }
    }

    fun onRevertCroppedPhotoClick() {
        viewModelScope.launch {
            croppedImage.emit(null)
        }
    }

    fun onConfirmCropClick(onNavigateToPreview: () -> Unit) {
        viewModelScope.launch {
            croppedImage.value?.let {
                fileRepository.overrideCroppedImageFile(fileName, it)
                onNavigateToPreview()
            }
        }
    }
}
