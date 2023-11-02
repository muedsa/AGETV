package com.muedsa.agetv.viewmodel

import androidx.compose.runtime.mutableStateOf
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repo: AppRepository
) : ViewModel() {

    val searchTextState = mutableStateOf("")

    val searchAnimeLPState = mutableStateOf(LazyPagedList.new<CatalogAnimeModel>())

    fun fetchSearchAnime() {
        searchAnimeLPState.value = searchAnimeLPState.value.loadingNext()
        viewModelScope.launch(context = Dispatchers.IO) {
            try {
                repo.search(
                    query = searchTextState.value,
                    page = searchAnimeLPState.value.nextPage
                ).let {
                    if (it.code == AgePlayerService.SUCCESS_CODE) {
                        if (it.data != null) {
                            searchAnimeLPState.value = searchAnimeLPState.value.successNext(
                                it.data.videos,
                                it.data.totalPage
                            )
                        }
                    } else {
                        throw DataRequestException(it.message ?: "age request error");
                    }
                }
            } catch (t: Throwable) {
                withContext(Dispatchers.Main) {
                    searchAnimeLPState.value = searchAnimeLPState.value.failNext(t)
                }
                LogUtil.fb(t)
            }
        }
    }
}