package com.muedsa.agetv.screens.home

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Tab
import androidx.tv.material3.TabDefaults
import androidx.tv.material3.TabRow
import androidx.tv.material3.TabRowDefaults
import androidx.tv.material3.Text
import com.muedsa.agetv.screens.home.catalog.CatalogScreen
import com.muedsa.agetv.screens.home.favorites.FavoritesScreen
import com.muedsa.agetv.screens.home.latest.LatestUpdateScreen
import com.muedsa.agetv.screens.home.main.MainScreen
import com.muedsa.agetv.screens.home.main.MainScreenViewModel
import com.muedsa.agetv.screens.home.rank.RankScreen
import com.muedsa.agetv.screens.home.recommend.RecommendScreen
import com.muedsa.agetv.screens.home.search.SearchScreen
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomeNavTab(
    tabIndex: Int = 0,
    mainScreenViewModel: MainScreenViewModel
) {
    val backgroundState = useLocalHomeScreenBackgroundState()
    var focusedTabIndex by rememberSaveable { mutableIntStateOf(tabIndex) }
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(focusedTabIndex) }

    var tabPanelIndex by remember { mutableIntStateOf(selectedTabIndex) }

    LaunchedEffect(selectedTabIndex) {
        delay(150.milliseconds)
        tabPanelIndex = selectedTabIndex
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        TabRow(
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
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
            mainScreenViewModel = mainScreenViewModel
        )
    }
}

@Composable
fun HomeContent(
    tabIndex: Int,
    mainScreenViewModel: MainScreenViewModel
) {
    when (tabIndex) {
        0 -> MainScreen(
            viewModel = mainScreenViewModel
        )

        1 -> RankScreen()

        2 -> LatestUpdateScreen()

        3 -> RecommendScreen()

        4 -> SearchScreen()

        5 -> FavoritesScreen()

        6 -> CatalogScreen()
    }
}