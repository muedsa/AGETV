package com.muedsa.agetv.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muedsa.agetv.model.LazyPagedList
import com.muedsa.agetv.model.age.PosterAnimeModel
import com.muedsa.agetv.repository.AppRepository
import com.muedsa.uitl.LogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.ceil

@HiltViewModel
class LatestUpdateViewModel @Inject constructor(
    private val repo: AppRepository
) : ViewModel() {

    private val _latestUpdateLPSF =
        MutableStateFlow(LazyPagedList.new<Unit, PosterAnimeModel>(Unit))
    val latestUpdateLPSF: StateFlow<LazyPagedList<Unit, PosterAnimeModel>> = _latestUpdateLPSF

    fun latestUpdate() {
        viewModelScope.launch {
            val lp = _latestUpdateLPSF.value.loadingNext()
            _latestUpdateLPSF.value = lp
            _latestUpdateLPSF.value = withContext(Dispatchers.IO) {
                fetchLatestUpdate(lp)
            }
        }

    }

    private suspend fun fetchLatestUpdate(
        lp: LazyPagedList<Unit, PosterAnimeModel>
    ): LazyPagedList<Unit, PosterAnimeModel> {
        return try {
            return repo.update(lp.nextPage, PAGE_SIZE).let {
                lp.successNext(
                    it.videos,
                    ceil(it.total.toDouble() / CatalogViewModel.PAGE_SIZE).toInt()
                )
            }
        } catch (t: Throwable) {
            LogUtil.fb(t)
            lp.failNext(t)
        }
    }

    init {
        latestUpdate()
    }

    companion object {
        const val PAGE_SIZE = 30
    }
}