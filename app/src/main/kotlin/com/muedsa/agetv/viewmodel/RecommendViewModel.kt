package com.muedsa.agetv.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muedsa.agetv.model.LazyData
import com.muedsa.agetv.model.age.PosterAnimeModel
import com.muedsa.agetv.repository.AppRepository
import com.muedsa.uitl.LogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RecommendViewModel @Inject constructor(
    private val repo: AppRepository
) : ViewModel() {

    val recommendLDState = mutableStateOf<LazyData<List<PosterAnimeModel>>>(LazyData.init())

    fun fetchRecommend() {
        recommendLDState.value = LazyData.init()
        viewModelScope.launch(context = Dispatchers.IO) {
            try {
                repo.recommend().let {
                    recommendLDState.value = LazyData.success(it.videos)
                }
            } catch (t: Throwable) {
                withContext(Dispatchers.Main) {
                    recommendLDState.value = LazyData.fail(t)
                }
                LogUtil.fb(t)
            }
        }
    }
}