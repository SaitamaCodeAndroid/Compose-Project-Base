package com.season.home

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.season.analytics.AnalyticsHelper
import com.season.analytics.LocalAnalyticsHelper
import com.season.data.util.NetworkMonitor
import com.season.designsystem.theme.NiaTheme
import com.season.model.DarkThemeConfig
import com.season.model.ThemeBrand

@Composable
internal fun HomeRoute(
    networkMonitor: NetworkMonitor,
    windowSizeClass: WindowSizeClass,
    analyticsHelper: AnalyticsHelper,
    viewModel: HomeViewModel = hiltViewModel()
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
            HomeScreen(
                networkMonitor = networkMonitor,
                windowSizeClass = windowSizeClass,
            )
        }
    }
}

/**
 * Returns `true` if the Android theme should be used, as a function of the [uiState].
 */
@Composable
private fun shouldUseAndroidTheme(
    uiState: HomeUiState,
): Boolean = when (uiState) {
     HomeUiState.Loading -> false
    is HomeUiState.Success -> when (uiState.userData.themeBrand) {
        ThemeBrand.DEFAULT -> false
        ThemeBrand.ANDROID -> true
    }
}

/**
 * Returns `true` if the dynamic color is disabled, as a function of the [uiState].
 */
@Composable
private fun shouldDisableDynamicTheming(
    uiState: HomeUiState,
): Boolean = when (uiState) {
    HomeUiState.Loading -> false
    is HomeUiState.Success -> !uiState.userData.useDynamicColor
}

/**
 * Returns `true` if dark theme should be used, as a function of the [uiState] and the
 * current system context.
 */
@Composable
private fun shouldUseDarkTheme(
    uiState: HomeUiState,
): Boolean = when (uiState) {
    HomeUiState.Loading -> isSystemInDarkTheme()
    is HomeUiState.Success -> when (uiState.userData.darkThemeConfig) {
        DarkThemeConfig.FOLLOW_SYSTEM -> isSystemInDarkTheme()
        DarkThemeConfig.LIGHT -> false
        DarkThemeConfig.DARK -> true
    }
}
