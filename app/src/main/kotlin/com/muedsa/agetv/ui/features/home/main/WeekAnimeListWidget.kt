package com.muedsa.agetv.ui.features.home.main

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.muedsa.agetv.model.age.WeekAnimeModel
import com.muedsa.agetv.ui.WeekCardColorList
import com.muedsa.compose.tv.model.ContentModel
import com.muedsa.compose.tv.widget.ImageCardsRow


val WeekKeyList = listOf("1", "2", "3", "4", "5", "6", "0")
val WeekNameList = listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")

@Composable
fun WeekAnimeListWidget(
    model: Map<String, List<WeekAnimeModel>>,
    onClickAnime: (Int, Int, WeekAnimeModel) -> Unit = { _, _, _ -> }
) {
    Column {
        WeekKeyList.forEachIndexed { index, key ->
            val dayList = model[key] ?: emptyList()

            ImageCardsRow(
                title = "每周放送 · ${WeekNameList[index]}",
                modelList = dayList,
                imageFn = { _, _ -> "" },
                backgroundColorFn = { itemIndex, _ -> WeekCardColorList[(index + itemIndex) % WeekCardColorList.size] },
                contentFn = { _, item ->
                    ContentModel(
                        title = item.name,
                        subtitle = item.nameForNew,
                        description = if (item.isNew == 1) "\uD83D\uDCA5 New" else null
                    )
                },
                onItemClick = { itemIndex, item ->
                    onClickAnime(index, itemIndex, item)
                }
            )
        }
    }
}