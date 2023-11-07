package com.muedsa.agetv.ui.features.playback

import androidx.annotation.OptIn
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.muedsa.agetv.BuildConfig
import com.muedsa.compose.tv.widget.ErrorMessageBox
import com.muedsa.compose.tv.widget.ErrorMessageBoxState
import com.muedsa.compose.tv.widget.player.SimpleVideoPlayer
import com.muedsa.uitl.LogUtil

@Composable
@OptIn(UnstableApi::class)
fun PlaybackScreen(
    aid: Int,
    episodeTitle: String,
    mediaUrl: String,
    danEpisodeId: Long = 0,
) {
    val context = LocalContext.current
    val errorMessageBoxState = remember { ErrorMessageBoxState() }
    ErrorMessageBox(state = errorMessageBoxState) {
        SimpleVideoPlayer(
            debug = BuildConfig.DEBUG
        ) {
            addListener(object : Player.Listener {
                override fun onPlayerErrorChanged(error: PlaybackException?) {
                    errorMessageBoxState.error(error, SnackbarDuration.Long)
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