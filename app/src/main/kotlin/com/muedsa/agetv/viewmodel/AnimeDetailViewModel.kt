package com.muedsa.agetv.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muedsa.agetv.exception.DataRequestException
import com.muedsa.agetv.model.AgePlayInfoModel
import com.muedsa.agetv.model.LazyData
import com.muedsa.agetv.model.LazyPagedList
import com.muedsa.agetv.model.LazyType
import com.muedsa.agetv.model.age.AnimeDetailPageModel
import com.muedsa.agetv.model.age.CommentModel
import com.muedsa.agetv.model.dandanplay.DanAnimeInfo
import com.muedsa.agetv.model.dandanplay.DanSearchAnime
import com.muedsa.agetv.repository.AppRepository
import com.muedsa.uitl.LogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AnimeDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repo: AppRepository
) : ViewModel() {

    val animeIdLD = savedStateHandle.getLiveData(ANIME_ID_SAVED_STATE_KEY, "0")
    val animeDetailLDState = mutableStateOf(LazyData.init<AnimeDetailPageModel>())
    val commentsLPState = mutableStateOf(LazyPagedList.new<CommentModel>())

    val danSearchAnimeListLDState = mutableStateOf<LazyData<List<DanSearchAnime>>>(LazyData.init())
    val danAnimeInfoLDState = mutableStateOf<LazyData<DanAnimeInfo>>(LazyData.init())

    private fun fetchAnimeDetail(aid: Int) {
        viewModelScope.launch(context = Dispatchers.IO) {
            try {
                repo.detail(aid).let {
                    animeDetailLDState.value = LazyData.success(it)
                }
            } catch (t: Throwable) {
                withContext(Dispatchers.Main) {
                    animeDetailLDState.value = LazyData.fail(t)
                }
                LogUtil.fb(t)
            }
        }
    }

    fun fetchNextPageComments() {
        viewModelScope.launch(context = Dispatchers.IO) {
            try {
                val paged = commentsLPState.value
                if (animeIdLD.value != null && paged.type != LazyType.LOADING && (paged.page == 0 || paged.hasNext)) {
                    repo.comment(aid = animeIdLD.value!!.toInt(), page = paged.nextPage).let {
                        if (it.code == 0) {
                            if (it.data != null) {
                                commentsLPState.value = paged
                                    .successNext(it.data.comments, it.data.pagination.totalPage)
                            }
                        } else {
                            commentsLPState.value =
                                paged.failNext(RuntimeException(it.message))
                        }
                    }
                }
            } catch (t: Throwable) {
                withContext(Dispatchers.Main) {
                    commentsLPState.value = commentsLPState.value.failNext(t)
                }
                LogUtil.fb(t)
            }
        }
    }

    fun parsePlayInfo(
        url: String,
        onSuccess: (AgePlayInfoModel) -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ) {
        viewModelScope.launch(context = Dispatchers.IO) {
            try {
                // val playInfo = repo.playInfo(URLDecoder.decode(url, Charsets.UTF_8.name()))
                val playInfo = repo.playInfo(url)
                withContext(Dispatchers.Main) {
                    onSuccess(playInfo)
                }
            } catch (t: Throwable) {
                withContext(Dispatchers.Main) {
                    onError(t)
                }
                LogUtil.fb(t)
            }

        }

    }

    fun danDanPlaySearchAnime() {
        if (animeDetailLDState.value.type == LazyType.SUCCESS
            || animeDetailLDState.value.data != null
        ) {
            val title = animeDetailLDState.value.data!!.video.name
            viewModelScope.launch(context = Dispatchers.IO) {
                try {
                    repo.danDanPlaySearchAnime(title).let {
                        if (it.errorCode == SUCCESS_CODE) {
                            danSearchAnimeListLDState.value = LazyData.success(it.animes)
                            if (it.animes.isNotEmpty()) {
                                fetchDanBangumi(it.animes[0].animeId)
                            }
                        } else {
                            throw DataRequestException(it.errorMessage)
                        }
                    }
                } catch (t: Throwable) {
                    withContext(Dispatchers.Main) {
                        danSearchAnimeListLDState.value = LazyData.fail(t)
                    }
                    LogUtil.fb(t)
                }
            }
        }
    }

    fun fetchDanBangumi(animeId: Int) {
        viewModelScope.launch(context = Dispatchers.IO) {
            try {
                repo.danDanPlayGetAnime(animeId).let {
                    if (it.errorCode == SUCCESS_CODE) {
                        danAnimeInfoLDState.value = LazyData.success(it.bangumi)
                    } else {
                        throw DataRequestException(it.errorMessage)
                    }
                }
            } catch (t: Throwable) {
                withContext(Dispatchers.Main) {
                    danAnimeInfoLDState.value = LazyData.fail(t)
                }
                LogUtil.fb(t)
            }
        }
    }

    init {
        viewModelScope.launch {
            animeIdLD.observeForever {
                it?.let {
                    fetchAnimeDetail(it.toInt())
                    // commentsLPState.value = LazyPagedList.new()
                    danSearchAnimeListLDState.value = LazyData.init()
                }
            }
        }
    }

    companion object {
        private const val ANIME_ID_SAVED_STATE_KEY = "animeId"

        const val SUCCESS_CODE = 0
    }
}