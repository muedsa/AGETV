package com.muedsa.agetv.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muedsa.agetv.exception.DataRequestException
import com.muedsa.agetv.model.AgePlayInfoModel
import com.muedsa.agetv.model.LazyData
import com.muedsa.agetv.model.LazyType
import com.muedsa.agetv.model.age.AnimeDetailPageModel
import com.muedsa.agetv.model.dandanplay.DanAnimeInfo
import com.muedsa.agetv.model.dandanplay.DanSearchAnime
import com.muedsa.agetv.repository.AppRepository
import com.muedsa.agetv.room.dao.EpisodeProgressDao
import com.muedsa.agetv.room.dao.FavoriteAnimeDao
import com.muedsa.agetv.room.model.FavoriteAnimeModel
import com.muedsa.uitl.LogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AnimeDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repo: AppRepository,
    private val favoriteAnimeDao: FavoriteAnimeDao,
    private val episodeProgressDao: EpisodeProgressDao
) : ViewModel() {

    private val _navAnimeIdFlow = savedStateHandle.getStateFlow(ANIME_ID_SAVED_STATE_KEY, "0")
    val animeIdSF = MutableStateFlow(_navAnimeIdFlow.value)

    private val _animeDetailLDSF = MutableStateFlow(LazyData.init<AnimeDetailPageModel>())
    val animeDetailLDSF: StateFlow<LazyData<AnimeDetailPageModel>> = _animeDetailLDSF

    val danSearchTitleSF: MutableStateFlow<String?> = MutableStateFlow(null)

    private val _danSearchAnimeListLDSF = MutableStateFlow(LazyData.init<List<DanSearchAnime>>())
    val danSearchAnimeListLDSF: StateFlow<LazyData<List<DanSearchAnime>>> = _danSearchAnimeListLDSF

    private val _danAnimeInfoLDSF = MutableStateFlow(LazyData.init<DanAnimeInfo>())
    val danAnimeInfoLDSF: StateFlow<LazyData<DanAnimeInfo>> = _danAnimeInfoLDSF

    private val _favoriteRefreshSF = MutableStateFlow(0)
    val favoriteModelSF = animeDetailLDSF.combine(_favoriteRefreshSF) { animeDetailLD, _ ->
        if (animeDetailLD.type == LazyType.SUCCESS) {
            animeDetailLD.data?.video?.id?.let {
                favoriteAnimeDao.getById(it)
            }
        } else null
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private val _watchedEpisodeTitleSetRefreshSF = MutableStateFlow(0)
    val watchedEpisodeTitleMapSF =
        animeDetailLDSF.combine(_watchedEpisodeTitleSetRefreshSF) { animeDetailLD, _ ->
            (if (animeDetailLD.type == LazyType.SUCCESS) {
                animeDetailLD.data?.video?.id?.let {
                    episodeProgressDao.getListByAid(it)
                }
            } else null)?.associateBy({ it.title }, { it }) ?: emptyMap()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    private fun animeDetail(aid: Int) {
        viewModelScope.launch {
            _animeDetailLDSF.value = LazyData.init()
            _animeDetailLDSF.value = withContext(Dispatchers.IO) {
                fetchAnimeDetail(aid)
            }
        }
    }

    fun danBangumi(animeId: Int) {
        viewModelScope.launch {
            _danAnimeInfoLDSF.value = LazyData.init()
            _danAnimeInfoLDSF.value = withContext(Dispatchers.IO) {
                fetchDanBangumi(animeId)
            }
        }
    }

    private suspend fun fetchAnimeDetail(aid: Int): LazyData<AnimeDetailPageModel> {
        return try {
            LazyData.success(repo.detail(aid))
        } catch (t: Throwable) {
            LogUtil.fb(t)
            LazyData.fail(t)
        }
    }

    private suspend fun fetchSearchDanAnime(title: String): LazyData<List<DanSearchAnime>> {
        return try {
            val resp = repo.danDanPlaySearchAnime(title)
            if (resp.errorCode != SUCCESS_CODE) {
                throw DataRequestException(resp.errorMessage)
            }
            LazyData.success(resp.animes)
        } catch (t: Throwable) {
            LogUtil.fb(t)
            LazyData.fail(t)
        }
    }

    private suspend fun fetchDanBangumi(animeId: Int): LazyData<DanAnimeInfo> {
        return try {
            val resp = repo.danDanPlayGetAnime(animeId)
            if (resp.errorCode != SUCCESS_CODE) {
                throw DataRequestException(resp.errorMessage)
            }
            LazyData.success(resp.bangumi!!)
        } catch (t: Throwable) {
            LogUtil.fb(t)
            LazyData.fail(t)
        }
    }

    fun favorite(model: FavoriteAnimeModel, favorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (favorite) {
                favoriteAnimeDao.insertAll(model)
            } else {
                favoriteAnimeDao.delete(model)
            }
            _favoriteRefreshSF.update {
                it + 1
            }
        }
    }

    fun refreshWatchedEpisodeTitleSet() {
        _watchedEpisodeTitleSetRefreshSF.update {
            it + 1
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

    init {
        viewModelScope.launch {
            _navAnimeIdFlow.collectLatest { navAnimeId ->
                animeIdSF.value = navAnimeId
            }
        }

        viewModelScope.launch {
            animeIdSF.collectLatest {
                val aid = it.toInt()
                animeDetail(aid)
            }
        }

        viewModelScope.launch {
            _animeDetailLDSF.collectLatest {
                if (it.type == LazyType.SUCCESS) {
                    danSearchTitleSF.value = it.data?.video?.name

                }
            }
        }

        viewModelScope.launch {
            danSearchTitleSF.collectLatest {
                if (!it.isNullOrBlank()) {
                    _danSearchAnimeListLDSF.value = LazyData.init()
                    _danSearchAnimeListLDSF.value = fetchSearchDanAnime(it)
                }
            }
        }

        viewModelScope.launch {
            _danSearchAnimeListLDSF.collectLatest {
                if (it.type == LazyType.SUCCESS && it.data != null) {
                    if (it.data.isNotEmpty()) {
                        danBangumi(it.data[0].animeId)
                    } else {
                        _danAnimeInfoLDSF.value = LazyData(type = LazyType.SUCCESS)
                    }
                }
            }
        }
    }

    companion object {
        private const val ANIME_ID_SAVED_STATE_KEY = "animeId"

        const val SUCCESS_CODE = 0
    }
}