package com.season.camera.preview

import android.os.FileObserver
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.season.camera.preview.navigation.PreviewImageArgs
import com.season.common.decoder.StringDecoder
import com.season.data.repository.FileRepository
import com.season.model.DisplayImageItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreviewImageViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    stringDecoder: StringDecoder,
    private val fileRepository: FileRepository,
) : ViewModel() {

    private val previewImageArgs = PreviewImageArgs(savedStateHandle, stringDecoder)

    private val cameraSessionId = previewImageArgs.cameraSessionId

    private val imageList = fileRepository.observeAllImagesInSession(
        sessionId = cameraSessionId,
        events = listOf(FileObserver.DELETE)
    )
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(0),
            initialValue = emptyList(),
        )

    private val isPageMarginOn = MutableStateFlow(false)

    private val isExportConfirmDialogVisible = MutableStateFlow(false)

    val uiState = combine(
        imageList,
        isPageMarginOn,
        isExportConfirmDialogVisible,
    ) { imageList, isMarginOn, isExportConfirmDialogVisible ->
        if (imageList.isNotEmpty()) {
            PreviewImageUiState.Shown(
                imageList = imageList,
                isPageMarginOn = isMarginOn,
                isExportConfirmDialogVisible = isExportConfirmDialogVisible,
            )
        } else {
            PreviewImageUiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(0),
        initialValue = PreviewImageUiState.Loading,
    )

    fun onMarginToggled() {
        viewModelScope.launch {
            isPageMarginOn.update { it.not() }
        }
    }

    fun onDeleteClicked(fileName: String) {
        viewModelScope.launch {
            fileRepository.deleteCacheImage(fileName)
        }
    }

    fun onExportClicked() {
        viewModelScope.launch {
            isExportConfirmDialogVisible.emit(true)
        }
    }

    fun onExportDialogDismissed() {
        viewModelScope.launch {
            isExportConfirmDialogVisible.emit(false)
        }
    }

    fun onExportDialogConfirmed(onNavigateToScan: (String, Boolean) -> Unit) {
        viewModelScope.launch {
            isExportConfirmDialogVisible.emit(false)
            delay(100L)
            onNavigateToScan(cameraSessionId, isPageMarginOn.value)
        }
    }

}

sealed interface PreviewImageUiState {
    object Loading : PreviewImageUiState
    data class Shown(
        val imageList: List<DisplayImageItem>,
        val isPageMarginOn: Boolean,
        val isExportConfirmDialogVisible: Boolean,
    ) : PreviewImageUiState
}
