package com.season.camera.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.season.camera.edit.navigation.fileNameArg
import com.season.data.repository.FileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class EditImageViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val fileRepository: FileRepository,
) : ViewModel() {

    private val fileName = checkNotNull(savedStateHandle.get<String>(fileNameArg))

    val imageFile = MutableStateFlow<File?>(null)

    init {
        viewModelScope.launch {
            imageFile.emit(fileRepository.getImageByName(fileName))
        }
    }

}