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
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.extractor.DefaultExtractorsFactory
import com.muedsa.agetv.BuildConfig
import com.muedsa.compose.tv.widget.ErrorMessageBox
import com.muedsa.compose.tv.widget.ErrorMessageBoxState
import com.muedsa.compose.tv.widget.player.SimpleVideoPlayer
import com.muedsa.uitl.ChromeUserAgent
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
            debug = BuildConfig.DEBUG,
            playerBuilderInit = {
                val dataSourceFactory = DefaultDataSource.Factory(
                    context,
                    DefaultHttpDataSource.Factory()
                        .setDefaultRequestProperties(
                            mapOf(
                                "Sec-Ch-Ua" to "\"Chromium\";v=\"118\", \"Google Chrome\";v=\"118\", \"Not=A?Brand\";v=\"99\"",
                                "Sec-Ch-Ua-Mobile" to "?0",
                                "Sec-Ch-Ua-Platform" to "\"Windows\""
                            )
                        )
                        .setUserAgent(ChromeUserAgent)
                )
                setMediaSourceFactory(
                    DefaultMediaSourceFactory(
                        dataSourceFactory,
                        DefaultExtractorsFactory()
                    )
                )
            },
            playerInit = {
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
        )
    }
}