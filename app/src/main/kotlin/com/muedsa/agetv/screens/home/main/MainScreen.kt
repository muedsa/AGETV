package com.muedsa.agetv.screens.home.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.muedsa.agetv.BuildConfig
import com.muedsa.agetv.model.LazyType
import com.muedsa.agetv.screens.NavigationItems
import com.muedsa.agetv.screens.home.useLocalHomeScreenBackgroundState
import com.muedsa.agetv.screens.navigate
import com.muedsa.agetv.theme.AgePosterSize
import com.muedsa.compose.tv.model.ContentModel
import com.muedsa.compose.tv.theme.ImageCardRowCardPadding
import com.muedsa.compose.tv.theme.ScreenPaddingLeft
import com.muedsa.compose.tv.useLocalNavHostController
import com.muedsa.compose.tv.useLocalToastMsgBoxController
import com.muedsa.compose.tv.widget.ContentBlock
import com.muedsa.compose.tv.widget.EmptyDataScreen
import com.muedsa.compose.tv.widget.ErrorScreen
import com.muedsa.compose.tv.widget.ImageCardsRow
import com.muedsa.compose.tv.widget.ImmersiveList
import com.muedsa.compose.tv.widget.LoadingScreen
import com.muedsa.compose.tv.widget.ScreenBackgroundType
import com.muedsa.compose.tv.widget.StandardImageCardsRow
import com.muedsa.uitl.LogUtil

@Composable
fun MainScreen(
    viewModel: MainScreenViewModel = hiltViewModel()
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    val backgroundState = useLocalHomeScreenBackgroundState()
    val toastController = useLocalToastMsgBoxController()
    val navController = useLocalNavHostController()

    val homeData by viewModel.homeDataSF.collectAsState()

    var title by remember { mutableStateOf("") }
    var subTitle by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = homeData.type, key2 = homeData.error) {
        if (homeData.type == LazyType.FAILURE) {
            toastController.error(homeData.error)
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

            LazyColumn(
                modifier = Modifier.padding(start = ScreenPaddingLeft - ImageCardRowCardPadding)
            ) {

                // 最近更新
                item {
                    ImmersiveList(
                        background = {
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
                                modifier = Modifier.testTag("mainScreen_row_1"),
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
                                    navController.navigate(
                                        NavigationItems.Detail,
                                        listOf(anime.aid.toString())
                                    )
                                }
                            )
                        }
                    }
                }

                // 每日推荐
                item {
                    StandardImageCardsRow(
                        modifier = Modifier.testTag("mainScreen_row_2"),
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
                            navController.navigate(
                                NavigationItems.Detail,
                                listOf(anime.aid.toString())
                            )
                        }
                    )
                }

                // 每周放送
                item {
                    WeekAnimeListWidget(model = weekList) { _, _, item ->
                        LogUtil.d("Click $item")
                        navController.navigate(NavigationItems.Detail, listOf(item.id.toString()))
                    }
                }

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .align(Alignment.CenterEnd)
                                .graphicsLayer { alpha = 0.6f },
                            text = "APP版本: ${BuildConfig.BUILD_TYPE}-${BuildConfig.VERSION_NAME}(${BuildConfig.VERSION_CODE})",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        } else {
            EmptyDataScreen()
        }
    } else if (homeData.type == LazyType.LOADING) {
        LoadingScreen()
    } else {
        ErrorScreen {
            viewModel.refreshHomeData()
        }
    }
}

