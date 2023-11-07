package com.muedsa.agetv.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muedsa.agetv.model.LazyData
import com.muedsa.agetv.model.age.AgeCatalogOption
import com.muedsa.agetv.model.age.RankAnimeModel
import com.muedsa.agetv.repository.AppRepository
import com.muedsa.uitl.LogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RankViewModel @Inject constructor(
    private val repo: AppRepository
) : ViewModel() {
    val selectedYearSF = MutableStateFlow(AgeCatalogOption.Years[0])

    private val _rankLDSF = MutableStateFlow(LazyData.init<List<List<RankAnimeModel>>>())
    val rankLDSF: StateFlow<LazyData<List<List<RankAnimeModel>>>> = _rankLDSF

    private suspend fun fetchRank(yearOption: AgeCatalogOption): LazyData<List<List<RankAnimeModel>>> {
        return try {
            LazyData.success(repo.rank(year = yearOption.value).rank)
        } catch (t: Throwable) {
            LogUtil.fb(t)
            LazyData.fail(t)
        }
    }

    init {
        viewModelScope.launch {
            selectedYearSF.collectLatest {
                _rankLDSF.value = LazyData.init()
                _rankLDSF.value = withContext(Dispatchers.IO) {
                    fetchRank(it)
                }
            }
        }
    }
}