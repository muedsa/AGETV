package com.muedsa.agetv.ui.features.home.recommend

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.itemsIndexed
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.muedsa.agetv.model.LazyType
import com.muedsa.agetv.ui.AgePosterSize
import com.muedsa.agetv.ui.features.home.LocalHomeScreenBackgroundState
import com.muedsa.agetv.ui.navigation.LocalAppNavController
import com.muedsa.agetv.ui.navigation.NavigationItems
import com.muedsa.agetv.ui.navigation.navigate
import com.muedsa.agetv.viewmodel.RecommendViewModel
import com.muedsa.compose.tv.model.ContentModel
import com.muedsa.compose.tv.theme.ImageCardRowCardPadding
import com.muedsa.compose.tv.theme.ScreenPaddingLeft
import com.muedsa.compose.tv.widget.CardType
import com.muedsa.compose.tv.widget.ErrorScreen
import com.muedsa.compose.tv.widget.ImageContentCard
import com.muedsa.compose.tv.widget.LoadingScreen
import com.muedsa.compose.tv.widget.LocalErrorMsgBoxState
import com.muedsa.compose.tv.widget.ScreenBackgroundType
import com.muedsa.uitl.LogUtil


@OptIn(ExperimentalComposeUiApi::class, ExperimentalTvMaterial3Api::class)
@Composable
fun RecommendScreen(
    viewModel: RecommendViewModel = hiltViewModel()
) {
    val backgroundState = LocalHomeScreenBackgroundState.current
    val errorMsgBoxState = LocalErrorMsgBoxState.current
    val navController = LocalAppNavController.current

    val recommendLD by viewModel.recommendLDSF.collectAsState()

    LaunchedEffect(key1 = recommendLD) {
        if (recommendLD.type == LazyType.FAILURE) {
            errorMsgBoxState.error(recommendLD.error)
        }
    }

    if (recommendLD.type == LazyType.SUCCESS) {
        Column(modifier = Modifier.padding(start = ScreenPaddingLeft)) {
            Text(
                text = "推荐列表",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineMedium
            )

            if (!recommendLD.data.isNullOrEmpty()) {
                val recommendList = recommendLD.data!!
                val gridFocusRequester = remember { FocusRequester() }

                TvLazyVerticalGrid(
                    modifier = Modifier
                        .padding(start = 0.dp, top = 20.dp, end = 20.dp, bottom = 20.dp)
                        .focusRequester(gridFocusRequester)
                        .focusProperties {
                            exit = { gridFocusRequester.saveFocusedChild(); FocusRequester.Default }
                            enter = {
                                if (gridFocusRequester.restoreFocusedChild()) {
                                    LogUtil.d("grid restoreFocusedChild")
                                    FocusRequester.Cancel
                                } else {
                                    LogUtil.d("grid focused default child")
                                    FocusRequester.Default
                                }
                            }
                        },
                    columns = TvGridCells.Adaptive(AgePosterSize.width + ImageCardRowCardPadding),
                    contentPadding = PaddingValues(
                        top = ImageCardRowCardPadding,
                        bottom = ImageCardRowCardPadding
                    )
                ) {
                    itemsIndexed(
                        items = recommendList,
                        key = { _, item -> item.aid }
                    ) { _, item ->
                        ImageContentCard(
                            modifier = Modifier.padding(end = ImageCardRowCardPadding),
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
                                navController.navigate(
                                    NavigationItems.Detail,
                                    listOf(item.aid.toString())
                                )
                            }
                        )
                    }
                }
            }
        }
    } else if (recommendLD.type == LazyType.FAILURE) {
        ErrorScreen {
            viewModel.refreshRecommend()
        }
    } else {
        LoadingScreen()
    }

}