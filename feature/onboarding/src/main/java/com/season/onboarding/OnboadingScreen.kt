package com.season.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.season.designsystem.component.NiaBackground
import com.season.designsystem.component.NiaButton
import com.season.designsystem.icon.NiaIcons
import com.season.designsystem.theme.NiaTheme
import com.season.onboarding.navigation.OnboardingPageType
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingRoute(
    onGetStartedClick: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {

    val scope = rememberCoroutineScope()

    val pageState = rememberPagerState()

    OnboadingScreen(
        pagerState = pageState,
        onPageClick = {
            if (pageState.canScrollForward) {
                scope.launch {
                    pageState.animateScrollToPage(pageState.currentPage + 1)
                }
            } else {
                viewModel.onGetStartedClick()
                onGetStartedClick()
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun OnboadingScreen(
    pagerState: PagerState = rememberPagerState(),
    onPageClick: () -> Unit,
) {

    val pageCount = OnboardingPageType.values().size

    Column {

        HorizontalPager(
            modifier = Modifier.weight(1f),
            pageCount = pageCount,
            state = pagerState
        ) { index ->
            OnboardingPage(page = OnboardingPageType.values()[index])
        }

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) {
                    Color.DarkGray
                } else {
                    Color.LightGray
                }
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(12.dp)

                )
            }
        }

        NiaButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            onClick = onPageClick
        ) {
            Text(
                text = stringResource(id = OnboardingPageType.values()[pagerState.currentPage].actionTextId),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
internal fun OnboardingPage(
    page: OnboardingPageType,
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Icon(
            modifier = Modifier.size(64.dp),
            imageVector = NiaIcons.Bookmark,
            contentDescription = ""
        )

        Text(
            text = stringResource(id = page.titleId),
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = stringResource(id = page.descriptionId),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun PreviewOnboardingScreen() {
    NiaTheme {
        NiaBackground {
            OnboadingScreen {}
        }
    }
}