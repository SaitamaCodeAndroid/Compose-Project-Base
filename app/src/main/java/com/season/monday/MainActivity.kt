package com.season.monday

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.appdistribution.InterruptionLevel
import com.google.firebase.appdistribution.ktx.appDistribution
import com.google.firebase.ktx.Firebase
import com.season.home.HomeActivity
import com.season.monday.MainActivityUiState.Loading
import com.season.monday.MainActivityUiState.Success
import com.season.onboarding.OnboardingRoute
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Firebase.appDistribution.showFeedbackNotification(
            // Text providing notice to your testers about collection and
            // processing of their feedback data
            R.string.app_name,
            // The level of interruption for the notification
            InterruptionLevel.HIGH)

        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Turn off the decor fitting system windows, which allows us to handle insets,
        // including IME animations
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {

            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(key1 = uiState) {
                // Keep the splash screen on-screen until the UI state is loaded. This condition is
                // evaluated each time the app needs to be redrawn so it should be fast to avoid blocking
                // the UI.
                splashScreen.setKeepOnScreenCondition {
                    when (uiState) {
                        Loading -> true
                        is Success -> false
                    }
                }
                if ((uiState as? Success)?.shouldHideOnboarding == false) {
                    navigateToHome()
                }
            }

//            if ((uiState as? Success)?.shouldHideOnboarding == false) {
//                OnboardingRoute(
//                    onGetStartedClick = ::navigateToHome
//                )
//            }
        }
    }

    private fun navigateToHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}
