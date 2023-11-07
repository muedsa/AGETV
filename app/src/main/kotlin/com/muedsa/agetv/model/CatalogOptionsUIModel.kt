package com.muedsa.agetv.model

import com.muedsa.agetv.model.age.AgeCatalogOption

data class CatalogOptionsUIModel(
    val order: AgeCatalogOption = AgeCatalogOption.Order[0],
    val region: AgeCatalogOption = AgeCatalogOption.Regions[0],
    val genre: AgeCatalogOption = AgeCatalogOption.Genres[0],
    val year: AgeCatalogOption = AgeCatalogOption.Years[0],
    val season: AgeCatalogOption = AgeCatalogOption.Seasons[0],
    val status: AgeCatalogOption = AgeCatalogOption.Status[0],
    val label: AgeCatalogOption = AgeCatalogOption.Labels[0],
    val resource: AgeCatalogOption = AgeCatalogOption.Resources[0],
) {

    fun default(): CatalogOptionsUIModel {
        return if (this == Default) {
            this
        } else {
            Default
        }
    }

    companion object {
        val Default = CatalogOptionsUIModel()
    }
}