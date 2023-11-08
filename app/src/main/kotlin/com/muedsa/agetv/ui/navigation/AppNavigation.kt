package com.muedsa.agetv.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.muedsa.agetv.ui.features.detail.AnimeDetailScreen
import com.muedsa.agetv.ui.features.home.HomeNavScreen
import com.muedsa.agetv.ui.features.setting.AppSettingScreen
import com.muedsa.compose.tv.widget.ErrorMessageBoxState
import com.muedsa.compose.tv.widget.NotFoundScreen

@Composable
fun AppNavigation(navController: NavHostController, errorMsgBoxState: ErrorMessageBoxState) {

    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    }

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
                errorMsgBoxState = errorMsgBoxState,
                onNavigate = { navItem, pathParams ->
                    onNavigate(navController, navItem, pathParams)
                }
            )
        }

        composable(
            route = NavigationItems.Detail.path,
            arguments = listOf(navArgument("animeId") {
                type = NavType.StringType
            })
        ) {
            AnimeDetailScreen(
                errorMsgBoxState = errorMsgBoxState,
                onNavigate = { navItem, pathParams ->
                    onNavigate(navController, navItem, pathParams)
                }
            )
        }

        composable(
            route = NavigationItems.Setting.path
        ) {
            AppSettingScreen(
                errorMsgBoxState = errorMsgBoxState
            )
        }

        composable(NavigationItems.NotFound.path) {
            NotFoundScreen()
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

fun onNavigate(
    navController: NavHostController,
    navItem: NavigationItems,
    pathParams: List<String>?
) {
    navController.navigate(buildRoute(navItem, pathParams))
}
