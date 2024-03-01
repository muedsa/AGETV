package com.muedsa.agetv.ui.navigation

sealed class NavigationItems(
    val path: String,
    val pathParams: List<String>? = null,
) {
    data object Home : NavigationItems("home/{tabIndex}", listOf("{tabIndex}"))

    data object Detail : NavigationItems("detail/{animeId}", listOf("{animeId}"))

    data object Setting : NavigationItems("setting")

    data object NotFound : NavigationItems("not_found")

    data object RightSideDrawer : NavigationItems("right_side_drawer")
}
