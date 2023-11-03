package com.muedsa.agetv.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muedsa.agetv.model.LazyPagedList
import com.muedsa.agetv.model.age.PosterAnimeModel
import com.muedsa.agetv.repository.AppRepository
import com.muedsa.uitl.LogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.ceil

@HiltViewModel
class LatestUpdateViewModel @Inject constructor(
    private val repo: AppRepository
) : ViewModel() {

    val latestUpdateLPState = mutableStateOf<LazyPagedList<PosterAnimeModel>>(LazyPagedList.new())

    fun fetchLatestUpdate() {
        val nextPage = latestUpdateLPState.value.nextPage
        latestUpdateLPState.value = latestUpdateLPState.value.loadingNext()
        viewModelScope.launch(context = Dispatchers.IO) {
            try {
                repo.update(nextPage, PAGE_SIZE).let {
                    latestUpdateLPState.value = latestUpdateLPState.value.successNext(
                        it.videos,
                        ceil(it.total.toDouble() / CatalogViewModel.PAGE_SIZE).toInt()
                    )
                }
            } catch (t: Throwable) {
                withContext(Dispatchers.Main) {
                    latestUpdateLPState.value = latestUpdateLPState.value.failNext(t)
                }
                LogUtil.fb(t)
            }
        }
    }

    companion object {
        const val PAGE_SIZE = 30
    }
}