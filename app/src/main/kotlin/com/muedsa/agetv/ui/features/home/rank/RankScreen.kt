package com.muedsa.agetv.ui.features.home.rank

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.muedsa.agetv.model.LazyType
import com.muedsa.agetv.model.age.AgeCatalogOption
import com.muedsa.agetv.ui.navigation.NavigationItems
import com.muedsa.agetv.viewmodel.RankViewModel
import com.muedsa.compose.tv.theme.ScreenPaddingLeft
import com.muedsa.compose.tv.widget.ErrorMessageBoxState
import com.muedsa.compose.tv.widget.ErrorScreen
import com.muedsa.compose.tv.widget.ExposedDropdownMenuButton
import com.muedsa.compose.tv.widget.LoadingScreen
import com.muedsa.uitl.LogUtil

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun RankScreen(
    viewModel: RankViewModel = hiltViewModel(),
    errorMsgBoxState: ErrorMessageBoxState,
    onNavigate: (NavigationItems, List<String>?) -> Unit = { _, _ -> }
) {

    var selectYear by remember { viewModel.selectedYear }
    val rankLD by remember { viewModel.rankLDState }

    LaunchedEffect(key1 = rankLD) {
        if (rankLD.type == LazyType.FAILURE) {
            errorMsgBoxState.error(rankLD.error)
        }
    }

    Column(modifier = Modifier.padding(start = ScreenPaddingLeft)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "首播年份",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.width(8.dp))
            ExposedDropdownMenuButton(
                selectedIndexState = remember {
                    mutableIntStateOf(AgeCatalogOption.Years.indexOf(selectYear))
                },
                itemList = AgeCatalogOption.Years,
                textFn = { _, item ->
                    item.text
                },
                onSelected = { _, item ->
                    selectYear = item
                    viewModel.fetchRank()
                }
            )
        }
        if (rankLD.type == LazyType.SUCCESS && !rankLD.data.isNullOrEmpty() && rankLD.data!!.size > 2) {
            val dayList = rankLD.data!![0]
            val weekList = rankLD.data!![1]
            val totalList = rankLD.data!![2]
            Row {
                Column(
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 10.dp, end = 10.dp)
                        .weight(1f)
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "周榜",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleLarge
                    )
                    TvLazyColumn {
                        items(dayList) {
                            RankAnimeWidget(
                                model = it,
                                onClick = {
                                    LogUtil.d("Click $it")
                                    onNavigate(NavigationItems.Detail, listOf(it.aid.toString()))
                                }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(100.dp))
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .weight(1f)
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "月榜",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleLarge
                    )
                    TvLazyColumn {
                        items(weekList) {
                            RankAnimeWidget(
                                model = it,
                                onClick = {
                                    LogUtil.d("Click $it")
                                    onNavigate(NavigationItems.Detail, listOf(it.aid.toString()))
                                }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(100.dp))
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .weight(1f)
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "总榜",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleLarge
                    )

                    TvLazyColumn {
                        items(totalList) {
                            RankAnimeWidget(
                                model = it,
                                onClick = {
                                    LogUtil.d("Click $it")
                                    onNavigate(NavigationItems.Detail, listOf(it.aid.toString()))
                                }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(100.dp))
                        }
                    }
                }
            }
        } else if (rankLD.type == LazyType.LOADING) {
            LoadingScreen()
        } else {
            ErrorScreen {
                viewModel.fetchRank()
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.fetchRank()
    }
}