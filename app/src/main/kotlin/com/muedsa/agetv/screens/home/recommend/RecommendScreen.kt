package com.muedsa.agetv.screens.home.recommend

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.muedsa.agetv.model.LazyType
import com.muedsa.agetv.screens.NavigationItems
import com.muedsa.agetv.screens.home.useLocalHomeScreenBackgroundState
import com.muedsa.agetv.screens.nav
import com.muedsa.agetv.theme.AgePosterSize
import com.muedsa.compose.tv.model.ContentModel
import com.muedsa.compose.tv.theme.ImageCardRowCardPadding
import com.muedsa.compose.tv.theme.ScreenPaddingLeft
import com.muedsa.compose.tv.useLocalNavHostController
import com.muedsa.compose.tv.useLocalToastMsgBoxController
import com.muedsa.compose.tv.widget.CardType
import com.muedsa.compose.tv.widget.ErrorScreen
import com.muedsa.compose.tv.widget.ImageContentCard
import com.muedsa.compose.tv.widget.LoadingScreen
import com.muedsa.compose.tv.widget.ScreenBackgroundType
import com.muedsa.uitl.LogUtil


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RecommendScreen(
    viewModel: RecommendViewModel = hiltViewModel()
) {
    val backgroundState = useLocalHomeScreenBackgroundState()
    val toastController = useLocalToastMsgBoxController()
    val navController = useLocalNavHostController()

    val recommendLD by viewModel.recommendLDSF.collectAsState()

    LaunchedEffect(key1 = recommendLD) {
        if (recommendLD.type == LazyType.FAILURE) {
            toastController.error(recommendLD.error)
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

                LazyVerticalGrid(
                    modifier = Modifier
                        .testTag("recommendScreen_grid")
                        .padding(start = 0.dp, top = 20.dp, end = 20.dp, bottom = 20.dp),
                    columns = GridCells.Adaptive(AgePosterSize.width + ImageCardRowCardPadding),
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
                                navController.nav(
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