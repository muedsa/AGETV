package com.muedsa.agetv.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import com.muedsa.agetv.ui.features.detail.AnimeDetailScreen
import com.muedsa.agetv.ui.features.home.HomeNavScreen
import com.muedsa.agetv.ui.features.setting.AppSettingScreen
import com.muedsa.compose.tv.widget.FullWidthDialogProperties
import com.muedsa.compose.tv.widget.LocalRightSideDrawerState
import com.muedsa.compose.tv.widget.NotFoundScreen
import com.muedsa.compose.tv.widget.RightSideDrawerWithNavDrawerContent
import com.muedsa.compose.tv.widget.RightSideDrawerWithNavState

val LocalAppNavController = compositionLocalOf<NavHostController> {
    error("LocalAppNavController not init")
}

@Composable
fun AppNavigation(navController: NavHostController) {

    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    }

    val rightSideDrawerState =
        RightSideDrawerWithNavState(navController, NavigationItems.RightSideDrawer.path)

    CompositionLocalProvider(
        LocalAppNavController provides navController,
        LocalRightSideDrawerState provides rightSideDrawerState
    ) {
        NavHost(
            navController = navController,
            startDestination = buildRoute(NavigationItems.Home, listOf("0"))
        ) {

            composable(
                route = NavigationItems.Home.path,
                arguments = listOf(navArgument("tabIndex") {
                    type = NavType.IntType
                })
            ) {
                HomeNavScreen(
                    tabIndex = checkNotNull(it.arguments?.getInt("tabIndex")),
                    homePageViewModel = hiltViewModel(viewModelStoreOwner),
                )
            }

            composable(
                route = NavigationItems.Detail.path,
                arguments = listOf(navArgument("animeId") {
                    type = NavType.StringType
                })
            ) {
                AnimeDetailScreen()
            }

            dialog(
                route = NavigationItems.Setting.path,
                dialogProperties = FullWidthDialogProperties()
            ) {
                AppSettingScreen()
            }

            composable(NavigationItems.NotFound.path) {
                NotFoundScreen()
            }

            dialog(
                route = NavigationItems.RightSideDrawer.path,
                dialogProperties = FullWidthDialogProperties()
            ) {
                RightSideDrawerWithNavDrawerContent(
                    state = rightSideDrawerState
                )
            }
        }
    }


}

fun buildRoute(
    navItem: NavigationItems,
    pathParams: List<String>?
): String {
    var route = navItem.path
    if (!navItem.pathParams.isNullOrEmpty()) {
        checkNotNull(pathParams)
        check(pathParams.size == navItem.pathParams.size)
        for (i in 0 until navItem.pathParams.size) {
            route = route.replace(navItem.pathParams[i], pathParams[i])
        }
    }
    return route
}

fun NavHostController.navigate(
    navItem: NavigationItems,
    pathParams: List<String>?
) {
    navigate(buildRoute(navItem, pathParams))
}
