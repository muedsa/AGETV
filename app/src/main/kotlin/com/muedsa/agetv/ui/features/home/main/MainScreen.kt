package com.muedsa.agetv.ui.features.home.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.ImmersiveList
import androidx.tv.material3.MaterialTheme
import com.muedsa.agetv.model.LazyType
import com.muedsa.agetv.ui.AgePosterSize
import com.muedsa.agetv.ui.navigation.NavigationItems
import com.muedsa.agetv.viewmodel.HomePageViewModel
import com.muedsa.compose.tv.model.ContentModel
import com.muedsa.compose.tv.theme.ImageCardRowCardPadding
import com.muedsa.compose.tv.theme.ScreenPaddingLeft
import com.muedsa.compose.tv.widget.ContentBlock
import com.muedsa.compose.tv.widget.EmptyDataScreen
import com.muedsa.compose.tv.widget.ErrorMessageBoxState
import com.muedsa.compose.tv.widget.ErrorScreen
import com.muedsa.compose.tv.widget.ImageCardsRow
import com.muedsa.compose.tv.widget.LoadingScreen
import com.muedsa.compose.tv.widget.ScreenBackgroundState
import com.muedsa.compose.tv.widget.ScreenBackgroundType
import com.muedsa.compose.tv.widget.StandardImageCardsRow
import com.muedsa.uitl.LogUtil

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: HomePageViewModel = hiltViewModel(),
    backgroundState: ScreenBackgroundState,
    errorMsgBoxState: ErrorMessageBoxState,
    onNavigate: (NavigationItems, List<String>?) -> Unit = { _, _ -> }
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    val homeData by remember { viewModel.homeDataState }

    var title by remember { mutableStateOf("") }
    var subTitle by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = homeData.type, key2 = homeData.error) {
        if (homeData.type == LazyType.FAILURE) {
            errorMsgBoxState.error(homeData.error)
        }
    }

    if (homeData.type == LazyType.SUCCESS) {
        if (homeData.data != null) {
            val latestList = homeData.data!!.latest
            val recommendList = homeData.data!!.recommend
            val weekList = homeData.data!!.weekList

            val firstRowHeight =
                (MaterialTheme.typography.titleLarge.fontSize.value * configuration.fontScale + 0.5f).dp +
                        ImageCardRowCardPadding * 3 + AgePosterSize.height
            val tabHeight =
                (MaterialTheme.typography.labelLarge.fontSize.value * configuration.fontScale + 0.5f).dp +
                        24.dp * 2 +
                        6.dp * 2

            LaunchedEffect(key1 = homeData) {
                if (latestList.isNotEmpty()) {
                    val animeModel = latestList[0]
                    title = animeModel.title
                    subTitle = animeModel.newTitle
                    backgroundState.url = animeModel.picSmall
                    backgroundState.type = ScreenBackgroundType.SCRIM
                }
            }

            TvLazyColumn(
                modifier = Modifier
                    .offset(x = ScreenPaddingLeft - ImageCardRowCardPadding),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {

                // 最近更新
                item {
                    ImmersiveList(
                        background = { _, _ ->
                            ContentBlock(
                                modifier = Modifier
                                    .width(screenWidth / 2)
                                    .height(screenHeight - firstRowHeight - tabHeight - 20.dp),
                                model = ContentModel(title = title, subtitle = subTitle),
                                descriptionMaxLines = 3
                            )
                        },
                    ) {
                        Column {
                            Spacer(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(screenHeight - firstRowHeight - tabHeight)
                            )
                            ImageCardsRow(
                                title = "最近更新",
                                modelList = latestList,
                                imageFn = { _, item ->
                                    item.picSmall
                                },
                                imageSize = AgePosterSize,
                                onItemFocus = { _, anime ->
                                    title = anime.title
                                    subTitle = anime.newTitle
                                    backgroundState.url = anime.picSmall
                                    backgroundState.type = ScreenBackgroundType.SCRIM
                                },
                                onItemClick = { _, anime ->
                                    LogUtil.d("Click $anime")
                                    onNavigate(NavigationItems.Detail, listOf(anime.aid.toString()))
                                }
                            )
                        }
                    }
                }

                // 每日推荐
                item {
                    StandardImageCardsRow(
                        title = "每日推荐",
                        modelList = recommendList,
                        imageFn = { _, item ->
                            item.picSmall
                        },
                        imageSize = AgePosterSize,
                        contentFn = { _, item ->
                            ContentModel(item.title, subtitle = item.newTitle)
                        },
                        onItemFocus = { _, anime ->
                            title = anime.title
                            subTitle = anime.newTitle
                            backgroundState.url = anime.picSmall
                            backgroundState.type = ScreenBackgroundType.BLUR
                        },
                        onItemClick = { _, anime ->
                            LogUtil.d("Click $anime")
                            onNavigate(NavigationItems.Detail, listOf(anime.aid.toString()))
                        }
                    )
                }

                // 每周放送
                item {
                    WeekAnimeListWidget(model = weekList)
                }

                // bottom space
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        } else {
            EmptyDataScreen()
        }
    } else if (homeData.type == LazyType.FAILURE) {
        ErrorScreen {
            viewModel.fetchHome()
        }
    } else {
        LoadingScreen()
    }
}

