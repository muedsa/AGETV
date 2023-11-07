package com.muedsa.agetv.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muedsa.agetv.model.LazyData
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

@HiltViewModel
class RecommendViewModel @Inject constructor(
    private val repo: AppRepository
) : ViewModel() {

    private val _recommendLDSF = MutableStateFlow(LazyData.init<List<PosterAnimeModel>>())
    val recommendLDSF: StateFlow<LazyData<List<PosterAnimeModel>>> = _recommendLDSF

    fun refreshRecommend() {
        viewModelScope.launch {
            _recommendLDSF.value = LazyData.init()
            _recommendLDSF.value = withContext(Dispatchers.IO) {
                fetchRecommend()
            }
        }
    }

    private suspend fun fetchRecommend(): LazyData<List<PosterAnimeModel>> {
        return try {
            LazyData.success(repo.recommend().videos)
        } catch (t: Throwable) {
            LogUtil.fb(t)
            LazyData.fail(t)
        }
    }

    init {
        refreshRecommend()
    }
}