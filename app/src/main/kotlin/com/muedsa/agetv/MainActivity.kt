package com.muedsa.agetv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.muedsa.agetv.model.LazyType
import com.muedsa.agetv.ui.navigation.AppNavigation
import com.muedsa.agetv.viewmodel.HomePageViewModel
import com.muedsa.compose.tv.theme.TvTheme
import com.muedsa.compose.tv.widget.Scaffold
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val homePageViewModel: HomePageViewModel by viewModels()

    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition {
            homePageViewModel.homeDataSF.value.type == LazyType.LOADING
        }
        setContent {
            TvTheme {
                Scaffold {
                    AppNavigation(navController = rememberNavController())
                }
            }
        }
    }
}