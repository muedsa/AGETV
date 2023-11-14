package com.muedsa.agetv.ui.features.detail

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.itemsIndexed
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import androidx.tv.material3.WideButton
import com.muedsa.agetv.model.dandanplay.DanEpisode
import com.muedsa.agetv.room.model.EpisodeProgressModel
import com.muedsa.compose.tv.theme.ImageCardRowCardPadding
import kotlin.math.max

const val EpisodePageSize = 20
val EpisodeProgressStrokeWidth = 12.dp
val WideButtonCornerRadius = 12.dp

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun EpisodeListWidget(
    modifier: Modifier = Modifier,
    episodeList: List<List<String>>,
    danEpisodeList: List<DanEpisode>,
    episodeProgressMap: Map<String, EpisodeProgressModel>,
    episodeRelationMap: Map<String, Long>,
    onEpisodeClick: (List<String>, DanEpisode?) -> Unit = { _, _ -> },
    onChangeEpisodeRelation: (List<Pair<String, DanEpisode>>) -> Unit = {}
) {

    val episodeListChunks = episodeList.chunked(EpisodePageSize)
    val danEpisodeListChunks = danEpisodeList.chunked(EpisodePageSize)

    var changeDanEpisodeMode by remember { mutableStateOf(false) }
    var selectedEpisodeIndex by remember { mutableIntStateOf(0) }
    var selectedEpisode by remember { mutableStateOf(episodeList[0]) }

    BackHandler(enabled = changeDanEpisodeMode) {
        changeDanEpisodeMode = false
    }

    Crossfade(targetState = changeDanEpisodeMode, label = "changeDanEpisodeCrossFade") {
        Column(modifier = modifier) {
            if (!it) {
                episodeListChunks.forEachIndexed { chunkIndex, currentPartEpisodeList ->
                    val episodePartStartNo = 1 + chunkIndex * EpisodePageSize
                    val episodePartEndNo = episodePartStartNo - 1 + currentPartEpisodeList.size
                    // 剧集标题
                    Row(
                        modifier = Modifier.padding(
                            start = ImageCardRowCardPadding,
                            bottom = ImageCardRowCardPadding
                        ),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "剧集 ${episodePartStartNo}-${episodePartEndNo}",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1
                        )
                        if (chunkIndex == 0 && danEpisodeList.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(ImageCardRowCardPadding))
                            Text(
                                text = "长按更改匹配的弹幕剧集",
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1
                            )
                        }
                    }

                    // 剧集列表
                    TvLazyRow {
                        itemsIndexed(
                            items = currentPartEpisodeList,
                            key = { _, item -> item[1] }
                        ) { episodePartIndex, episode ->
                            val interactionSource = remember { MutableInteractionSource() }
                            val focusedState = interactionSource.collectIsFocusedAsState()
                            WideButton(
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .drawWithCache {
                                        // 进度条
                                        val model = episodeProgressMap[episode[0]]
                                        val strokeWidthPx = EpisodeProgressStrokeWidth.toPx()
                                        val wideButtonCornerRadiusPx = WideButtonCornerRadius.toPx()
                                        val width = if (model != null) {
                                            val progressRatio =
                                                model.progress.toFloat() / model.duration
                                            size.width * max(progressRatio, 0.1f)
                                        } else 0f
                                        val height = strokeWidthPx / 2
                                        val cornerRadius = CornerRadius(
                                            wideButtonCornerRadiusPx,
                                            wideButtonCornerRadiusPx
                                        )
                                        onDrawWithContent {
                                            drawContent()
                                            if (width > 0) {
                                                scale(if (focusedState.value) 1.1f else 1f) {
                                                    clipRect(
                                                        right = width,
                                                        bottom = height
                                                    ) {
                                                        drawRoundRect(
                                                            color = Color.Red,
                                                            cornerRadius = cornerRadius
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    },
                                interactionSource = interactionSource,
                                title = {
                                    Text(text = episode[0], overflow = TextOverflow.Ellipsis)
                                },
                                subtitle = {
                                    val danEpisode = getDanEpisode(
                                        episode = episode,
                                        episodeIndex = episodePartIndex + chunkIndex * EpisodePageSize,
                                        danEpisodeList = danEpisodeList,
                                        episodeRelationMap = episodeRelationMap
                                    )
                                    if (danEpisode != null) {
                                        Text(
                                            text = danEpisode.episodeTitle,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                },
                                onClick = {
                                    onEpisodeClick(
                                        episode, getDanEpisode(
                                            episode = episode,
                                            episodeIndex = episodePartIndex + chunkIndex * EpisodePageSize,
                                            danEpisodeList = danEpisodeList,
                                            episodeRelationMap = episodeRelationMap
                                        )
                                    )
                                },
                                onLongClick = {
                                    if (danEpisodeList.isNotEmpty()) {
                                        val index = episodePartIndex + chunkIndex * EpisodePageSize
                                        selectedEpisodeIndex = index
                                        selectedEpisode = episodeList[index]
                                        changeDanEpisodeMode = true
                                    }
                                }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.width(100.dp))
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.padding(
                        start = ImageCardRowCardPadding,
                        bottom = ImageCardRowCardPadding
                    ),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "更改弹幕剧情匹配模式",
                        color = MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.width(ImageCardRowCardPadding))
                    Text(
                        text = "选择一个弹弹Play剧集来匹配:",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1
                    )
                    Text(
                        text = selectedEpisode[0],
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1
                    )
                }

                danEpisodeListChunks.forEachIndexed { danChunkIndex, danEpisodeList ->
                    val episodePartStartNo = 1 + danChunkIndex * EpisodePageSize
                    val episodePartEndNo = episodePartStartNo - 1 + danEpisodeList.size
                    Row(
                        modifier = Modifier.padding(
                            start = ImageCardRowCardPadding,
                            bottom = ImageCardRowCardPadding
                        ),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "剧集 ${episodePartStartNo}-${episodePartEndNo}",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1
                        )
                        if (danChunkIndex == 0) {
                            Spacer(modifier = Modifier.width(ImageCardRowCardPadding))
                            Text(
                                text = "长按使接下来的剧集都按当前选择的剧集依次匹配",
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1
                            )
                        }
                    }

                    TvLazyRow {
                        itemsIndexed(
                            items = danEpisodeList,
                            key = { _, item -> item.episodeId }
                        ) { danEpisodePartIndex, danEpisode ->
                            WideButton(
                                modifier = Modifier.padding(end = 12.dp),
                                title = {
                                    Text(
                                        text = danEpisode.episodeTitle,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                onClick = {
                                    onChangeEpisodeRelation(listOf(selectedEpisode[0] to danEpisode))
                                    changeDanEpisodeMode = false
                                },
                                onLongClick = {
                                    onChangeEpisodeRelation(buildList {
                                        var danEpisodePos =
                                            danEpisodePartIndex + danChunkIndex * EpisodePageSize
                                        for (episodePos in selectedEpisodeIndex..<episodeList.size) {
                                            if (danEpisodePos >= danEpisodeList.size) {
                                                break
                                            }
                                            add(episodeList[episodePos][0] to danEpisodeList[danEpisodePos])
                                            danEpisodePos++
                                        }
                                    })
                                    changeDanEpisodeMode = false
                                }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.width(100.dp))
                        }
                    }
                }
            }
        }
    }
}

fun getDanEpisode(
    episode: List<String>,
    episodeIndex: Int,
    danEpisodeList: List<DanEpisode>,
    episodeRelationMap: Map<String, Long>
): DanEpisode? {
    var danEpisode: DanEpisode? = null

    if (danEpisodeList.isNotEmpty()) {
        val episodeId = episodeRelationMap[episode[0]]
        if (episodeId != null) {
            val danEpisodeOptional = danEpisodeList.stream()
                .filter { it.episodeId == episodeId }
                .findFirst()
            if (danEpisodeOptional.isPresent) {
                danEpisode = danEpisodeOptional.get()
            }
        } else {
            if (episodeIndex < danEpisodeList.size) {
                danEpisode = danEpisodeList[episodeIndex]
            }
        }
    }
    return danEpisode
}