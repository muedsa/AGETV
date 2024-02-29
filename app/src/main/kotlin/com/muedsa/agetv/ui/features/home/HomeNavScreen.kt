package com.muedsa.agetv.ui.features.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.hilt.navigation.compose.hiltViewModel
import com.muedsa.agetv.ui.navigation.NavigationItems
import com.muedsa.agetv.viewmodel.HomePageViewModel
import com.muedsa.compose.tv.widget.ScreenBackground
import com.muedsa.compose.tv.widget.ScreenBackgroundState
import com.muedsa.compose.tv.widget.rememberScreenBackgroundState

val LocalHomeScreenBackgroundState = compositionLocalOf<ScreenBackgroundState> {
    error("LocalHomeScreenBackgroundState not init")
}

@Composable
fun HomeNavScreen(
    tabIndex: Int = 0,
    homePageViewModel: HomePageViewModel = hiltViewModel(),
    onNavigate: (NavigationItems, List<String>?) -> Unit = { _, _ -> },
) {
    val backgroundState = rememberScreenBackgroundState()
    ScreenBackground(state = backgroundState)
    CompositionLocalProvider(value = LocalHomeScreenBackgroundState provides backgroundState) {
        HomeNavTab(
            tabIndex = tabIndex,
            homePageViewModel = homePageViewModel,
            onNavigate = onNavigate
        )
    }
}