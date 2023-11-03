package com.muedsa.agetv.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muedsa.agetv.model.LazyData
import com.muedsa.agetv.model.age.AgeCatalogOption
import com.muedsa.agetv.model.age.RankAnimeModel
import com.muedsa.agetv.repository.AppRepository
import com.muedsa.uitl.LogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RankViewModel @Inject constructor(
    private val repo: AppRepository
) : ViewModel() {

    val selectedYear = mutableStateOf<AgeCatalogOption>(AgeCatalogOption.Years[0])

    val rankLDState = mutableStateOf<LazyData<List<List<RankAnimeModel>>>>(LazyData.init())

    fun fetchRank() {
        viewModelScope.launch(context = Dispatchers.IO) {
            try {
                rankLDState.value =
                    LazyData.success(repo.rank(year = selectedYear.value.value).rank)
            } catch (t: Throwable) {
                withContext(Dispatchers.Main) {
                    rankLDState.value = LazyData.fail(t)
                }
                LogUtil.fb(t)
            }
        }
    }
}