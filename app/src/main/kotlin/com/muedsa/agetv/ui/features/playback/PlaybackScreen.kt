package com.muedsa.agetv.ui.features.playback

import android.app.Activity
import androidx.annotation.OptIn
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.muedsa.agetv.BuildConfig
import com.muedsa.agetv.model.LazyType
import com.muedsa.agetv.viewmodel.PlaybackViewModel
import com.muedsa.compose.tv.widget.ErrorMessageBoxState
import com.muedsa.compose.tv.widget.player.DanmakuVideoPlayer
import com.muedsa.uitl.LogUtil
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
@OptIn(UnstableApi::class)
fun PlaybackScreen(
    aid: Int,
    episodeTitle: String,
    mediaUrl: String,
    danEpisodeId: Long = 0,
    playbackViewModel: PlaybackViewModel = hiltViewModel(),
    errorMsgBoxState: ErrorMessageBoxState
) {
    val activity = LocalContext.current as? Activity

    val danmakuSettingLD by playbackViewModel.danmakuSettingLDSF.collectAsState()
    val danmakuListLD by playbackViewModel.danmakuListLDSF.collectAsState()
    val episodeProgress by playbackViewModel.episodeProgressSF.collectAsState()

    var exoplayerHolder by remember { mutableStateOf<ExoPlayer?>(null) }

    var playerEnd by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = danmakuSettingLD) {
        if (danmakuListLD.type == LazyType.FAILURE) {
            errorMsgBoxState.error(danmakuListLD.error)
        }
    }

    LaunchedEffect(key1 = danmakuListLD) {
        if (danmakuListLD.type == LazyType.FAILURE) {
            errorMsgBoxState.error(danmakuListLD.error)
        }
    }

    LaunchedEffect(key1 = playerEnd) {
        if (playerEnd) {
            errorMsgBoxState.error("播放结束,即将返回")
            delay(3_000)
            activity?.finish()
        }
    }

    LaunchedEffect(key1 = exoplayerHolder) {
        if (exoplayerHolder != null) {
            val exoPlayer = exoplayerHolder!!
            delay(10_000)
            if (episodeProgress.aid == aid) {
                episodeProgress.progress = exoPlayer.currentPosition
                episodeProgress.duration = exoPlayer.duration
                if (episodeProgress.progress + 5_000 > episodeProgress.duration) {
                    episodeProgress.progress = max(episodeProgress.duration - 5_000, 0)
                }
                episodeProgress.updateAt = System.currentTimeMillis()
                playbackViewModel.saveEpisodeProgress(episodeProgress)
            }
        }
    }

    if (danmakuListLD.type == LazyType.SUCCESS && danmakuSettingLD.type == LazyType.SUCCESS && episodeProgress.aid == aid) {
        val danmakuSetting = danmakuSettingLD.data!!

        DanmakuVideoPlayer(
            debug = BuildConfig.DEBUG,
            danmakuConfigSetting = {
                textSizeScale = danmakuSetting.danmakuSizeScale / 100f
                alpha = danmakuSetting.danmakuAlpha / 100f
                screenPart = danmakuSetting.danmakuScreenPart / 100f
            },
            danmakuPlayerInit = {
                if (!danmakuListLD.data.isNullOrEmpty()) {
                    updateData(danmakuListLD.data!!)
                }
            }
        ) {
            addListener(object : Player.Listener {

                override fun onPlayerErrorChanged(error: PlaybackException?) {
                    errorMsgBoxState.error(error, SnackbarDuration.Long)
                    error?.let {
                        LogUtil.fb(it, "exoplayer mediaUrl: $mediaUrl")
                    }
                }

                override fun onRenderedFirstFrame() {
                    if (exoplayerHolder == null) {
                        exoplayerHolder = this@DanmakuVideoPlayer
                        if (episodeProgress.progress > 0) {
                            seekTo(episodeProgress.progress)
                            val progressStr = episodeProgress.progress
                                .toDuration(DurationUnit.MILLISECONDS)
                                .toComponents { hours, minutes, seconds, _ ->
                                    String.format(
                                        "%02d:%02d:%02d",
                                        hours,
                                        minutes,
                                        seconds,
                                    )
                                }
                            errorMsgBoxState.error("跳转到上次播放位置: $progressStr")
                        }
                    }
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    val ended = playbackState == Player.STATE_ENDED
                    if (playerEnd != ended) {
                        playerEnd = ended
                    }
                }
            })
            playWhenReady = true
            setMediaItem(MediaItem.fromUri(mediaUrl))
            prepare()
        }
    }

    if (danmakuSettingLD.type == LazyType.SUCCESS) {
        val danmakuSetting = danmakuSettingLD.data!!
        LaunchedEffect(key1 = danEpisodeId) {
            playbackViewModel.loadDanmakuList(
                if (danmakuSetting.danmakuEnable)
                    danEpisodeId
                else 0
            )
        }
    }

    LaunchedEffect(key1 = aid, key2 = episodeTitle) {
        playbackViewModel.loadEpisodeProgress(aid, episodeTitle)
    }
}