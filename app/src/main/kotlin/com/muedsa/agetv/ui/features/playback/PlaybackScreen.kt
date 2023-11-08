package com.muedsa.agetv.ui.features.playback

import androidx.annotation.OptIn
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
    viewModel: PlaybackViewModel = hiltViewModel()
) {
    val errorMsgBoxState = remember { ErrorMessageBoxState() }
    ErrorMessageBox(state = errorMsgBoxState) {

        val danmakuListLD by viewModel.danmakuListLDSF.collectAsState()

        LaunchedEffect(key1 = danEpisodeId) {
            viewModel.loadDanmakuList(danEpisodeId)
        }

        LaunchedEffect(key1 = danmakuListLD) {
            if (danmakuListLD.type == LazyType.FAILURE) {
                errorMsgBoxState.error(danmakuListLD.error)
            }
        }

        if (danmakuListLD.type == LazyType.SUCCESS) {
            DanmakuVideoPlayer(
                debug = BuildConfig.DEBUG,
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
                })
                playWhenReady = true
                setMediaItem(MediaItem.fromUri(mediaUrl))
                prepare()
            }
        }
    }
}