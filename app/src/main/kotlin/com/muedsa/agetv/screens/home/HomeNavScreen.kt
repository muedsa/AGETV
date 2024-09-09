package com.muedsa.agetv.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.hilt.navigation.compose.hiltViewModel
import com.muedsa.agetv.screens.home.main.MainScreenViewModel
import com.muedsa.compose.tv.widget.ScreenBackground
import com.muedsa.compose.tv.widget.ScreenBackgroundState
import com.muedsa.compose.tv.widget.rememberScreenBackgroundState

private val LocalHomeScreenBackgroundState = compositionLocalOf<ScreenBackgroundState?> { null }

@Composable
fun LocalHomeScreenBackgroundStateProvider(
    backgroundState: ScreenBackgroundState = rememberScreenBackgroundState(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        value = LocalHomeScreenBackgroundState provides backgroundState,
        content = content
    )
}

@Composable
fun useLocalHomeScreenBackgroundState(): ScreenBackgroundState {
    return LocalHomeScreenBackgroundState.current
        ?: throw RuntimeException("Please wrap your app with LocalHomeScreenBackgroundState")
}

@Composable
fun HomeNavScreen(
    tabIndex: Int = 0,
    mainScreenViewModel: MainScreenViewModel = hiltViewModel(),
) {
    val backgroundState = rememberScreenBackgroundState()
    ScreenBackground(state = backgroundState)
    LocalHomeScreenBackgroundStateProvider(backgroundState) {
        HomeNavTab(
            tabIndex = tabIndex,
            mainScreenViewModel = mainScreenViewModel
        )
    }
}