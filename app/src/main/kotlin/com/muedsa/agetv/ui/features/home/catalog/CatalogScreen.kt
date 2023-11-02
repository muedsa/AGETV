package com.muedsa.agetv.ui.features.home.catalog

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.foundation.lazy.grid.TvGridCells
import androidx.tv.foundation.lazy.grid.TvLazyVerticalGrid
import androidx.tv.foundation.lazy.grid.itemsIndexed
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Card
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.OutlinedButton
import androidx.tv.material3.OutlinedIconButton
import androidx.tv.material3.Text
import com.muedsa.agetv.model.LazyPagedList
import com.muedsa.agetv.model.LazyType
import com.muedsa.agetv.model.age.AgeCatalogOption
import com.muedsa.agetv.ui.AgePosterSize
import com.muedsa.agetv.ui.navigation.NavigationItems
import com.muedsa.agetv.viewmodel.CatalogViewModel
import com.muedsa.compose.tv.model.ContentModel
import com.muedsa.compose.tv.theme.CardContentPadding
import com.muedsa.compose.tv.theme.ImageCardRowCardPadding
import com.muedsa.compose.tv.theme.ScreenPaddingLeft
import com.muedsa.compose.tv.widget.CardType
import com.muedsa.compose.tv.widget.ErrorMessageBoxState
import com.muedsa.compose.tv.widget.ImageContentCard
import com.muedsa.compose.tv.widget.ScreenBackgroundState
import com.muedsa.compose.tv.widget.ScreenBackgroundType
import com.muedsa.uitl.LogUtil

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun CatalogScreen(
    viewModel: CatalogViewModel = hiltViewModel(),
    backgroundState: ScreenBackgroundState,
    errorMsgBoxState: ErrorMessageBoxState,
    onNavigate: (NavigationItems, List<String>?) -> Unit = { _, _ -> }
) {
    val fontScale = LocalConfiguration.current.fontScale
    val titleMediumFontSize = MaterialTheme.typography.titleMedium.fontSize.value
    val bodyMediumFontSize = MaterialTheme.typography.bodyMedium.fontSize.value
    val contentHeight = remember {
        (titleMediumFontSize * fontScale + 0.5f).dp +
                (bodyMediumFontSize * fontScale + 0.5f).dp +
                CardContentPadding * 2
    }


    val orderState = remember { viewModel.orderState }
    val regionState = remember { viewModel.regionState }
    val genreState = remember { viewModel.genreState }
    val yearState = remember { viewModel.yearState }
    val seasonState = remember { viewModel.seasonState }
    val statusState = remember { viewModel.statusState }
    val labelState = remember { viewModel.labelState }
    val resourceState = remember { viewModel.resourceState }

    val searchAnimeLPState by remember { viewModel.animeLPState }

    val handleFetchCatalog = remember {
        {
            viewModel.animeLPState.value = LazyPagedList.new()
            viewModel.fetchAnimeCatalog()
        }
    }

    var optionsExpand by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = searchAnimeLPState.type, key2 = searchAnimeLPState.error) {
        if (searchAnimeLPState.type == LazyType.FAILURE) {
            errorMsgBoxState.error(searchAnimeLPState.error)
        }
    }

    BackHandler(enabled = optionsExpand) {
        optionsExpand = false
    }

    Column(modifier = Modifier.padding(start = ScreenPaddingLeft)) {
        Row(
            modifier = Modifier
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
                viewModel.fetchAnimeCatalog()
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
            TvLazyColumn(contentPadding = PaddingValues(top = ImageCardRowCardPadding)) {
                item {
                    CatalogOptionsWidget(
                        "排序",
                        orderState,
                        AgeCatalogOption.Order,
                        handleFetchCatalog
                    )
                }
                item {
                    CatalogOptionsWidget(
                        "区域", regionState, AgeCatalogOption.Regions,
                        handleFetchCatalog
                    )
                }
                item {
                    CatalogOptionsWidget(
                        "类型", genreState, AgeCatalogOption.Genres,
                        handleFetchCatalog
                    )
                }
                item {
                    CatalogOptionsWidget(
                        "年份", yearState, AgeCatalogOption.Years,
                        handleFetchCatalog
                    )
                }
                item {
                    CatalogOptionsWidget(
                        "季度", seasonState, AgeCatalogOption.Seasons,
                        handleFetchCatalog
                    )
                }
                item {
                    CatalogOptionsWidget(
                        "状态", statusState, AgeCatalogOption.Status,
                        handleFetchCatalog
                    )
                }
                item {
                    CatalogOptionsWidget(
                        "标签", labelState, AgeCatalogOption.Labels,
                        handleFetchCatalog
                    )
                }
                item {
                    CatalogOptionsWidget(
                        "资源", resourceState, AgeCatalogOption.Resources,
                        handleFetchCatalog
                    )
                }
            }
        } else {
            if (searchAnimeLPState.list.isNotEmpty()) {
                val gridFocusRequester = remember { FocusRequester() }

                TvLazyVerticalGrid(
                    columns = TvGridCells.Adaptive(AgePosterSize.width + ImageCardRowCardPadding),
                    contentPadding = PaddingValues(
                        top = ImageCardRowCardPadding,
                        bottom = ImageCardRowCardPadding
                    ),
                    modifier = Modifier
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
                        }
                ) {
                    itemsIndexed(
                        items = searchAnimeLPState.list,
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
                                onNavigate(NavigationItems.Detail, listOf(item.id.toString()))
                            }
                        )

                        LaunchedEffect(key1 = Unit) {
                            if (searchAnimeLPState.offset == index) {
                                itemFocusRequester.requestFocus()
                            }
                        }
                    }

                    if (searchAnimeLPState.type != LazyType.LOADING && searchAnimeLPState.hasNext) {
                        item {
                            Column {
                                Card(
                                    modifier = Modifier
                                        .size(AgePosterSize)
                                        .padding(end = ImageCardRowCardPadding),
                                    onClick = {
                                        viewModel.fetchAnimeCatalog()
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
                                Spacer(modifier = Modifier.height(contentHeight))
                            }
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.fetchAnimeCatalog()
    }
}

