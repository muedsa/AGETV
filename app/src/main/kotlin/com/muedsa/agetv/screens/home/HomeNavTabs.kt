package com.muedsa.agetv.screens.home

sealed class HomeNavTabs(val title: String) {
    data object Main : HomeNavTabs("首页")
    data object Rank : HomeNavTabs("排行")
    data object Latest : HomeNavTabs("更新")
    data object Recommend : HomeNavTabs("推荐")
    data object Search : HomeNavTabs("搜索")
    data object Favorites : HomeNavTabs("收藏")
    data object Catalog : HomeNavTabs("目录")
}