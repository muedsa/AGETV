package com.muedsa.agetv.ui.features.home.recommend

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.muedsa.agetv.ui.navigation.NavigationItems
import com.muedsa.agetv.viewmodel.RecommendViewModel
import com.muedsa.compose.tv.model.ContentModel
import com.muedsa.compose.tv.theme.ImageCardRowCardPadding
import com.muedsa.compose.tv.theme.ScreenPaddingLeft
import com.muedsa.compose.tv.widget.CardType
import com.muedsa.compose.tv.widget.ErrorMessageBoxState
import com.muedsa.compose.tv.widget.ImageContentCard
import com.muedsa.compose.tv.widget.ScreenBackgroundState
import com.muedsa.compose.tv.widget.ScreenBackgroundType
import com.muedsa.uitl.LogUtil


@OptIn(ExperimentalComposeUiApi::class, ExperimentalTvMaterial3Api::class)
@Composable
fun RecommendScreen(
    viewModel: RecommendViewModel = hiltViewModel(),
    backgroundState: ScreenBackgroundState,
    errorMsgBoxState: ErrorMessageBoxState,
    onNavigate: (NavigationItems, List<String>?) -> Unit = { _, _ -> }
) {

    val recommendLD by remember { viewModel.recommendLDState }

    LaunchedEffect(key1 = recommendLD) {
        if (recommendLD.type == LazyType.FAILURE) {
            errorMsgBoxState.error(recommendLD.error)
        }
    }

    Column(modifier = Modifier.padding(start = ScreenPaddingLeft)) {
        Text(
            text = "推荐列表",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge
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
                ) { index, item ->
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
                            onNavigate(NavigationItems.Detail, listOf(item.aid.toString()))
                        }
                    )
                }
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.fetchRecommend()
    }

}