package com.muedsa.agetv.screens.home.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muedsa.agetv.model.LazyData
import com.muedsa.agetv.model.age.HomeModel
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
class MainScreenViewModel @Inject constructor(
    private val repo: AppRepository
) : ViewModel() {

    private val _homeDataSF = MutableStateFlow(LazyData.init<HomeModel>())
    val homeDataSF: StateFlow<LazyData<HomeModel>> = _homeDataSF

    fun refreshHomeData() {
        viewModelScope.launch {
            _homeDataSF.value = withContext(Dispatchers.IO) {
                fetchHomeData()
            }
        }
    }

    private suspend fun fetchHomeData(): LazyData<HomeModel> {
        return try {
            LazyData.success(repo.home())
        } catch (t: Throwable) {
            LogUtil.fb(t)
            LazyData.fail(t)
        }
    }

    init {
        refreshHomeData()
    }
}