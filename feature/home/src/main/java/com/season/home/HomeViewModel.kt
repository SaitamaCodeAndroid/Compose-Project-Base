package com.season.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.season.data.repository.UserDataRepository
import com.season.home.HomeUiState.Loading
import com.season.home.HomeUiState.Success
import com.season.model.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    userDataRepository: UserDataRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = userDataRepository.userData.map {
        Success(it)
    }.stateIn(
        scope = viewModelScope,
        initialValue = Loading,
        started = SharingStarted.WhileSubscribed(5_000),
    )
}

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val userData: UserData) : HomeUiState
}
