package com.muedsa.agetv.viewmodel

import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.exoplayer.ExoPlayer
import com.kuaishou.akdanmaku.data.DanmakuItemData
import com.muedsa.agetv.model.LazyData
import com.muedsa.agetv.service.DanDanPlayApiService
import com.muedsa.uitl.LogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PlaybackViewModel @Inject constructor(
    private val danDanPlayApiService: DanDanPlayApiService
) : ViewModel() {

    private val _danmakuListLDSF = MutableStateFlow(LazyData.init<List<DanmakuItemData>>())
    val danmakuListLDSF: StateFlow<LazyData<List<DanmakuItemData>>> = _danmakuListLDSF

    private val _saverIdSet: MutableSet<String> = mutableSetOf()

    fun loadDanmakuList(episodeId: Long) {
        viewModelScope.launch {
            if (episodeId > 0) {
                _danmakuListLDSF.value = withContext(Dispatchers.IO) {
                    fetchDanmakuList(episodeId)
                }
            } else {
                _danmakuListLDSF.value = LazyData.success(emptyList())
            }
        }
    }

    private suspend fun fetchDanmakuList(episodeId: Long): LazyData<List<DanmakuItemData>> {
        return try {
            danDanPlayApiService.getComment(
                episodeId = episodeId,
                from = 0,
                withRelated = true,
                chConvert = 1
            ).comments
                .map {
                    val propArr = it.p.split(",")
                    val pos = (propArr[0].toFloat() * 1000).toLong()
                    val mode = if (propArr[1] == "1")
                        DanmakuItemData.DANMAKU_MODE_ROLLING
                    else if (propArr[1] == "4")
                        DanmakuItemData.DANMAKU_MODE_CENTER_BOTTOM
                    else if (propArr[1] == "5")
                        DanmakuItemData.DANMAKU_MODE_CENTER_TOP
                    else
                        DanmakuItemData.DANMAKU_MODE_ROLLING
                    val colorInt = propArr[2].toInt()
                    DanmakuItemData(
                        danmakuId = it.cid,
                        position = pos,
                        content = it.m,
                        mode = mode,
                        textSize = 25,
                        textColor = colorInt,
                        score = 9,
                        danmakuStyle = DanmakuItemData.DANMAKU_STYLE_NONE,
                        rank = 9
                    )
                }.let {
                    LazyData.success(it)
                }
        } catch (t: Throwable) {
            LogUtil.fb(t)
            LazyData.success(emptyList())
        }
    }

    fun registerPlayerPositionSaver(
        aid: Int,
        episodeTitle: String,
        exoPlayer: ExoPlayer,
        stopState: State<Boolean>
    ) {
        viewModelScope.launch(Dispatchers.Unconfined) {
            val id = "$aid:$episodeTitle"
            synchronized(_saverIdSet) {
                if (!_saverIdSet.contains(id)) {
                    _saverIdSet.add(id)
                    viewModelScope.launch(Dispatchers.Unconfined) {
                        LogUtil.d("[Player position saver-${id}] running for $aid-$episodeTitle")
                        while (!stopState.value) {
                            delay(15 * 1000)
                            val pos = withContext(Dispatchers.Main) {
                                exoPlayer.currentPosition
                            }
                            LogUtil.d("[Player position saver-${id}] save pos: $pos for $aid-$episodeTitle")
                        }
                        LogUtil.d("[Player position saver-${id}] stop for $aid-$episodeTitle")
                        synchronized(_saverIdSet) {
                            _saverIdSet.remove(id)
                        }
                    }
                }
            }
        }
    }
}