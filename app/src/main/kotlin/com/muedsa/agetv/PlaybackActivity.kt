package com.muedsa.agetv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import com.muedsa.agetv.ui.features.playback.PlaybackScreen
import com.muedsa.compose.tv.theme.TvTheme
import com.muedsa.compose.tv.widget.AppCloseHandler
import com.muedsa.compose.tv.widget.ErrorMessageBox
import com.muedsa.compose.tv.widget.ErrorMessageBoxState
import com.muedsa.compose.tv.widget.FillTextScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaybackActivity : ComponentActivity() {

    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val aid = intent.getIntExtra(AID_KEY, 0)
        val episodeTitle = intent.getStringExtra(EPISODE_TITLE_KEY)
        val mediaUrl = intent.getStringExtra(MEDIA_URL_KEY)
        val episodeId = intent.getLongExtra(DAN_EPISODE_ID_KEY, 0)
        setContent {
            TvTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (aid <= 0 || episodeTitle.isNullOrEmpty() || mediaUrl.isNullOrEmpty()) {
                        FillTextScreen(context = "视频地址错误")
                    } else {
                        val errorMsgBoxState = remember { ErrorMessageBoxState() }
                        AppCloseHandler {
                            errorMsgBoxState.error("再次点击返回键退出")
                        }
                        ErrorMessageBox(state = errorMsgBoxState) {
                            PlaybackScreen(
                                aid = aid,
                                episodeTitle = episodeTitle,
                                mediaUrl = mediaUrl,
                                danEpisodeId = episodeId,
                                errorMsgBoxState = errorMsgBoxState
                            )
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val AID_KEY = "AID"
        const val EPISODE_TITLE_KEY = "EPISODE_TITLE"
        const val MEDIA_URL_KEY = "MEDIA_URL"
        const val DAN_EPISODE_ID_KEY = "EPISODE_ID"
    }
}