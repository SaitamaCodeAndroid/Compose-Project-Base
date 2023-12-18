package com.season.camera.edit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
internal fun EditImageRoute(
    viewModel: EditImageViewModel = hiltViewModel(),
    onNavigateToEdit: (String) -> Unit,
) {

    val imageFile by viewModel.imageFile.collectAsStateWithLifecycle()

    imageFile?.let {
        onNavigateToEdit(it.absolutePath)
    }

    EditImageScreen(
    )
}

@Composable
internal fun EditImageScreen(
) {

}
