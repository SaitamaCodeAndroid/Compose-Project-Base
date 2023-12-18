package com.season.browserfiles

import com.season.model.FileFolder

sealed interface BrowserFilesUiState {
    /**
     * The onboarding state is loading.
     */
    object Loading : BrowserFilesUiState

    /**
     * The onboarding state was unable to load.
     */
    object LoadFailed : BrowserFilesUiState

    /**
     * There is no onboarding state.
     */
    object NotShown : BrowserFilesUiState

    /**
     * There is a onboarding state, with the given lists of topics.
     */
    data class Shown(
        val selectedTabIndex : Int,
        val folderTabs: List<FileFolder>,
    ) : BrowserFilesUiState
}
