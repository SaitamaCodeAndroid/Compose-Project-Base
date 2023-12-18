package com.season.browserfiles

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.season.browserfiles.navigation.ARGUMENT_SELECTED_TAB
import com.season.data.repository.FileRepository
import com.season.model.DisplayFileItem
import com.season.model.FileFolder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrowserFilesViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val fileRepository: FileRepository,
) : ViewModel() {

    private val cacheImageFiles = mutableMapOf<Int, List<DisplayFileItem>>()

    private val selectedTabIndex: StateFlow<Int> = savedStateHandle.getStateFlow(
        key = ARGUMENT_SELECTED_TAB,
        initialValue = 0,
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 0,
    )

    private val isStoragePermissionGranted = MutableStateFlow<Boolean?>(null)

    private val imageFolderTabs = isStoragePermissionGranted
        .map { isPermissionGranted ->
            if (isPermissionGranted == true) {
                fileRepository.getImageFolders()
            } else {
                emptyList()
            }
        }

    private val imageFiles: Flow<MutableMap<Int, List<DisplayFileItem>>> =
        combine(
            selectedTabIndex,
            imageFolderTabs,
        ) { tabIndex, folderTabs ->
            if (folderTabs.isNotEmpty() && cacheImageFiles[tabIndex] == null) {
                val images = when (val tab = folderTabs[tabIndex]) {
                    is FileFolder.All -> fileRepository.getImagesInFolder(null)
                    is FileFolder.Common -> fileRepository.getImagesInFolder(tab.path)
                }
                cacheImageFiles[tabIndex] = images
            }
            return@combine cacheImageFiles
        }

    val uiState: StateFlow<BrowserFilesUiState> =
        combine(
            selectedTabIndex,
            isStoragePermissionGranted,
            imageFolderTabs,
            imageFiles,
        ) { tabIndex, isPermissionGranted, imageFolderTabs, imageFiles ->
            when (isPermissionGranted) {
                true -> {
                    val tabs = imageFolderTabs.toMutableList().map {
                        when (it) {
                            is FileFolder.All -> {
                                it.copy(files = imageFiles[tabIndex] ?: emptyList())
                            }

                            is FileFolder.Common -> {
                                it.copy(files = imageFiles[tabIndex] ?: emptyList())
                            }
                        }
                    }
                    if (tabs.isEmpty()) {
                        BrowserFilesUiState.NotShown
                    } else {
                        BrowserFilesUiState.Shown(
                            selectedTabIndex = tabIndex,
                            folderTabs = tabs,
                        )
                    }
                }

                false -> {
                    BrowserFilesUiState.LoadFailed
                }

                else -> {
                    BrowserFilesUiState.Loading
                }
            }
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = BrowserFilesUiState.Loading
            )

    fun onTabSelected(index: Int) {
        savedStateHandle[ARGUMENT_SELECTED_TAB] = index
    }

    fun onPermissionGranted() {
        viewModelScope.launch {
            isStoragePermissionGranted.emit(true)
        }
    }

    fun onPermissionDenied() {
        viewModelScope.launch {
            isStoragePermissionGranted.emit(false)
        }
    }

    fun onShouldShowRationale() {
        // TODO show rationale
    }
}
