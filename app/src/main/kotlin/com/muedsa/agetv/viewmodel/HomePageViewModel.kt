package com.muedsa.agetv.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muedsa.agetv.model.LazyData
import com.muedsa.agetv.model.age.HomeModel
import com.muedsa.agetv.repository.AppRepository
import com.muedsa.uitl.LogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomePageViewModel @Inject constructor(
    private val repo: AppRepository
) : ViewModel() {

    val homeDataState = mutableStateOf<LazyData<HomeModel>>(LazyData.init())

    fun fetchHome() {
        homeDataState.value = LazyData.init()
        viewModelScope.launch(context = Dispatchers.IO) {
            try {
                repo.home().let {
                    homeDataState.value = LazyData.success(it)
                }
            } catch (t: Throwable) {
                withContext(Dispatchers.Main) {
                    homeDataState.value = LazyData.fail(t)
                }
                LogUtil.fb(t)
            }
        }
    }

    init {
        viewModelScope.launch(context = Dispatchers.IO) {
            fetchHome()
        }
    }
}