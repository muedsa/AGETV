package com.muedsa.agetv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import com.muedsa.agetv.ui.features.playback.PlaybackScreen
import com.muedsa.compose.tv.theme.TvTheme
import com.muedsa.compose.tv.widget.FillTextScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaybackActivity : ComponentActivity() {

    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mediaUrl = intent.getStringExtra(MEDIA_URL_KEY)
        setContent {
            TvTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (mediaUrl.isNullOrEmpty()) {
                        FillTextScreen(context = "视频地址错误")
                    } else {
                        PlaybackScreen(mediaUrl = mediaUrl)
                    }

                }
            }
        }
    }

    companion object {
        const val MEDIA_URL_KEY = "MEDIA_URL"
    }
}