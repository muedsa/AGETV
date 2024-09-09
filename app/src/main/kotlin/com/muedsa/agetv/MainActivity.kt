package com.muedsa.agetv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.muedsa.agetv.model.LazyType
import com.muedsa.agetv.screens.AppNavigation
import com.muedsa.agetv.screens.home.main.MainScreenViewModel
import com.muedsa.compose.tv.theme.TvTheme
import com.muedsa.compose.tv.widget.Scaffold
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainScreenViewModel: MainScreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition {
            mainScreenViewModel.homeDataSF.value.type == LazyType.LOADING
        }
        setContent {
            TvTheme {
                Scaffold {
                    AppNavigation()
                }
            }
        }
    }
}