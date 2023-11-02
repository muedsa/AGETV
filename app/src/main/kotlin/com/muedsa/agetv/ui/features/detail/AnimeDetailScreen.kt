package com.muedsa.agetv.ui.features.detail

import android.content.Intent
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.muedsa.agetv.PlaybackActivity
import com.muedsa.agetv.model.LazyType
import com.muedsa.agetv.ui.AgePosterSize
import com.muedsa.agetv.ui.RankFontColor
import com.muedsa.agetv.ui.RankIconColor
import com.muedsa.agetv.viewmodel.AnimeDetailViewModel
import com.muedsa.compose.tv.model.ContentModel
import com.muedsa.compose.tv.theme.ScreenPaddingLeft
import com.muedsa.compose.tv.widget.ContentBlock
import com.muedsa.compose.tv.widget.ContentBlockType
import com.muedsa.compose.tv.widget.EmptyDataScreen
import com.muedsa.compose.tv.widget.ErrorMessageBoxState
import com.muedsa.compose.tv.widget.ErrorScreen
import com.muedsa.compose.tv.widget.ExposedDropdownMenuButton
import com.muedsa.compose.tv.widget.LoadingScreen
import com.muedsa.compose.tv.widget.ScreenBackground
import com.muedsa.compose.tv.widget.ScreenBackgroundType
import com.muedsa.compose.tv.widget.StandardImageCardsRow
import com.muedsa.compose.tv.widget.rememberScreenBackgroundState
import com.muedsa.uitl.LogUtil

@OptIn(
    ExperimentalTvMaterial3Api::class,
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
fun AnimeDetailScreen(
    viewModel: AnimeDetailViewModel = hiltViewModel(),
    errorMsgBoxState: ErrorMessageBoxState
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    val animeDetailLD by remember { viewModel.animeDetailLDState }
    val commentsLP by remember { viewModel.commentsLPState }

    val danSearchAnimeListLD by remember { viewModel.danSearchAnimeListLDState }
    val danAnimeInfo by remember { viewModel.danAnimeInfoLDState }

    val backgroundState = rememberScreenBackgroundState(
        initType = ScreenBackgroundType.SCRIM
    )

    LaunchedEffect(key1 = animeDetailLD) {
        if (animeDetailLD.type == LazyType.FAILURE) {
            errorMsgBoxState.error(animeDetailLD.error)
        } else if (animeDetailLD.type == LazyType.SUCCESS) {
            if (animeDetailLD.data != null) {
                backgroundState.url = animeDetailLD.data!!.video.cover
                viewModel.danDanPlaySearchAnime()
            }
        }
    }

    LaunchedEffect(key1 = commentsLP) {
        if (commentsLP.type == LazyType.FAILURE) {
            errorMsgBoxState.error(commentsLP.error)
        }
    }

    LaunchedEffect(key1 = danSearchAnimeListLD) {
        if (danSearchAnimeListLD.type == LazyType.FAILURE) {
            errorMsgBoxState.error(danSearchAnimeListLD.error)
        }
    }

    LaunchedEffect(key1 = danAnimeInfo) {
        if (danAnimeInfo.type == LazyType.FAILURE) {
            errorMsgBoxState.error(danAnimeInfo.error)
        }
    }

    ScreenBackground(backgroundState)

    if (animeDetailLD.type == LazyType.SUCCESS) {
        if (animeDetailLD.data != null) {
            val animeDetail = animeDetailLD.data!!
            val info = animeDetail.video

            var expanded by remember { mutableStateOf(false) }
            var selectedPlaySource by remember { mutableStateOf(info.playLists.keys.first()) }
            var selectedPlaySourceList by remember { mutableStateOf(info.playLists[selectedPlaySource]!!) }

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
                        if (danAnimeInfo.type == LazyType.SUCCESS && danAnimeInfo.data != null) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "评分",
                                tint = RankIconColor
                            )
                            Text(
                                modifier = Modifier
                                    .width(70.dp)
                                    .padding(start = 8.dp, end = 15.dp),
                                text = "${danAnimeInfo.data!!.rating}",
                                color = RankFontColor,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(25.dp))
                }

                // 播放源
                item {

                    // 切换播放源
                    Row(verticalAlignment = Alignment.CenterVertically) {
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

                        // 弹弹Play数据
                        if (danSearchAnimeListLD.type == LazyType.SUCCESS && !danSearchAnimeListLD.data.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.width(15.dp))
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
                                    viewModel.fetchDanBangumi(item.animeId)
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(0.9f),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        selectedPlaySourceList.forEach {
                            AssistChip(
                                modifier = Modifier.padding(end = 8.dp, bottom = 8.dp),
                                onClick = {
                                    LogUtil.fb("click play: $it")
                                    val url = if (animeDetail.isVip(selectedPlaySource)) {
                                        "${animeDetail.playerJx["vip"]}${it[1]}"
                                    } else {
                                        "${animeDetail.playerJx["zj"]}${it[1]}"
                                    }
                                    LogUtil.fb("click playPage: $url")
                                    viewModel.parsePlayInfo(
                                        url = url,
                                        onSuccess = {
                                            LogUtil.fb("try play => $it")
                                            val intent =
                                                Intent(context, PlaybackActivity::class.java)
                                            intent.putExtra(
                                                PlaybackActivity.MEDIA_URL_KEY,
                                                it.realUrl
                                            )
                                            context.startActivity(intent)
                                        },
                                        onError = {
                                            errorMsgBoxState.error(it)
                                        }
                                    )
                                }
                            ) {
                                Text(text = it[0])
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
                                viewModel.animeIdLD.value = anime.aid.toString()
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
                                viewModel.animeIdLD.value = anime.aid.toString()
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
            viewModel.animeIdLD.value = viewModel.animeIdLD.value
        }
    } else {
        LoadingScreen()
    }


}