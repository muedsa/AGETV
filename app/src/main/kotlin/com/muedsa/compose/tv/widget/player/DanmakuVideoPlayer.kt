package com.muedsa.compose.tv.widget.player

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.KeyEvent
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.util.EventLogger
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.kuaishou.akdanmaku.DanmakuConfig
import com.kuaishou.akdanmaku.render.SimpleRenderer
import com.kuaishou.akdanmaku.ui.DanmakuPlayer
import com.kuaishou.akdanmaku.ui.DanmakuView
import com.muedsa.compose.tv.widget.OutlinedIconBox
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@SuppressLint("OpaqueUnitKey")
@OptIn(UnstableApi::class)
@Composable
fun DanmakuVideoPlayer(
    debug: Boolean = false,
    videoPlayerInit: ExoPlayer.() -> Unit,
    danmakuPlayerInit: DanmakuPlayer.() -> Unit
) {

    val context = LocalContext.current

    val playerControlTicker = remember { mutableIntStateOf(0) }

    val danmakuConfig = remember {
        DanmakuConfig().apply {
            textSizeScale = 1.4f
        }
    }

    val danmakuPlayer = remember {
        DanmakuPlayer(SimpleRenderer())
    }

    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .build()
            .also {
                if (debug) {
                    it.addAnalyticsListener(EventLogger())
                }
                it.addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        if (isPlaying) {
                            danmakuPlayer.seekTo(it.currentPosition)
                            danmakuPlayer.start(danmakuConfig)
                        } else {
                            danmakuPlayer.pause()
                        }
                    }
                })
                it.videoPlayerInit()
            }
    }

    BackHandler(enabled = playerControlTicker.intValue > 0) {
        playerControlTicker.intValue = 0
    }

    DisposableEffect(
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(factory = {
                PlayerView(context).apply {
                    hideController()
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    player = exoPlayer
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                    )
                }
            })
            AndroidView(factory = {
                DanmakuView(context).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                }.also {
                    danmakuPlayer.bindView(it)
                    danmakuPlayer.danmakuPlayerInit()
                }
            })
        }

    ) {
        onDispose {
            exoPlayer.release()
            danmakuPlayer.release()
        }
    }

    PlayerControl(debug = debug, player = exoPlayer, state = playerControlTicker)
}

@SuppressLint("OpaqueUnitKey")
@OptIn(UnstableApi::class)
@Composable
fun SimpleVideoPlayer(
    debug: Boolean = false,
    playerBuilderInit: ExoPlayer.Builder.() -> Unit = {},
    playerInit: ExoPlayer.() -> Unit
) {
    val context = LocalContext.current

    val playerControlTicker = remember { mutableIntStateOf(0) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .also(playerBuilderInit)
            .build()
            .also {
                if (debug) {
                    it.addAnalyticsListener(EventLogger())
                }
                it.playerInit()
            }
    }

    BackHandler(enabled = playerControlTicker.intValue > 0) {
        playerControlTicker.intValue = 0
    }

    DisposableEffect(
        AndroidView(factory = {
            PlayerView(context).apply {
                hideController()
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                player = exoPlayer
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    Gravity.CENTER
                )
            }
        })
    ) {
        onDispose {
            exoPlayer.release()
        }
    }

    PlayerControl(debug = debug, player = exoPlayer, state = playerControlTicker)
}

@kotlin.OptIn(ExperimentalTvMaterial3Api::class)
@OptIn(UnstableApi::class)
@Composable
fun PlayerControl(
    modifier: Modifier = Modifier,
    debug: Boolean = false,
    player: Player,
    state: MutableState<Int> = remember { mutableIntStateOf(0) },
) {
    var leftArrowBtnPressed by remember { mutableStateOf(false) }
    var rightArrowBtnPressed by remember { mutableStateOf(false) }
    var playBtnPressed by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        while (true) {
            delay(1.seconds)
            if (state.value > 0) {
                state.value--
            }
        }
    }

    Box(modifier = modifier
        .focusable()
        .onPreviewKeyEvent {
            if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) {
                if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DPAD_UP
                    || it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DPAD_LEFT
                    || it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
                    || it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DPAD_DOWN
                    || it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DPAD_CENTER
                    || it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER
                    || it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_MENU
                    || it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
                    || it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_MEDIA_PLAY
                    || it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE
                ) {
                    state.value = 5
                }
            }
            if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (it.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) {
                    leftArrowBtnPressed = true
                } else if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) {
                    leftArrowBtnPressed = false
                    player.seekBack()
                }
            } else if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (it.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) {
                    rightArrowBtnPressed = true
                } else if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) {
                    rightArrowBtnPressed = false
                    player.seekForward()
                }
            } else if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                if (it.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) {
                    playBtnPressed = true
                } else if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) {
                    playBtnPressed = false
                    if (player.isPlaying) {
                        player.pause()
                    } else {
                        player.play()
                    }
                }
            } else if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
                if (it.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) {
                    playBtnPressed = true
                } else if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) {
                    playBtnPressed = false
                    if (!player.isPlaying) {
                        player.play()
                    }
                }
            } else if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
                if (it.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) {
                    playBtnPressed = true
                } else if (it.nativeKeyEvent.action == KeyEvent.ACTION_UP) {
                    playBtnPressed = false
                    if (player.isPlaying) {
                        player.pause()
                    }
                }
            }
            return@onPreviewKeyEvent false
        }
        .fillMaxSize()
    ) {
        AnimatedVisibility(
            visible = state.value > 0,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.75f))
                    .padding(20.dp),
                verticalArrangement = Arrangement.Bottom,
            ) {
                PlayerProgressIndicator(player)
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom
                ) {
                    OutlinedIconBox(scaleUp = leftArrowBtnPressed) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "后退")
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    OutlinedIconBox(
                        scaleUp = playBtnPressed,
                        inverse = true
                    ) {
                        if (player.isPlaying) {
                            Icon(
                                Icons.Outlined.Pause,
                                contentDescription = "暂停"
                            )
                        } else {
                            Icon(Icons.Outlined.PlayArrow, contentDescription = "播放")
                        }
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    OutlinedIconBox(scaleUp = rightArrowBtnPressed) {
                        Icon(Icons.Outlined.ArrowForward, contentDescription = "前进")
                    }
                }

                if (debug) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(text = "show: $state", color = Color.Red)
                }
            }
        }
    }
}

@kotlin.OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PlayerProgressIndicator(player: Player) {

    val currentStr = if (player.duration > 0L) {
        player.currentPosition.toDuration(DurationUnit.MILLISECONDS)
            .toComponents { hours, minutes, seconds, _ ->
                String.format(
                    "%02d:%02d:%02d",
                    hours,
                    minutes,
                    seconds,
                )
            }
    } else {
        "--:--:--"
    }
    val totalStr = if (player.duration > 0L) {
        player.duration.toDuration(DurationUnit.MILLISECONDS)
            .toComponents { hours, minutes, seconds, _ ->
                String.format(
                    "%02d:%02d:%02d",
                    hours,
                    minutes,
                    seconds,
                )
            }
    } else {
        "--:--:--"
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        if (player.duration > 0L) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                progress = player.currentPosition.toFloat() / player.duration,
            )
        } else {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "$currentStr / $totalStr",
            textAlign = TextAlign.Right,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
    }

}