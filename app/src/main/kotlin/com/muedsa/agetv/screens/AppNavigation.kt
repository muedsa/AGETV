package com.muedsa.agetv.screens

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.muedsa.agetv.screens.detail.AnimeDetailScreen
import com.muedsa.agetv.screens.home.HomeNavScreen
import com.muedsa.agetv.screens.setting.AppSettingScreen
import com.muedsa.compose.tv.LocalNavHostControllerProvider
import com.muedsa.compose.tv.LocalRightSideDrawerControllerProvider
import com.muedsa.compose.tv.widget.FullWidthDialogProperties
import com.muedsa.compose.tv.widget.RightSideDrawerWithNavController
import com.muedsa.compose.tv.widget.RightSideDrawerWithNavDrawerContent

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {

    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    }

    val drawerController =
        RightSideDrawerWithNavController(navController, NavigationItems.RightSideDrawer.path)

    LocalNavHostControllerProvider(navController) {
        LocalRightSideDrawerControllerProvider(drawerController) {
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
                        mainScreenViewModel = hiltViewModel(viewModelStoreOwner),
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

                dialog(
                    route = NavigationItems.RightSideDrawer.path,
                    dialogProperties = FullWidthDialogProperties()
                ) {
                    RightSideDrawerWithNavDrawerContent(controller = drawerController)
                }
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

fun NavHostController.nav(
    navItem: NavigationItems,
    pathParams: List<String>? = null
) {
    navigate(buildRoute(navItem, pathParams))
}
