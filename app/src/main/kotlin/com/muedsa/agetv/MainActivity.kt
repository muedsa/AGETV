package com.muedsa.agetv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import com.muedsa.agetv.model.LazyType
import com.muedsa.agetv.ui.navigation.AppNavigation
import com.muedsa.agetv.viewmodel.HomePageViewModel
import com.muedsa.compose.tv.theme.TvTheme
import com.muedsa.compose.tv.widget.AppBackHandler
import com.muedsa.compose.tv.widget.ErrorMessageBox
import com.muedsa.compose.tv.widget.ErrorMessageBoxState
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
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val errorMsgBoxState = remember { ErrorMessageBoxState() }
                    AppBackHandler {
                        errorMsgBoxState.error("再次点击返回键退出")
                    }
                    ErrorMessageBox(state = errorMsgBoxState) {
                        AppNavigation(
                            navController = rememberNavController(),
                            errorMsgBoxState = errorMsgBoxState
                        )
                    }
                }
            }
        }
    }
}