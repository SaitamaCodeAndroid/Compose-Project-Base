package com.season.exportresult

import android.content.Intent
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.season.common.decoder.StringDecoder
import com.season.common.result.Result
import com.season.data.repository.FileRepository
import com.season.domain.GeneratePdfFileUseCase
import com.season.exportresult.navigation.ExportResultArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExportResultViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    stringDecoder: StringDecoder,
    generatePdfPreviewPagesUseCase: GeneratePdfFileUseCase,
    private val fileRepository: FileRepository,
) : ViewModel() {

    private val args: ExportResultArgs = ExportResultArgs(savedStateHandle, stringDecoder)

    val uiState: StateFlow<ExportResultUiState> = generatePdfPreviewPagesUseCase(
        cameraSessionId = args.cameraSessionId,
        isMarginOn = args.isMarginOn
    ).flatMapLatest {
        flowOf(
            when (it) {
                Result.Loading -> {
                    ExportResultUiState.Loading
                }

                is Result.Error -> {
                    ExportResultUiState.Error
                }

                is Result.Success -> {
                    ExportResultUiState.Shown(
                        fileName = it.data.name,
                        fileSize = it.data.size,
                        pageSize = it.data.pageSize,
                        pageMargin = it.data.pageMargin,
                        numberOfPages = it.data.numberOfPages,
                        fileLocation = it.data.fileLocation,
                    )
                }
            }
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ExportResultUiState.Loading
        )

    fun onShareClicked(onStartShareActivity: (Intent) -> Unit) {
        val fileLocation = (uiState.value as? ExportResultUiState.Shown)?.fileLocation ?: return
        viewModelScope.launch {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, fileRepository.getSharePdfUri(fileLocation))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            onStartShareActivity(Intent.createChooser(shareIntent, null))
        }
    }

    fun onHomeClicked() {

    }

}

sealed interface ExportResultUiState {

    object Loading : ExportResultUiState

    object Error : ExportResultUiState
    data class Shown(
        val fileName: String,
        val fileSize: String,
        val pageSize: String,
        val pageMargin: String,
        val numberOfPages: Int,
        val fileLocation: String,
    ) : ExportResultUiState

}
