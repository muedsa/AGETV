package com.muedsa.agetv.ui.features.detail

import android.content.Intent
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.material3.AssistChip
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.LocalContentColor
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.OutlinedButton
import androidx.tv.material3.Text
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
import com.muedsa.compose.tv.widget.ErrorMessageBoxState
import com.muedsa.compose.tv.widget.ErrorScreen
import com.muedsa.compose.tv.widget.ExposedDropdownMenuButton
import com.muedsa.compose.tv.widget.FocusScaleSwitch
import com.muedsa.compose.tv.widget.LoadingScreen
import com.muedsa.compose.tv.widget.ScreenBackground
import com.muedsa.compose.tv.widget.ScreenBackgroundType
import com.muedsa.compose.tv.widget.StandardImageCardsRow
import com.muedsa.compose.tv.widget.rememberScreenBackgroundState
import com.muedsa.uitl.LogUtil
import kotlinx.coroutines.flow.update

@OptIn(
    ExperimentalTvMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
fun AnimeDetailScreen(
    viewModel: AnimeDetailViewModel = hiltViewModel(),
    errorMsgBoxState: ErrorMessageBoxState,
    onNavigate: (NavigationItems, List<String>?) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    val animeDetailLD by viewModel.animeDetailLDSF.collectAsState()
    val favoriteModel by viewModel.favoriteModelSF.collectAsState()
    val progressedEpisodeTitleSet by viewModel.progressedEpisodeTitleSetSF.collectAsState()
    val danSearchAnimeListLD by viewModel.danSearchAnimeListLDSF.collectAsState()
    val danAnimeInfoLD by viewModel.danAnimeInfoLDSF.collectAsState()

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
                    .offset(x = ScreenPaddingLeft)
                    .width(screenWidth - ScreenPaddingLeft),
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
                        Text(
                            text = "播放源",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        ExposedDropdownMenuButton(
                            itemList = info.playLists.keys.toList(),
                            textFn = { _, item ->
                                animeDetail.playerLabelArr.getOrDefault(
                                    item,
                                    "Unknown"
                                ) + if (animeDetail.isVip(item)) "*" else " "
                            },
                            onSelected = { _, item ->
                                selectedPlaySource = item
                                selectedPlaySourceList = info.playLists[item]!!
                            }
                        )

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
                                tint = if (favoriteModel == null) FavoriteIconColor else LocalContentColor.current
                            )
                        }

                        // 开启弹幕按钮
                        if (danAnimeInfoLD.type == LazyType.SUCCESS && danAnimeInfoLD.data != null) {
                            Spacer(modifier = Modifier.width(25.dp))
                            Text(
                                text = "弹幕",
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            FocusScaleSwitch(
                                checked = enabledDanmaku,
                                onCheckedChange = { enabledDanmaku = it }
                            )
                        }

                        // 弹弹Play数据
                        if (danSearchAnimeListLD.type == LazyType.SUCCESS && !danSearchAnimeListLD.data.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.width(25.dp))
                            Text(
                                text = "匹配弹弹Play",
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            ExposedDropdownMenuButton(
                                itemList = danSearchAnimeListLD.data!!,
                                textFn = { _, item ->
                                    item.animeTitle
                                },
                                onSelected = { _, item ->
                                    viewModel.danBangumi(item.animeId)
                                }
                            )
                        }

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

                // 切换播放源
                item {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        selectedPlaySourceList.forEachIndexed { index, item ->
                            AssistChip(
                                modifier = Modifier.padding(end = 8.dp, bottom = 8.dp),
                                onClick = {
                                    LogUtil.fb("click play: $item")
                                    val url = if (animeDetail.isVip(selectedPlaySource)) {
                                        "${animeDetail.playerJx["vip"]}${item[1]}"
                                    } else {
                                        "${animeDetail.playerJx["zj"]}${item[1]}"
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
                                                item[0]
                                            )
                                            intent.putExtra(
                                                PlaybackActivity.MEDIA_URL_KEY,
                                                it.realUrl
                                            )
                                            if (enabledDanmaku
                                                && danAnimeInfoLD.type == LazyType.SUCCESS
                                                && danAnimeInfoLD.data != null
                                                && !danAnimeInfoLD.data?.episodes.isNullOrEmpty()
                                                && danAnimeInfoLD.data?.episodes!!.size > index
                                            ) {
                                                intent.putExtra(
                                                    PlaybackActivity.DAN_EPISODE_ID_KEY,
                                                    danAnimeInfoLD.data?.episodes!![index].episodeId
                                                )
                                            }
                                            context.startActivity(intent)
                                        },
                                        onError = {
                                            errorMsgBoxState.error(it)
                                        }
                                    )
                                }
                            ) {
                                if (enabledDanmaku
                                    && danAnimeInfoLD.type == LazyType.SUCCESS
                                    && danAnimeInfoLD.data != null
                                    && !danAnimeInfoLD.data?.episodes.isNullOrEmpty()
                                    && danAnimeInfoLD.data?.episodes!!.size > index
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = if (progressedEpisodeTitleSet.contains(item[0]))
                                                "${item[0]}*" else item[0]
                                        )
                                        Text(
                                            text = danAnimeInfoLD.data!!.episodes[index].episodeTitle,
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                } else {
                                    Text(text = item[0])
                                }
                            }
                        }
                    }
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