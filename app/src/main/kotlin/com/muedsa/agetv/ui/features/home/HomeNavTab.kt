package com.muedsa.agetv.ui.features.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Tab
import androidx.tv.material3.TabDefaults
import androidx.tv.material3.TabRow
import androidx.tv.material3.TabRowDefaults
import androidx.tv.material3.Text
import com.muedsa.agetv.ui.features.home.catalog.CatalogScreen
import com.muedsa.agetv.ui.features.home.favorites.FavoritesScreen
import com.muedsa.agetv.ui.features.home.latest.LatestUpdateScreen
import com.muedsa.agetv.ui.features.home.main.MainScreen
import com.muedsa.agetv.ui.features.home.rank.RankScreen
import com.muedsa.agetv.ui.features.home.recommend.RecommendScreen
import com.muedsa.agetv.ui.features.home.search.SearchScreen
import com.muedsa.agetv.ui.navigation.NavigationItems
import com.muedsa.agetv.viewmodel.HomePageViewModel
import com.muedsa.compose.tv.widget.ErrorMessageBoxState
import com.muedsa.compose.tv.widget.NotFoundScreen
import com.muedsa.compose.tv.widget.ScreenBackgroundState
import com.muedsa.compose.tv.widget.ScreenBackgroundType
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

val tabs = listOf(
    HomeNavTabs.Main,
    HomeNavTabs.Rank,
    HomeNavTabs.Latest,
    HomeNavTabs.Recommend,
    HomeNavTabs.Search,
    HomeNavTabs.Favorites,
    HomeNavTabs.Catalog
)

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun HomeNavTab(
    tabIndex: Int = 0,
    homePageViewModel: HomePageViewModel,
    backgroundState: ScreenBackgroundState = ScreenBackgroundState(),
    errorMsgBoxState: ErrorMessageBoxState,
    onNavigate: (NavigationItems, List<String>?) -> Unit = { _, _ -> },
) {
    var focusedTabIndex by rememberSaveable { mutableIntStateOf(tabIndex) }
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(focusedTabIndex) }

    var tabPanelIndex by remember { mutableIntStateOf(selectedTabIndex) }

    LaunchedEffect(selectedTabIndex) {
        delay(150.milliseconds)
        tabPanelIndex = selectedTabIndex
    }

    Column {
        TabRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, bottom = 24.dp)
                .focusRestorer(),
            selectedTabIndex = selectedTabIndex,
            indicator = { tabPositions, doesTabRowHaveFocus ->
                // FocusedTab's indicator
                TabRowDefaults.PillIndicator(
                    currentTabPosition = tabPositions[focusedTabIndex],
                    doesTabRowHaveFocus = doesTabRowHaveFocus,
                    activeColor = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.4f),
                    inactiveColor = Color.Transparent
                )

                // SelectedTab's indicator
                TabRowDefaults.PillIndicator(
                    currentTabPosition = tabPositions[selectedTabIndex],
                    doesTabRowHaveFocus = doesTabRowHaveFocus
                )
            }
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTabIndex == index,
                    onFocus = { focusedTabIndex = index },
                    onClick = {
                        if (selectedTabIndex != index) {
                            backgroundState.url = null
                            backgroundState.type = ScreenBackgroundType.BLUR
                            selectedTabIndex = index
                        }
                    },
                    colors = TabDefaults.pillIndicatorTabColors(
                        selectedContentColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(
                        tab.title,
                        fontSize = MaterialTheme.typography.labelLarge.fontSize,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }
        HomeContent(
            tabIndex = tabPanelIndex,
            homePageViewModel = homePageViewModel,
            backgroundState = backgroundState,
            errorMsgBoxState = errorMsgBoxState,
            onNavigate = onNavigate
        )
    }
}

@Composable
fun HomeContent(
    tabIndex: Int,
    homePageViewModel: HomePageViewModel,
    backgroundState: ScreenBackgroundState,
    errorMsgBoxState: ErrorMessageBoxState,
    onNavigate: (NavigationItems, List<String>?) -> Unit = { _, _ -> },
) {
    when (tabIndex) {
        0 -> MainScreen(
            viewModel = homePageViewModel,
            backgroundState = backgroundState,
            errorMsgBoxState = errorMsgBoxState,
            onNavigate = onNavigate
        )

        1 -> RankScreen(
            errorMsgBoxState = errorMsgBoxState,
            onNavigate = onNavigate
        )

        2 -> LatestUpdateScreen(
            backgroundState = backgroundState,
            errorMsgBoxState = errorMsgBoxState,
            onNavigate = onNavigate
        )

        3 -> RecommendScreen(
            backgroundState = backgroundState,
            errorMsgBoxState = errorMsgBoxState,
            onNavigate = onNavigate
        )

        4 -> SearchScreen(
            backgroundState = backgroundState,
            errorMsgBoxState = errorMsgBoxState,
            onNavigate = onNavigate
        )

        5 -> FavoritesScreen(
            backgroundState = backgroundState,
            onNavigate = onNavigate
        )

        6 -> CatalogScreen(
            backgroundState = backgroundState,
            errorMsgBoxState = errorMsgBoxState,
            onNavigate = onNavigate
        )

        else -> NotFoundScreen()
    }
}