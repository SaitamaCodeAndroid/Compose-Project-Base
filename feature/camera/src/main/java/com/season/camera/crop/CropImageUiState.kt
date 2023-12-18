package com.season.camera.crop

import android.graphics.Bitmap

sealed interface CropImageUiState {

    object Loading : CropImageUiState

    data class ShownImage(
        val image: Bitmap,
    ) : CropImageUiState

    data class ShownCropped(
        val image: Bitmap,
    ) : CropImageUiState

}
