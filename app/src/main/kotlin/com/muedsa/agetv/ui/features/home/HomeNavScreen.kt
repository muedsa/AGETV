package com.muedsa.agetv.ui.features.home

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.muedsa.agetv.ui.navigation.NavigationItems
import com.muedsa.agetv.viewmodel.HomePageViewModel
import com.muedsa.compose.tv.widget.ErrorMessageBoxState
import com.muedsa.compose.tv.widget.ScreenBackground
import com.muedsa.compose.tv.widget.rememberScreenBackgroundState

@Composable
fun HomeNavScreen(
    tabIndex: Int = 0,
    homePageViewModel: HomePageViewModel = hiltViewModel(),
    errorMsgBoxState: ErrorMessageBoxState,
    onNavigate: (NavigationItems, List<String>?) -> Unit = { _, _ -> },
) {
    val backgroundState = rememberScreenBackgroundState()
    ScreenBackground(state = backgroundState)
    HomeNavTab(
        tabIndex = tabIndex,
        homePageViewModel = homePageViewModel,
        backgroundState = backgroundState,
        errorMsgBoxState = errorMsgBoxState,
        onNavigate = onNavigate
    )
}