package com.muedsa.agetv.screens.home.latest

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Card
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.muedsa.agetv.model.LazyType
import com.muedsa.agetv.screens.NavigationItems
import com.muedsa.agetv.screens.home.useLocalHomeScreenBackgroundState
import com.muedsa.agetv.screens.nav
import com.muedsa.agetv.theme.AgePosterSize
import com.muedsa.agetv.theme.GirdLastItemHeight
import com.muedsa.compose.tv.model.ContentModel
import com.muedsa.compose.tv.theme.ImageCardRowCardPadding
import com.muedsa.compose.tv.theme.ScreenPaddingLeft
import com.muedsa.compose.tv.useLocalNavHostController
import com.muedsa.compose.tv.useLocalToastMsgBoxController
import com.muedsa.compose.tv.widget.CardType
import com.muedsa.compose.tv.widget.ImageContentCard
import com.muedsa.compose.tv.widget.ScreenBackgroundType
import com.muedsa.uitl.LogUtil

@Composable
fun LatestUpdateScreen(
    viewModel: LatestUpdateViewModel = hiltViewModel()
) {
    val navController = useLocalNavHostController()
    val backgroundState = useLocalHomeScreenBackgroundState()
    val toastController = useLocalToastMsgBoxController()

    val latestUpdateLP by viewModel.latestUpdateLPSF.collectAsState()

    LaunchedEffect(key1 = latestUpdateLP) {
        if (latestUpdateLP.type == LazyType.FAILURE) {
            toastController.error(latestUpdateLP.error)
        }
    }

    Column(modifier = Modifier.padding(start = ScreenPaddingLeft)) {
        Text(
            text = "最近更新",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineMedium
        )

        LazyVerticalGrid(
            modifier = Modifier
                .testTag("latestUpdateScreen_grid")
                .padding(start = 0.dp, top = 20.dp, end = 20.dp, bottom = 20.dp),
            columns = GridCells.Adaptive(AgePosterSize.width + ImageCardRowCardPadding),
            contentPadding = PaddingValues(
                top = ImageCardRowCardPadding,
                bottom = ImageCardRowCardPadding
            )
        ) {
            itemsIndexed(
                items = latestUpdateLP.list,
                key = { _, item -> item.aid }
            ) { index, item ->
                val itemFocusRequester = remember {
                    FocusRequester()
                }
                ImageContentCard(
                    modifier = Modifier
                        .padding(end = ImageCardRowCardPadding)
                        .focusRequester(itemFocusRequester),
                    url = item.picSmall,
                    imageSize = AgePosterSize,
                    type = CardType.STANDARD,
                    model = ContentModel(
                        item.title,
                        subtitle = item.newTitle
                    ),
                    onItemFocus = {
                        backgroundState.url = item.picSmall
                        backgroundState.type = ScreenBackgroundType.BLUR
                    },
                    onItemClick = {
                        LogUtil.d("Click $item")
                        navController.nav(NavigationItems.Detail, listOf(item.aid.toString()))
                    }
                )

                LaunchedEffect(key1 = Unit) {
                    if (latestUpdateLP.offset == index) {
                        itemFocusRequester.requestFocus()
                    }
                }
            }

            if (latestUpdateLP.type != LazyType.LOADING && latestUpdateLP.hasNext) {
                item {
                    Card(
                        modifier = Modifier
                            .size(AgePosterSize)
                            .padding(end = ImageCardRowCardPadding),
                        onClick = {
                            viewModel.latestUpdate()
                        }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "继续加载")
                        }
                    }
                }
            }

            // 最后一行占位
            item {
                Spacer(modifier = Modifier.height(GirdLastItemHeight))
            }
        }
    }
}