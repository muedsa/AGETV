package com.muedsa.agetv.model

data class LazyPagedList<T>(
    val list: MutableList<T> = mutableListOf(),
    val page: Int = 0,
    val totalPage: Int = 0,
    val offset: Int = 0,
    val type: LazyType = LazyType.LOADING,
    val error: Throwable? = null
) {
    val nextPage get() = page + 1
    val hasNext get() = page == 0 || page < totalPage

    fun loadingNext() = LazyPagedList(
        list = list,
        page = page,
        totalPage = totalPage,
        type = LazyType.LOADING
    )

    fun successNext(appendList: List<T>, totalPage: Int) = LazyPagedList(
        offset = list.size,
        list = list.also { it.addAll(appendList) },
        page = nextPage,
        totalPage = totalPage,
        type = LazyType.SUCCESS
    )

    fun failNext(error: Throwable?) = LazyPagedList(
        list = list,
        page = page,
        totalPage = totalPage,
        type = LazyType.FAILURE,
        error = error
    )

    companion object {
        @JvmStatic
        fun <T> new(): LazyPagedList<T> = LazyPagedList(type = LazyType.SUCCESS)
    }
}