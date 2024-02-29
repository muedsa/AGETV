package com.muedsa.agetv.ui.features.home.search

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Card
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.OutlinedIconButton
import androidx.tv.material3.Text
import com.muedsa.agetv.model.LazyPagedList
import com.muedsa.agetv.model.LazyType
import com.muedsa.agetv.ui.AgePosterSize
import com.muedsa.agetv.ui.GirdLastItemHeight
import com.muedsa.agetv.ui.navigation.NavigationItems
import com.muedsa.agetv.viewmodel.SearchViewModel
import com.muedsa.compose.tv.model.ContentModel
import com.muedsa.compose.tv.theme.ImageCardRowCardPadding
import com.muedsa.compose.tv.theme.ScreenPaddingLeft
import com.muedsa.compose.tv.theme.outline
import com.muedsa.compose.tv.widget.CardType
import com.muedsa.compose.tv.widget.ImageContentCard
import com.muedsa.compose.tv.widget.LocalErrorMsgBoxState
import com.muedsa.compose.tv.widget.ScreenBackgroundState
import com.muedsa.compose.tv.widget.ScreenBackgroundType
import com.muedsa.uitl.LogUtil

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    backgroundState: ScreenBackgroundState,
    onNavigate: (NavigationItems, List<String>?) -> Unit = { _, _ -> }
) {
    val errorMsgBoxState = LocalErrorMsgBoxState.current

    val searchText by viewModel.searchTextSF.collectAsState()
    val searchAnimeLP by viewModel.searchAnimeLPSF.collectAsState()

    LaunchedEffect(key1 = searchAnimeLP) {
        if (searchAnimeLP.type == LazyType.FAILURE) {
            errorMsgBoxState.error(searchAnimeLP.error)
        }
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
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .background(
                        color = MaterialTheme.colorScheme.inverseOnSurface,
                        shape = OutlinedTextFieldDefaults.shape
                    ),
                textStyle = MaterialTheme.typography.bodyLarge,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.outline,
                    cursorColor = MaterialTheme.colorScheme.onSurface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                value = searchText,
                onValueChange = {
                    viewModel.searchTextSF.value = it
                },
                singleLine = true
            )
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedIconButton(onClick = {
                viewModel.searchAnime(LazyPagedList.new(searchText))
            }) {
                Icon(
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "搜索"
                )
            }
        }

        if (searchAnimeLP.list.isNotEmpty()) {

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
                            title = item.name,
                            subtitle = item.tags
                        ),
                        onItemFocus = {
                            backgroundState.url = item.cover
                            backgroundState.type = ScreenBackgroundType.BLUR
                        },
                        onItemClick = {
                            LogUtil.fb("Click $item")
                            onNavigate(NavigationItems.Detail, listOf(item.id.toString()))
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
                                viewModel.searchAnime(searchAnimeLP)
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

