package com.season.camera.permission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.season.camera.permission.PermissionUiState.CheckingPermission
import com.season.camera.permission.PermissionUiState.PermissionChecked
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel @Inject constructor() : ViewModel() {

    private val isCameraPermissionGranted = MutableStateFlow<Boolean?>(null)

    val uiState: StateFlow<PermissionUiState> = isCameraPermissionGranted
        .map {
            it?.let { PermissionChecked(it) } ?: CheckingPermission
        }
        .stateIn(
            scope = viewModelScope,
            initialValue = CheckingPermission,
            started = SharingStarted.WhileSubscribed(5_000),
        )

    fun onPermissionGranted() {
        viewModelScope.launch {
            isCameraPermissionGranted.emit(true)
        }
    }

    fun onPermissionDenied() {
        viewModelScope.launch {
            isCameraPermissionGranted.emit(false)
        }
    }

}

sealed interface PermissionUiState {
    object CheckingPermission : PermissionUiState
    data class PermissionChecked(val isGranted: Boolean) : PermissionUiState
}
