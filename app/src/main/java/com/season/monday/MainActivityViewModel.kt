package com.season.monday

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.season.data.repository.UserDataRepository
import com.season.monday.MainActivityUiState.Loading
import com.season.monday.MainActivityUiState.Success
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    userDataRepository: UserDataRepository,
) : ViewModel() {

    val uiState: StateFlow<MainActivityUiState> = userDataRepository.userData.map {
        Success(it.shouldHideOnboarding)
    }.stateIn(
        scope = viewModelScope,
        initialValue = Loading,
        started = SharingStarted.WhileSubscribed(5_000),
    )
}


sealed interface MainActivityUiState {
    object Loading : MainActivityUiState
    data class Success(val shouldHideOnboarding: Boolean) : MainActivityUiState
}
