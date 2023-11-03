package com.muedsa.agetv.ui.features.home

sealed class HomeNavTabs(val title: String) {
    data object Main : HomeNavTabs("首页")

    data object Rank : HomeNavTabs("排行")

    data object Search : HomeNavTabs("搜索")
    data object Catalog : HomeNavTabs("目录")
}