package com.muedsa.agetv.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_anime")
data class FavoriteAnimeModel(
    @PrimaryKey @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "cover") val cover: String,
    @ColumnInfo(
        name = "update_at",
        defaultValue = "(CURRENT_TIMESTAMP)",
        index = true
    ) var updateAt: Long = 0
)
