package com.season.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.season.designsystem.component.NiaBackground
import com.season.designsystem.component.NiaLoadingWheel
import com.season.designsystem.component.NiaTab
import com.season.designsystem.component.ScrollableNiaTabRow
import com.season.designsystem.theme.NiaTheme
import com.season.model.DisplayFileItem
import com.season.model.FileFolder
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileCardListWithTab(
    modifier: Modifier = Modifier,
    selectedTabIndex: Int,
    tabs: List<FileFolder>,
    onTabSelected: (Int) -> Unit,
    pagerState: PagerState = rememberPagerState(),
    lazyListState: LazyListState = rememberLazyListState(),
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ScrollableNiaTabRow(
            selectedTabIndex = selectedTabIndex
        ) {
            tabs.forEachIndexed { index, item ->
                when (item) {
                    is FileFolder.All -> {
                        NiaTab(
                            selected = selectedTabIndex == index,
                            onClick = { onTabSelected(index) },
                            text = { Text(text = stringResource(id = R.string.all)) },
                        )
                    }

                    is FileFolder.Common -> {
                        NiaTab(
                            selected = selectedTabIndex == index,
                            onClick = { onTabSelected(index) },
                            text = { Text(text = item.name) },
                        )
                    }
                }
            }
        }

        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState,
            userScrollEnabled = false,
            pageCount = tabs.size,
        ) { index ->

            val files = tabs[index].files

            if (files.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    NiaLoadingWheel(
                        modifier = modifier,
                        contentDesc = stringResource(id = R.string.loading),
                    )
                }
            } else {

                LaunchedEffect(Unit) {
                    lazyListState.scrollToItem(0)
                }

                LazyColumn(
                    state = lazyListState,
                ) {
                    items(
                        count = files.size,
                        key = {
                            when (val item = files[it]) {
                                is DisplayFileItem.GroupDate -> item.date.toString()
                                is DisplayFileItem.File -> item.id
                            }
                        }
                    ) {
                        when (val item = files[it]) {

                            is DisplayFileItem.GroupDate -> {
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    text = item.date.toString(),
                                    textAlign = TextAlign.Start,
                                    maxLines = 1
                                )
                            }

                            is DisplayFileItem.File -> {
                                FileCard(item)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
private fun PreviewFileCardListWithTab() {

    val current = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    NiaTheme {
        NiaBackground {
            FileCardListWithTab(
                selectedTabIndex = 1,
                tabs = listOf(
                    FileFolder.All(),
                    FileFolder.Common(
                        "Images",
                        "",
                        listOf(
                            DisplayFileItem.GroupDate(
                                date = current.date
                            ),
                            DisplayFileItem.File(
                                id = 0,
                                name = "Test image 1",
                                path = "",
                                date = current
                            ),
                            DisplayFileItem.File(
                                id = 1,
                                name = "Test image 2",
                                path = "",
                                date = current
                            )
                        )
                    ),
                    FileFolder.Common("Messenger", ""),
                    FileFolder.Common("Telegram", ""),
                ),
                onTabSelected = {},
                pagerState = rememberPagerState(
                    initialPage = 1
                )
            )
        }
    }
}
