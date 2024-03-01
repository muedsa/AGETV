package com.muedsa.agetv.ui.features.detail

import android.content.Intent
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.items
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.OutlinedButton
import androidx.tv.material3.OutlinedIconButton
import androidx.tv.material3.RadioButton
import androidx.tv.material3.Text
import androidx.tv.material3.WideButtonDefaults
import com.muedsa.agetv.PlaybackActivity
import com.muedsa.agetv.model.LazyType
import com.muedsa.agetv.room.model.FavoriteAnimeModel
import com.muedsa.agetv.ui.AgePosterSize
import com.muedsa.agetv.ui.FavoriteIconColor
import com.muedsa.agetv.ui.RankFontColor
import com.muedsa.agetv.ui.RankIconColor
import com.muedsa.agetv.ui.navigation.NavigationItems
import com.muedsa.agetv.viewmodel.AnimeDetailViewModel
import com.muedsa.compose.tv.model.ContentModel
import com.muedsa.compose.tv.theme.ScreenPaddingLeft
import com.muedsa.compose.tv.widget.ContentBlock
import com.muedsa.compose.tv.widget.ContentBlockType
import com.muedsa.compose.tv.widget.EmptyDataScreen
import com.muedsa.compose.tv.widget.ErrorScreen
import com.muedsa.compose.tv.widget.LoadingScreen
import com.muedsa.compose.tv.widget.LocalErrorMsgBoxState
import com.muedsa.compose.tv.widget.LocalRightSideDrawerState
import com.muedsa.compose.tv.widget.NoBackground
import com.muedsa.compose.tv.widget.ScreenBackground
import com.muedsa.compose.tv.widget.ScreenBackgroundType
import com.muedsa.compose.tv.widget.StandardImageCardsRow
import com.muedsa.compose.tv.widget.TwoSideWideButton
import com.muedsa.compose.tv.widget.rememberScreenBackgroundState
import com.muedsa.uitl.LogUtil
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AnimeDetailScreen(
    viewModel: AnimeDetailViewModel = hiltViewModel(),
    onNavigate: (NavigationItems, List<String>?) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    val lifecycleOwner = LocalLifecycleOwner.current
    val errorMsgBoxState = LocalErrorMsgBoxState.current
    val rightSideDrawerState = LocalRightSideDrawerState.current

    val animeDetailLD by viewModel.animeDetailLDSF.collectAsState()
    val favoriteModel by viewModel.favoriteModelSF.collectAsState()
    val watchedEpisodeTitleMap by viewModel.watchedEpisodeTitleMapSF.collectAsState()
    val danSearchAnimeListLD by viewModel.danSearchAnimeListLDSF.collectAsState()
    val danAnimeInfoLD by viewModel.danAnimeInfoLDSF.collectAsState()

    val episodeRelationMap = remember { mutableStateMapOf<String, Long>() }

    val backgroundState = rememberScreenBackgroundState(
        initType = ScreenBackgroundType.SCRIM
    )

    LaunchedEffect(key1 = animeDetailLD) {
        if (animeDetailLD.type == LazyType.FAILURE) {
            errorMsgBoxState.error(animeDetailLD.error)
        } else if (animeDetailLD.type == LazyType.SUCCESS) {
            if (animeDetailLD.data != null) {
                backgroundState.url = animeDetailLD.data!!.video.cover
            }
        }
    }

    LaunchedEffect(key1 = danSearchAnimeListLD) {
        if (danSearchAnimeListLD.type == LazyType.FAILURE) {
            errorMsgBoxState.error(danSearchAnimeListLD.error)
        }
    }

    LaunchedEffect(key1 = danAnimeInfoLD) {
        if (danAnimeInfoLD.type == LazyType.FAILURE) {
            errorMsgBoxState.error(danAnimeInfoLD.error)
        }
    }

    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshWatchedEpisodeTitleSet()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    ScreenBackground(backgroundState)

    if (animeDetailLD.type == LazyType.SUCCESS) {
        if (animeDetailLD.data != null) {
            val animeDetail = animeDetailLD.data!!
            val info = animeDetail.video

            var selectedPlaySource by remember { mutableStateOf(info.playLists.keys.first()) }
            var selectedPlaySourceList by remember { mutableStateOf(info.playLists[selectedPlaySource]!!) }

            var enabledDanmaku by remember { mutableStateOf(true) }

            LaunchedEffect(key1 = animeDetailLD) {
                if (selectedPlaySource != info.playLists.keys.first()) {
                    selectedPlaySource = info.playLists.keys.first()
                    selectedPlaySourceList = info.playLists[selectedPlaySource]!!
                    LogUtil.d("update selected source")
                }
            }

            TvLazyColumn(
                modifier = Modifier
                    .padding(start = ScreenPaddingLeft),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // top space
                item {
                    // 占位锚点 使之可以通过Dpad返回页面的顶部
                    Spacer(modifier = Modifier.focusable())
                }

                // 介绍
                item {
                    ContentBlock(
                        modifier = Modifier
                            .padding(top = screenHeight * 0.2f)
                            .width(screenWidth * 0.60f),
                        model = ContentModel(
                            title = info.name,
                            subtitle = "${info.area} | ${info.type} | ${info.writer} | ${info.company}",
                            description = StringBuilder().also {
                                it.append("原版名称：${info.nameOriginal}\n")
                                it.append("其他名称：${info.nameOther}\n")
                                it.append("首播时间：${info.premiere}\n")
                                it.append("播放状态：${info.status}\n")
                                it.append("剧情类型：${info.tags}\n")
                                it.append("简介：${info.introClean}\n")
                            }.toString()
                        ),
                        type = ContentBlockType.CAROUSEL,
                        verticalArrangement = Arrangement.Top,
                        descriptionMaxLines = 10
                    )
                    Spacer(modifier = Modifier.height(25.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Whatshot,
                            contentDescription = "热度",
                            tint = RankIconColor
                        )
                        Text(
                            modifier = Modifier
                                .width(70.dp)
                                .padding(start = 8.dp, end = 15.dp),
                            text = info.rankCnt,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.labelLarge
                        )
                        Icon(
                            imageVector = Icons.Filled.Comment,
                            contentDescription = "评论",
                            tint = RankIconColor
                        )
                        Text(
                            modifier = Modifier
                                .width(70.dp)
                                .padding(start = 8.dp, end = 15.dp),
                            text = info.commentCnt,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.labelLarge
                        )
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "收藏",
                            tint = RankIconColor
                        )
                        Text(
                            modifier = Modifier
                                .width(70.dp)
                                .padding(start = 8.dp, end = 15.dp),
                            text = info.collectCnt,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.labelLarge
                        )
                        if (danAnimeInfoLD.type == LazyType.SUCCESS && danAnimeInfoLD.data != null) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "评分",
                                tint = RankIconColor
                            )
                            Text(
                                modifier = Modifier
                                    .width(70.dp)
                                    .padding(start = 8.dp, end = 15.dp),
                                text = "${danAnimeInfoLD.data!!.rating}",
                                color = RankFontColor,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(25.dp))
                }

                // 按钮列表
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // 切换播放源
                        val currentSourceName = animeDetail.playerLabelArr.getOrDefault(
                            selectedPlaySource,
                            "Unknown"
                        ) + if (animeDetail.isVip(selectedPlaySource)) "*" else " "
                        Text(
                            text = "播放源: $currentSourceName",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedIconButton(onClick = {
                            rightSideDrawerState.pop {
                                Column {
                                    Text(
                                        modifier = Modifier
                                            .padding(start = 8.dp, end = 15.dp),
                                        text = "弹幕剧集",
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    TvLazyColumn(
                                        contentPadding = PaddingValues(vertical = 20.dp)
                                    ) {
                                        items(items = info.playLists.keys.toList()) {
                                            val interactionSource =
                                                remember { MutableInteractionSource() }
                                            TwoSideWideButton(
                                                title = {
                                                    Text(
                                                        text = animeDetail.playerLabelArr.getOrDefault(
                                                            it,
                                                            "Unknown"
                                                        ) + if (animeDetail.isVip(it)) "*" else " "
                                                    )
                                                },
                                                onClick = {
                                                    rightSideDrawerState.close()
                                                    selectedPlaySource = it
                                                    selectedPlaySourceList = info.playLists[it]!!
                                                },
                                                interactionSource = interactionSource,
                                                background = {
                                                    WideButtonDefaults.NoBackground(
                                                        interactionSource = interactionSource
                                                    )
                                                }
                                            ) {
                                                RadioButton(
                                                    selected = selectedPlaySource == it,
                                                    onClick = { },
                                                    interactionSource = interactionSource
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = "修改播放源"
                            )
                        }

                        // 收藏按钮
                        Spacer(modifier = Modifier.width(25.dp))
                        OutlinedButton(onClick = {
                            if (favoriteModel == null) {
                                viewModel.favorite(
                                    model = FavoriteAnimeModel(
                                        id = animeDetail.video.id,
                                        name = animeDetail.video.name,
                                        cover = animeDetail.video.cover,
                                        updateAt = System.currentTimeMillis()
                                    ),
                                    favorite = true
                                )
                            } else {
                                viewModel.favorite(model = favoriteModel!!, favorite = false)
                            }
                        }) {
                            Text(text = if (favoriteModel == null) "追番" else "已追")
                            Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                            Icon(
                                imageVector = Icons.Outlined.Favorite,
                                contentDescription = "收藏",
                                tint = if (favoriteModel == null) LocalContentColor.current else FavoriteIconColor
                            )
                        }

                        // 切换弹弹Play匹配剧集
                        if (danAnimeInfoLD.type == LazyType.SUCCESS && danAnimeInfoLD.data != null) {
                            val danAnimeInfo = danAnimeInfoLD.data
                            Spacer(modifier = Modifier.width(25.dp))
                            Text(
                                text = "弹弹Play匹配剧集: ${if (enabledDanmaku) danAnimeInfo?.animeTitle ?: "--" else "关闭"}",
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        if (danSearchAnimeListLD.type == LazyType.SUCCESS && !danSearchAnimeListLD.data.isNullOrEmpty()) {
                            val danSearchAnimeList = danSearchAnimeListLD.data!!
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedIconButton(onClick = {
                                rightSideDrawerState.pop {
                                    Column {
                                        Text(
                                            modifier = Modifier
                                                .padding(start = 8.dp, end = 15.dp),
                                            text = "弹幕剧集",
                                            style = MaterialTheme.typography.titleLarge
                                        )
                                        TvLazyColumn(
                                            contentPadding = PaddingValues(vertical = 20.dp)
                                        ) {
                                            item {
                                                val interactionSource =
                                                    remember { MutableInteractionSource() }
                                                TwoSideWideButton(
                                                    title = { Text("关闭弹幕") },
                                                    onClick = {
                                                        rightSideDrawerState.close()
                                                        enabledDanmaku = false
                                                    },
                                                    interactionSource = interactionSource,
                                                    background = {
                                                        WideButtonDefaults.NoBackground(
                                                            interactionSource = interactionSource
                                                        )
                                                    }
                                                ) {
                                                    RadioButton(
                                                        selected = !enabledDanmaku,
                                                        onClick = { },
                                                        interactionSource = interactionSource
                                                    )
                                                }
                                            }
                                            items(items = danSearchAnimeList) {
                                                val interactionSource =
                                                    remember { MutableInteractionSource() }
                                                TwoSideWideButton(
                                                    title = { Text(it.animeTitle) },
                                                    subtitle = { Text(it.startDate) },
                                                    onClick = {
                                                        rightSideDrawerState.close()
                                                        enabledDanmaku = true
                                                        viewModel.danBangumi(it.animeId)
                                                    },
                                                    interactionSource = interactionSource,
                                                    background = {
                                                        WideButtonDefaults.NoBackground(
                                                            interactionSource = interactionSource
                                                        )
                                                    }
                                                ) {
                                                    RadioButton(
                                                        selected = enabledDanmaku
                                                                && danAnimeInfoLD.data?.animeId == it.animeId,
                                                        onClick = { },
                                                        interactionSource = interactionSource
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Outlined.Edit,
                                    contentDescription = "修改弹弹Play匹配剧集"
                                )
                            }
                        }

                        // 设置按钮
                        Spacer(modifier = Modifier.width(25.dp))
                        OutlinedButton(
                            onClick = {
                                onNavigate(NavigationItems.Setting, null)
                            }
                        ) {
                            Text(text = "设置")
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                // 剧集列表
                item {
                    EpisodeListWidget(
                        episodeList = selectedPlaySourceList,
                        danEpisodeList = danAnimeInfoLD.data?.episodes ?: emptyList(),
                        episodeProgressMap = watchedEpisodeTitleMap,
                        episodeRelationMap = episodeRelationMap,
                        onEpisodeClick = { episode, danEpisode ->
                            LogUtil.fb("click play: $episode")
                            val url = if (animeDetail.isVip(selectedPlaySource)) {
                                "${animeDetail.playerJx["vip"]}${episode[1]}"
                            } else {
                                "${animeDetail.playerJx["zj"]}${episode[1]}"
                            }
                            LogUtil.fb("click playPage: $url")
                            viewModel.parsePlayInfo(
                                url = url,
                                onSuccess = {
                                    LogUtil.fb("try play => $it")
                                    val intent =
                                        Intent(context, PlaybackActivity::class.java)
                                    intent.putExtra(
                                        PlaybackActivity.AID_KEY,
                                        animeDetail.video.id
                                    )
                                    intent.putExtra(
                                        PlaybackActivity.EPISODE_TITLE_KEY,
                                        episode[0]
                                    )
                                    intent.putExtra(
                                        PlaybackActivity.MEDIA_URL_KEY,
                                        it.realUrl
                                    )
                                    if (enabledDanmaku && danEpisode != null) {
                                        intent.putExtra(
                                            PlaybackActivity.DAN_EPISODE_ID_KEY,
                                            danEpisode.episodeId
                                        )
                                    }
                                    context.startActivity(intent)
                                },
                                onError = {
                                    errorMsgBoxState.error(it)
                                }
                            )
                        },
                        onChangeEpisodeRelation = {
                            it.forEach { pair ->
                                episodeRelationMap[pair.first] = pair.second.episodeId
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(25.dp))
                }

                // 相关动画
                if (animeDetail.series.isNotEmpty()) {
                    item {
                        StandardImageCardsRow(
                            title = "相关动画",
                            modelList = animeDetail.series,
                            imageFn = { _, item ->
                                item.picSmall
                            },
                            imageSize = AgePosterSize,
                            contentFn = { _, item ->
                                ContentModel(item.title, subtitle = item.newTitle)
                            },
                            onItemClick = { _, anime ->
                                LogUtil.fb("Click $anime")
                                viewModel.animeIdSF.update {
                                    anime.aid.toString()
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(25.dp))
                    }
                }

                // 相关推荐
                if (animeDetail.similar.isNotEmpty()) {
                    item {
                        StandardImageCardsRow(
                            title = "相关推荐",
                            modelList = animeDetail.similar,
                            imageFn = { _, item ->
                                item.picSmall
                            },
                            imageSize = AgePosterSize,
                            contentFn = { _, item ->
                                ContentModel(item.title, subtitle = item.newTitle)
                            },
                            onItemClick = { _, anime ->
                                LogUtil.fb("Click $anime")
                                viewModel.animeIdSF.update {
                                    anime.aid.toString()
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(25.dp))
                    }
                }

                // TODO 评论 (评论质量不高，暂时不展示评论)

                // bottom space
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        } else {
            EmptyDataScreen()
        }
    } else if (animeDetailLD.type == LazyType.FAILURE) {
        ErrorScreen {
            viewModel.animeIdSF.value = viewModel.animeIdSF.value
        }
    } else {
        LoadingScreen()
    }
}