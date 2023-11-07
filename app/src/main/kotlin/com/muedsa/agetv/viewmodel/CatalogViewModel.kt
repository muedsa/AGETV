package com.muedsa.agetv.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muedsa.agetv.model.CatalogOptionsUIModel
import com.muedsa.agetv.model.LazyPagedList
import com.muedsa.agetv.model.age.CatalogAnimeModel
import com.muedsa.agetv.repository.AppRepository
import com.muedsa.uitl.LogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.ceil

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val repo: AppRepository
) : ViewModel() {

    val querySF = MutableStateFlow(CatalogOptionsUIModel())

    private val _animeLPSF =
        MutableStateFlow(LazyPagedList.new<CatalogOptionsUIModel, CatalogAnimeModel>(querySF.value))
    val animeLPSF: StateFlow<LazyPagedList<CatalogOptionsUIModel, CatalogAnimeModel>> = _animeLPSF

    fun catalog(lp: LazyPagedList<CatalogOptionsUIModel, CatalogAnimeModel>) {
        viewModelScope.launch {
            val loadingLP = lp.loadingNext()
            _animeLPSF.value = loadingLP
            _animeLPSF.value = withContext(Dispatchers.IO) {
                fetchCatalog(loadingLP)
            }
        }
    }

    private suspend fun fetchCatalog(
        lp: LazyPagedList<CatalogOptionsUIModel, CatalogAnimeModel>
    ): LazyPagedList<CatalogOptionsUIModel, CatalogAnimeModel> {
        return try {
            repo.catalog(
                genre = lp.query.genre.value,
                label = lp.query.label.value,
                order = lp.query.order.value,
                region = lp.query.region.value,
                resource = lp.query.resource.value,
                season = lp.query.season.value,
                status = lp.query.status.value,
                year = lp.query.year.value,
                page = lp.nextPage,
                size = PAGE_SIZE
            ).let {
                lp.successNext(it.videos, ceil(it.total.toDouble() / PAGE_SIZE).toInt())
            }
        } catch (t: Throwable) {
            LogUtil.fb(t)
            lp.failNext(t)
        }
    }

    fun resetCatalogOptions() {
        querySF.update { it.default() }
    }

    init {
        viewModelScope.launch {
            querySF.collectLatest {
                catalog(LazyPagedList.new(it))
            }
        }
    }

    companion object {
        const val PAGE_SIZE = 20
    }
}