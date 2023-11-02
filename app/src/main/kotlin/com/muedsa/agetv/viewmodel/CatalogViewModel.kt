package com.muedsa.agetv.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muedsa.agetv.model.LazyPagedList
import com.muedsa.agetv.model.age.AgeCatalogOption
import com.muedsa.agetv.model.age.CatalogAnimeModel
import com.muedsa.agetv.repository.AppRepository
import com.muedsa.uitl.LogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.ceil

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val repo: AppRepository
) : ViewModel() {

    val orderState = mutableStateOf(AgeCatalogOption.Order[0])
    val regionState = mutableStateOf(AgeCatalogOption.Regions[0])
    val genreState = mutableStateOf(AgeCatalogOption.Genres[0])
    val yearState = mutableStateOf(AgeCatalogOption.Years[0])
    val seasonState = mutableStateOf(AgeCatalogOption.Seasons[0])
    val statusState = mutableStateOf(AgeCatalogOption.Status[0])
    val labelState = mutableStateOf(AgeCatalogOption.Labels[0])
    val resourceState = mutableStateOf(AgeCatalogOption.Resources[0])

    val animeLPState = mutableStateOf(LazyPagedList.new<CatalogAnimeModel>())

    fun fetchAnimeCatalog() {
        animeLPState.value = animeLPState.value.loadingNext()
        viewModelScope.launch(context = Dispatchers.IO) {
            try {
                repo.catalog(
                    genre = genreState.value.value,
                    label = labelState.value.value,
                    order = orderState.value.value,
                    region = regionState.value.value,
                    resource = regionState.value.value,
                    season = seasonState.value.value,
                    status = statusState.value.value,
                    year = yearState.value.value,
                    page = animeLPState.value.nextPage,
                    size = PAGE_SIZE
                ).let {
                    animeLPState.value = animeLPState.value.successNext(
                        it.videos,
                        ceil(it.total.toDouble() / PAGE_SIZE).toInt()
                    )
                }
            } catch (t: Throwable) {
                withContext(Dispatchers.Main) {
                    animeLPState.value = animeLPState.value.failNext(t)
                }
                LogUtil.fb(t)
            }
        }
    }

    fun resetCatalogOptions() {
        orderState.value = AgeCatalogOption.Order[0]
        regionState.value = AgeCatalogOption.Regions[0]
        genreState.value = AgeCatalogOption.Genres[0]
        yearState.value = AgeCatalogOption.Years[0]
        seasonState.value = AgeCatalogOption.Seasons[0]
        statusState.value = AgeCatalogOption.Status[0]
        labelState.value = AgeCatalogOption.Labels[0]
        resourceState.value = AgeCatalogOption.Resources[0]
    }

//    init {
//        viewModelScope.launch {
//            listOf(
//                orderState,
//                regionState,
//                genreState,
//                yearState,
//                seasonState,
//                statusState,
//                labelState,
//                resourceState
//            ).forEach {
//                snapshotFlow { it }.collect {
//                    animeLPState.value = animeLPState.value.needNew()
//                }
//            }
//        }
//    }

    companion object {
        const val PAGE_SIZE = 20
    }
}