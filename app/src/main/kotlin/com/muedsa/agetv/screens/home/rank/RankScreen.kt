package com.muedsa.agetv.screens.home.rank

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.OutlinedIconButton
import androidx.tv.material3.RadioButton
import androidx.tv.material3.Text
import androidx.tv.material3.WideButtonDefaults
import com.muedsa.agetv.model.LazyType
import com.muedsa.agetv.model.age.AgeCatalogOption
import com.muedsa.agetv.screens.NavigationItems
import com.muedsa.agetv.screens.nav
import com.muedsa.compose.tv.theme.ScreenPaddingLeft
import com.muedsa.compose.tv.useLocalNavHostController
import com.muedsa.compose.tv.useLocalRightSideDrawerController
import com.muedsa.compose.tv.useLocalToastMsgBoxController
import com.muedsa.compose.tv.widget.ErrorScreen
import com.muedsa.compose.tv.widget.LoadingScreen
import com.muedsa.compose.tv.widget.NoBackground
import com.muedsa.compose.tv.widget.TwoSideWideButton
import com.muedsa.uitl.LogUtil
import kotlinx.coroutines.flow.update

@Composable
fun RankScreen(
    viewModel: RankViewModel = hiltViewModel()
) {
    val toastController = useLocalToastMsgBoxController()
    val rightSideDrawerState = useLocalRightSideDrawerController()
    val navController = useLocalNavHostController()

    val selectYear by viewModel.selectedYearSF.collectAsState()
    val rankLD by viewModel.rankLDSF.collectAsState()

    LaunchedEffect(key1 = rankLD) {
        if (rankLD.type == LazyType.FAILURE) {
            toastController.error(rankLD.error)
        }
    }

    Column(modifier = Modifier.padding(start = ScreenPaddingLeft)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "首播年份: ${selectYear.text}",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedIconButton(onClick = {
                rightSideDrawerState.pop {
                    Column {
                        Text(
                            modifier = Modifier
                                .padding(start = 8.dp, end = 15.dp),
                            text = "年份",
                            style = MaterialTheme.typography.titleLarge
                        )
                        LazyColumn(
                            contentPadding = PaddingValues(vertical = 20.dp)
                        ) {
                            items(items = AgeCatalogOption.Years) {
                                val interactionSource = remember { MutableInteractionSource() }
                                TwoSideWideButton(
                                    title = { Text(text = it.text) },
                                    onClick = {
                                        rightSideDrawerState.close()
                                        viewModel.selectedYearSF.update { it }
                                    },
                                    interactionSource = interactionSource,
                                    background = {
                                        WideButtonDefaults.NoBackground(
                                            interactionSource = interactionSource
                                        )
                                    }
                                ) {
                                    RadioButton(
                                        selected = selectYear == it,
                                        onClick = { },
                                        interactionSource = interactionSource
                                    )
                                }
                            }
                        }
                    }
                }
            }) {
                Icon(imageVector = Icons.Outlined.MoreVert, contentDescription = "修改首播年份")
            }
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
                    LazyColumn(modifier = Modifier.testTag("rankScreen_col_week")) {
                        items(dayList) {
                            RankAnimeWidget(
                                model = it,
                                onClick = {
                                    LogUtil.d("Click $it")
                                    navController.nav(
                                        NavigationItems.Detail,
                                        listOf(it.aid.toString())
                                    )
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
                    LazyColumn(modifier = Modifier.testTag("rankScreen_col_month")) {
                        items(weekList) {
                            RankAnimeWidget(
                                model = it,
                                onClick = {
                                    LogUtil.d("Click $it")
                                    navController.nav(
                                        NavigationItems.Detail,
                                        listOf(it.aid.toString())
                                    )
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

                    LazyColumn(modifier = Modifier.testTag("rankScreen_col_all")) {
                        items(totalList) {
                            RankAnimeWidget(
                                model = it,
                                onClick = {
                                    LogUtil.d("Click $it")
                                    navController.nav(
                                        NavigationItems.Detail,
                                        listOf(it.aid.toString())
                                    )
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
                viewModel.selectedYearSF.value = selectYear
            }
        }
    }
}