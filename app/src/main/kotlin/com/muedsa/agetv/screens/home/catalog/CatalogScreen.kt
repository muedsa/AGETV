package com.muedsa.agetv.screens.home.catalog

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Card
import androidx.tv.material3.Icon
import androidx.tv.material3.OutlinedButton
import androidx.tv.material3.OutlinedIconButton
import androidx.tv.material3.Text
import com.muedsa.agetv.model.LazyType
import com.muedsa.agetv.model.age.AgeCatalogOption
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
fun CatalogScreen(
    viewModel: CatalogViewModel = hiltViewModel()
) {
    val navController = useLocalNavHostController()
    val backgroundState = useLocalHomeScreenBackgroundState()
    val toastMsgBoxController = useLocalToastMsgBoxController()

    val query by viewModel.querySF.collectAsState()
    val searchAnimeLP by viewModel.animeLPSF.collectAsState()

    var optionsExpand by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = searchAnimeLP) {
        if (searchAnimeLP.type == LazyType.FAILURE) {
            toastMsgBoxController.error(searchAnimeLP.error)
        }
    }

    BackHandler(enabled = optionsExpand) {
        optionsExpand = false
    }

    Column(modifier = Modifier.padding(start = ScreenPaddingLeft)) {
        Row(
            modifier = Modifier
                .testTag("catalogScreen_options")
                .fillMaxWidth()
                .offset(x = -ScreenPaddingLeft)
                .padding(vertical = 30.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(onClick = {
                optionsExpand = !optionsExpand
            }) {
                Text(text = "筛选项")
                Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                Icon(
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                    imageVector = if (optionsExpand) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.ArrowDropDown,
                    contentDescription = "展开筛选项"
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedIconButton(onClick = {
                viewModel.resetCatalogOptions()
            }) {
                Icon(
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = "重置筛选项"
                )
            }
        }

        if (optionsExpand) {
            // 筛选项
            LazyColumn(contentPadding = PaddingValues(top = ImageCardRowCardPadding)) {
                item {
                    CatalogOptionsWidget(
                        title = "排序",
                        selectedIndex = AgeCatalogOption.Order.indexOf(query.order),
                        options = AgeCatalogOption.Order,
                        onClick = { _, option ->
                            if (query.order == option) {
                                viewModel.querySF.value =
                                    query.copy(order = AgeCatalogOption.Order[0])
                            } else {
                                viewModel.querySF.value = query.copy(order = option)
                            }
                        }
                    )
                }
                item {
                    CatalogOptionsWidget(
                        title = "区域",
                        selectedIndex = AgeCatalogOption.Regions.indexOf(query.region),
                        options = AgeCatalogOption.Regions,
                        onClick = { _, option ->
                            if (query.region == option) {
                                viewModel.querySF.value =
                                    query.copy(region = AgeCatalogOption.Regions[0])
                            } else {
                                viewModel.querySF.value = query.copy(region = option)
                            }
                        }
                    )
                }
                item {
                    CatalogOptionsWidget(
                        title = "类型",
                        selectedIndex = AgeCatalogOption.Genres.indexOf(query.genre),
                        options = AgeCatalogOption.Genres,
                        onClick = { _, option ->
                            if (query.genre == option) {
                                viewModel.querySF.value =
                                    query.copy(genre = AgeCatalogOption.Genres[0])
                            } else {
                                viewModel.querySF.value = query.copy(genre = option)
                            }
                        }
                    )
                }
                item {
                    CatalogOptionsWidget(
                        title = "年份",
                        selectedIndex = AgeCatalogOption.Years.indexOf(query.year),
                        options = AgeCatalogOption.Years,
                        onClick = { _, option ->
                            if (query.year == option) {
                                viewModel.querySF.value =
                                    query.copy(year = AgeCatalogOption.Years[0])
                            } else {
                                viewModel.querySF.value = query.copy(year = option)
                            }
                        }
                    )
                }
                item {
                    CatalogOptionsWidget(
                        title = "季度",
                        selectedIndex = AgeCatalogOption.Seasons.indexOf(query.season),
                        options = AgeCatalogOption.Seasons,
                        onClick = { _, option ->
                            if (query.season == option) {
                                viewModel.querySF.value =
                                    query.copy(season = AgeCatalogOption.Seasons[0])
                            } else {
                                viewModel.querySF.value = query.copy(season = option)
                            }
                        }
                    )
                }
                item {
                    CatalogOptionsWidget(
                        title = "状态",
                        selectedIndex = AgeCatalogOption.Status.indexOf(query.status),
                        options = AgeCatalogOption.Status,
                        onClick = { _, option ->
                            if (query.status == option) {
                                viewModel.querySF.value =
                                    query.copy(status = AgeCatalogOption.Status[0])
                            } else {
                                viewModel.querySF.value = query.copy(status = option)
                            }
                        }
                    )
                }
                item {
                    CatalogOptionsWidget(
                        title = "标签",
                        selectedIndex = AgeCatalogOption.Labels.indexOf(query.label),
                        options = AgeCatalogOption.Labels,
                        onClick = { _, option ->
                            if (query.label == option) {
                                viewModel.querySF.value =
                                    query.copy(label = AgeCatalogOption.Labels[0])
                            } else {
                                viewModel.querySF.value = query.copy(label = option)
                            }
                        }
                    )
                }
                item {
                    CatalogOptionsWidget(
                        title = "资源",
                        selectedIndex = AgeCatalogOption.Resources.indexOf(query.resource),
                        options = AgeCatalogOption.Resources,
                        onClick = { _, option ->
                            if (query.resource == option) {
                                viewModel.querySF.value =
                                    query.copy(resource = AgeCatalogOption.Resources[0])
                            } else {
                                viewModel.querySF.value = query.copy(resource = option)
                            }
                        }
                    )
                }
            }
        } else {

            LazyVerticalGrid(
                columns = GridCells.Adaptive(AgePosterSize.width + ImageCardRowCardPadding),
                contentPadding = PaddingValues(
                    top = ImageCardRowCardPadding,
                    bottom = ImageCardRowCardPadding
                ),
                modifier = Modifier.testTag("catalogScreen_grid")
            ) {
                itemsIndexed(
                    items = searchAnimeLP.list,
                    key = { _, item -> item.id }
                ) { index, item ->
                    val itemFocusRequester = remember {
                        FocusRequester()
                    }
                    ImageContentCard(
                        modifier = Modifier
                            .padding(end = ImageCardRowCardPadding)
                            .focusRequester(itemFocusRequester),
                        url = item.cover,
                        imageSize = AgePosterSize,
                        type = CardType.STANDARD,
                        model = ContentModel(
                            item.name,
                            subtitle = item.tags,
                            description = item.status
                        ),
                        onItemFocus = {
                            backgroundState.url = item.cover
                            backgroundState.type = ScreenBackgroundType.BLUR
                        },
                        onItemClick = {
                            LogUtil.d("Click $item")
                            navController.nav(
                                NavigationItems.Detail,
                                listOf(item.id.toString())
                            )
                        }
                    )

                    LaunchedEffect(key1 = Unit) {
                        if (searchAnimeLP.offset == index) {
                            itemFocusRequester.requestFocus()
                        }
                    }
                }

                if (searchAnimeLP.type != LazyType.LOADING && searchAnimeLP.hasNext) {
                    item {
                        Card(
                            modifier = Modifier
                                .size(AgePosterSize)
                                .padding(end = ImageCardRowCardPadding),
                            onClick = {
                                viewModel.catalog(searchAnimeLP)
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
}

