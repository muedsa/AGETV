package com.muedsa.agetv.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muedsa.agetv.exception.DataRequestException
import com.muedsa.agetv.model.LazyPagedList
import com.muedsa.agetv.model.age.CatalogAnimeModel
import com.muedsa.agetv.repository.AppRepository
import com.muedsa.agetv.service.AgePlayerService
import com.muedsa.uitl.LogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repo: AppRepository
) : ViewModel() {

    val searchTextSF = MutableStateFlow("")
    private val _searchAnimeLPSF =
        MutableStateFlow(LazyPagedList.new<String, CatalogAnimeModel>(searchTextSF.value))
    val searchAnimeLPSF: StateFlow<LazyPagedList<String, CatalogAnimeModel>> = _searchAnimeLPSF

    fun searchAnime(lp: LazyPagedList<String, CatalogAnimeModel>) {
        if (lp.query.isNotBlank()) {
            viewModelScope.launch {
                val loadingLP = lp.loadingNext()
                _searchAnimeLPSF.value = loadingLP
                _searchAnimeLPSF.value = withContext(Dispatchers.IO) {
                    fetchSearch(loadingLP)
                }
            }
        }
    }


    private suspend fun fetchSearch(
        lp: LazyPagedList<String, CatalogAnimeModel>
    ): LazyPagedList<String, CatalogAnimeModel> {
        return try {
            repo.search(
                query = lp.query,
                page = lp.nextPage
            ).let {
                if (it.code != AgePlayerService.SUCCESS_CODE) {
                    throw DataRequestException(it.message ?: "age request error")
                }
                if (it.data == null) {
                    throw DataRequestException("response data is null")
                }
                lp.successNext(it.data.videos, it.data.totalPage)
            }
        } catch (t: Throwable) {
            LogUtil.fb(t)
            lp.failNext(t)
        }
    }
}