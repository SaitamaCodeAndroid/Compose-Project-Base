package com.season.onboarding.navigation

import androidx.annotation.StringRes
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.season.onboarding.OnboardingRoute
import com.season.onboarding.R.string

const val onboadringRoute = "onboarding_route"
fun NavController.navigateToOnboarding(navOptions: NavOptions? = null) {
    this.navigate(onboadringRoute, navOptions)
}

fun NavGraphBuilder.onboardingScreen(
    onGetStartedClick: () -> Unit,
) {
    composable(route = onboadringRoute) {
        OnboardingRoute(
            onGetStartedClick = onGetStartedClick,
        )
    }
}

enum class OnboardingPageType(
    @StringRes val titleId: Int,
    @StringRes val descriptionId: Int,
    @StringRes val actionTextId: Int,
) {
    FIRST(
        titleId = string.onboarding_page_first_title,
        descriptionId = string.onboarding_page_first_description,
        actionTextId = string.onboarding_page_first_action_text
    ),
    SECOND(
        titleId = string.onboarding_page_second_title,
        descriptionId = string.onboarding_page_second_description,
        actionTextId = string.onboarding_page_second_action_text
    ),
    THIRD(
        titleId = string.onboarding_page_third_title,
        descriptionId = string.onboarding_page_third_description,
        actionTextId = string.onboarding_page_third_action_text
    )
}