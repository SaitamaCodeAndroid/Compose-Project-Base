package com.season.imagetofile

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.season.analytics.AnalyticsHelper
import com.season.analytics.LocalAnalyticsHelper
import com.season.designsystem.component.NiaBackground
import com.season.designsystem.theme.NiaTheme
import com.season.imagetofile.ImageToFileUiState.Loading
import com.season.imagetofile.ImageToFileUiState.Success
import com.season.imagetofile.navigation.ImageToFileNavHost
import com.season.model.DarkThemeConfig
import com.season.model.ThemeBrand

@Composable
internal fun ImageToFileRoute(
    navController: NavHostController,
    analyticsHelper: AnalyticsHelper,
    viewModel: ImageToFileViewModel = hiltViewModel(),
    onNavigateToEditImage: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val systemUiController = rememberSystemUiController()
    val darkTheme = shouldUseDarkTheme(uiState)

    // Update the dark content of the system bars to match the theme
    DisposableEffect(systemUiController, darkTheme) {
        systemUiController.systemBarsDarkContentEnabled = !darkTheme
        onDispose {}
    }

    CompositionLocalProvider(LocalAnalyticsHelper provides analyticsHelper) {
        NiaTheme(
            darkTheme = darkTheme,
            androidTheme = shouldUseAndroidTheme(uiState),
            disableDynamicTheming = shouldDisableDynamicTheming(uiState),
        ) {
            CameraScreen(
                navController = navController,
                onNavigateToEditImage = onNavigateToEditImage,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun CameraScreen(
    navController: NavHostController = rememberNavController(),
    onNavigateToEditImage: (String) -> Unit,
) {

    NiaBackground {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
        ) { padding ->
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .consumeWindowInsets(padding)
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom,
                        ),
                    ),
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ImageToFileNavHost(
                        navController = navController,
                        onNavigateToEditImage = onNavigateToEditImage
                    )
                }
            }
        }
    }
}


/**
 * Returns `true` if the Android theme should be used, as a function of the [uiState].
 */
@Composable
private fun shouldUseAndroidTheme(
    uiState: ImageToFileUiState,
): Boolean = when (uiState) {
    Loading -> false
    is Success -> when (uiState.userData.themeBrand) {
        ThemeBrand.DEFAULT -> false
        ThemeBrand.ANDROID -> true
    }
}

/**
 * Returns `true` if the dynamic color is disabled, as a function of the [uiState].
 */
@Composable
private fun shouldDisableDynamicTheming(
    uiState: ImageToFileUiState,
): Boolean = when (uiState) {
    Loading -> false
    is Success -> !uiState.userData.useDynamicColor
}

/**
 * Returns `true` if dark theme should be used, as a function of the [uiState] and the
 * current system context.
 */
@Composable
private fun shouldUseDarkTheme(
    uiState: ImageToFileUiState,
): Boolean = when (uiState) {
    Loading -> isSystemInDarkTheme()
    is Success -> when (uiState.userData.darkThemeConfig) {
        DarkThemeConfig.FOLLOW_SYSTEM -> isSystemInDarkTheme()
        DarkThemeConfig.LIGHT -> false
        DarkThemeConfig.DARK -> true
    }
}
