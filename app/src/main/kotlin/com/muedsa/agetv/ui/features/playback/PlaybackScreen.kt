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
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.muedsa.agetv.BuildConfig
import com.muedsa.agetv.model.LazyType
import com.muedsa.agetv.viewmodel.PlaybackViewModel
import com.muedsa.compose.tv.widget.ErrorMessageBox
import com.muedsa.compose.tv.widget.ErrorMessageBoxState
import com.muedsa.compose.tv.widget.player.DanmakuVideoPlayer
import com.muedsa.uitl.LogUtil

@Composable
@OptIn(UnstableApi::class)
fun PlaybackScreen(
    aid: Int,
    episodeTitle: String,
    mediaUrl: String,
    danEpisodeId: Long = 0,
    playbackViewModel: PlaybackViewModel = hiltViewModel(),
) {
    val activity = (LocalContext.current as? Activity)

    val errorMsgBoxState = remember { ErrorMessageBoxState() }
    ErrorMessageBox(state = errorMsgBoxState) {

        val danmakuSettingLD by playbackViewModel.danmakuSettingLDSF.collectAsState()
        val danmakuListLD by playbackViewModel.danmakuListLDSF.collectAsState()

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

        if (danmakuListLD.type == LazyType.SUCCESS && danmakuSettingLD.type == LazyType.SUCCESS) {
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
                    private val stopState = mutableStateOf(true)

                    override fun onPlayerErrorChanged(error: PlaybackException?) {
                        errorMsgBoxState.error(error, SnackbarDuration.Long)
                        error?.let {
                            LogUtil.fb(it, "exoplayer mediaUrl: $mediaUrl")
                        }
                    }

                    override fun onRenderedFirstFrame() {
                        stopState.value = false
                        playbackViewModel.registerPlayerPositionSaver(
                            aid,
                            episodeTitle,
                            this@DanmakuVideoPlayer,
                            stopState
                        )
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        LogUtil.d("onPlaybackStateChanged: $playbackState")
                        when (playbackState) {
                            Player.STATE_READY -> {

                            }

                            Player.STATE_ENDED -> {
                                stopState.value = true
                                activity?.finish()
                            }

                            Player.STATE_IDLE -> {
                                stopState.value = true
                            }

                            Player.STATE_BUFFERING -> {

                            }
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
    }
}