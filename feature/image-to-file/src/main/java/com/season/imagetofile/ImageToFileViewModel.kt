package com.season.imagetofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.season.data.repository.UserDataRepository
import com.season.imagetofile.ImageToFileUiState.Loading
import com.season.imagetofile.ImageToFileUiState.Success
import com.season.model.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ImageToFileViewModel @Inject constructor(
    userDataRepository: UserDataRepository
) : ViewModel() {

    val uiState: StateFlow<ImageToFileUiState> = userDataRepository.userData
        .map {
            Success(it)
        }
        .stateIn(
            scope = viewModelScope,
            initialValue = Loading,
            started = SharingStarted.WhileSubscribed(5_000),
        )

}

sealed interface ImageToFileUiState {
    object Loading : ImageToFileUiState
    data class Success(val userData: UserData) : ImageToFileUiState
}
