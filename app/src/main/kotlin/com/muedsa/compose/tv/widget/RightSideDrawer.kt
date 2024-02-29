package com.muedsa.compose.tv.widget

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.tv.material3.DrawerState
import androidx.tv.material3.DrawerValue
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.ModalNavigationDrawer
import androidx.tv.material3.NonInteractiveSurfaceDefaults
import androidx.tv.material3.Surface
import com.muedsa.compose.tv.theme.surfaceContainer

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun RightSideDrawer(
    state: RightSideDrawerState = RightSideDrawerState(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerContent = {
                BackHandler(enabled = it == DrawerValue.Open) {
                    state.close()
                }
                if (it == DrawerValue.Open) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(all = 24.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxHeight(),
                            colors = NonInteractiveSurfaceDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                Box(
                                    Modifier
                                        .fillMaxHeight()
                                        .padding(all = 20.dp)) {
                                    state.ContentCompose()
                                }
                            }
                        }
                    }
                }
            },
            drawerState = state.drawerState,
            content = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Surface(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        content()
                    }
                }
            }
        )
    }
}


@OptIn(ExperimentalTvMaterial3Api::class)
class RightSideDrawerState {
    private val contentState: MutableState<@Composable () -> Unit> = mutableStateOf({})
    val drawerState: DrawerState = DrawerState(DrawerValue.Closed)

    @Composable
    fun ContentCompose() {
        contentState.value.invoke()
    }

    fun pop(content: @Composable () -> Unit) {
        contentState.value = content
        drawerState.setValue(DrawerValue.Open)
    }

    fun close() {
        drawerState.setValue(DrawerValue.Closed)
    }
}