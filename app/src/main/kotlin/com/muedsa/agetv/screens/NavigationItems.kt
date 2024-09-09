package com.muedsa.agetv.screens

sealed class NavigationItems(
    val path: String,
    val pathParams: List<String>? = null,
) {
    data object Home : NavigationItems("home/{tabIndex}", listOf("{tabIndex}"))

    data object Detail : NavigationItems("detail/{animeId}", listOf("{animeId}"))

    data object Setting : NavigationItems("setting")

    data object RightSideDrawer : NavigationItems("right_side_drawer")
}
