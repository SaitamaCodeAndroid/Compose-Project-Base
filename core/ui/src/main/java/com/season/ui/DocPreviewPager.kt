package com.season.ui

import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.season.designsystem.component.DynamicAsyncImage
import com.season.designsystem.component.NiaBackground
import com.season.designsystem.theme.NiaTheme
import com.season.model.DisplayImageItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DocPreviewPager(
    modifier: Modifier = Modifier,
    pagerState: PagerState = rememberPagerState(),
    imageList: List<DisplayImageItem>,
    isPageMarginOn: Boolean,
) {

    val animatedPaddingValue by animateDpAsState(
        targetValue = if (isPageMarginOn) 16.dp else 0.dp,
        animationSpec = TweenSpec(durationMillis = 300),
        label = ""
    )

    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize(),
        pageCount = imageList.size,
        verticalAlignment = Alignment.CenterVertically
    ) { pageIndex ->
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxHeight()
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(4.dp),
                )
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White)
                .padding(animatedPaddingValue),
            contentAlignment = Alignment.Center
        ) {
            DynamicAsyncImage(
                modifier = Modifier.aspectRatio(1f / 1.4142f),
                contentScale = ContentScale.Fit,
                imageUrl = imageList[pageIndex].url,
                contentDescription = null,
                placeholder = painterResource(R.drawable.img_placeholder_4_3),
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun PreviewDocPreviewPager() {
    NiaTheme {
        NiaBackground {
            DocPreviewPager(
                imageList = listOf(
                    DisplayImageItem(name = "Abc", url = "images/Abc.jpg")
                ),
                isPageMarginOn = true,
            )
        }
    }
}
