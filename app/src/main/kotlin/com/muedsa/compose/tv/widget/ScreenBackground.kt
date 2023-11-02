package com.muedsa.compose.tv.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.muedsa.compose.tv.theme.TvTheme
import com.muedsa.uitl.LogUtil
import jp.wasabeef.transformers.coil.BlurTransformation
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ScreenBackground(
    state: ScreenBackgroundState = rememberScreenBackgroundState()
) {
    val delayState = rememberScreenBackgroundState(
        initUrl = state.url,
        initType = state.type,
        initHeaders = state.headers
    )

    LaunchedEffect(key1 = state.url, key2 = state.type, key3 = state.headers) {
        delay(300.milliseconds)
        delayState.url = state.url
        delayState.type = state.type
        delayState.headers.apply {
            clear()
            putAll(state.headers)
        }
    }

    if (!delayState.url.isNullOrEmpty()) {
        val context = LocalContext.current
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        val screenHeight = configuration.screenHeightDp.dp
        val horizontal = screenWidth > screenHeight

        val immersiveImageWidth: Dp
        val immersiveImageHeight: Dp
        val immersiveImageOffsetX: Dp
        if (horizontal) {
            immersiveImageHeight = screenHeight * 8 / 10
            immersiveImageWidth = immersiveImageHeight * 16 / 9
            immersiveImageOffsetX = screenWidth - immersiveImageWidth
        } else {
            immersiveImageWidth = screenWidth
            immersiveImageHeight = screenHeight * 8 / 10
            immersiveImageOffsetX = 0.dp
        }
        val imageModifier = if (delayState.type == ScreenBackgroundType.SCRIM) {
            Modifier
                .size(immersiveImageWidth, immersiveImageHeight)
                .offset(x = immersiveImageOffsetX)
        } else {
            Modifier.size(screenWidth, screenHeight)
        }
        val imageRequest = ImageRequest.Builder(context)
            .data(delayState.url)
            .crossfade(true)
            .listener(onError = { _, result ->
                LogUtil.d(result.throwable, "loading image error")
            })
            .also {
                if (delayState.type == ScreenBackgroundType.BLUR) {
                    it.transformations(
                        BlurTransformation(context = context, radius = 25),
                    )
                }
                if (!delayState.headers.isEmpty()) {
                    delayState.headers.forEach { entry ->
                        it.addHeader(entry.key, entry.value)
                    }
                }
            }.build()

        Box(Modifier.fillMaxSize()) {
            SubcomposeAsyncImage(
                model = imageRequest,
                contentDescription = null,
                modifier = imageModifier,
                contentScale = if (horizontal) ContentScale.FillWidth else ContentScale.Crop
            )

            if (delayState.type == ScreenBackgroundType.BLUR) {
                Box(
                    Modifier
                        .size(screenWidth, screenHeight)
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.7f))
                )
            } else if (delayState.type == ScreenBackgroundType.SCRIM) {
                Box(
                    Modifier
                        .size(immersiveImageWidth, immersiveImageHeight)
                        .offset(x = immersiveImageOffsetX)
                        .background(
                            Brush.horizontalGradient(
                                0.0f to MaterialTheme.colorScheme.background,
                                0.8f to Color.Transparent,
                                startX = 0.0f
                            )
                        )
                )
                Box(
                    Modifier
                        .size(immersiveImageWidth, immersiveImageHeight)
                        .offset(x = immersiveImageOffsetX)
                        .background(
                            Brush.verticalGradient(
                                0.0f to Color.Transparent,
                                0.5f to MaterialTheme.colorScheme.background.copy(alpha = 0.3f),
                                1.0f to MaterialTheme.colorScheme.background,
                                startY = 0.0f
                            )
                        )
                )
            }
        }
    }
}

@Stable
class ScreenBackgroundState(
    initUrl: String? = null,
    initType: ScreenBackgroundType = ScreenBackgroundType.BLUR,
    initHeaders: Map<String, String> = mapOf()
) {
    var url by mutableStateOf(initUrl)
    var type by mutableStateOf(initType)
    val headers = mutableStateMapOf(*initHeaders.toList().toTypedArray())

    companion object {
        private const val SAVER_KEY_URL = "SAVER_KEY_URL"
        private const val SAVER_KEY_TYPE = "SAVER_KEY_TYPE"

        val Saver: Saver<ScreenBackgroundState, *> = mapSaver(
            save = {
                mutableMapOf<String, Any?>().apply {
                    put(SAVER_KEY_URL, it.url)
                    put(SAVER_KEY_TYPE, it.type)
                    putAll(it.headers)
                }
            },
            restore = {
                @Suppress("UNCHECKED_CAST")
                (ScreenBackgroundState(
                    initUrl = it[SAVER_KEY_URL] as String?,
                    initType = it[SAVER_KEY_TYPE] as ScreenBackgroundType,
                    initHeaders = it.filterKeys { key ->
                        SAVER_KEY_URL != key && SAVER_KEY_TYPE != key
                    } as Map<String, String>
                ))
            }
        )
    }
}

@Composable
fun rememberScreenBackgroundState(
    initUrl: String? = null,
    initType: ScreenBackgroundType = ScreenBackgroundType.BLUR,
    initHeaders: Map<String, String> = mapOf()
): ScreenBackgroundState {
    return rememberSaveable(saver = ScreenBackgroundState.Saver) {
        ScreenBackgroundState(
            initUrl = initUrl,
            initType = initType,
            initHeaders = initHeaders
        )
    }
}

enum class ScreenBackgroundType {
    BLUR,
    SCRIM
}

@Preview
@Composable
fun ScreenBackgroundPreview() {
    TvTheme {
        ScreenBackground()
    }
}