package com.muedsa.agetv.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.muedsa.agetv.room.dao.EpisodeProgressDao
import com.muedsa.agetv.room.dao.FavoriteAnimeDao
import com.muedsa.agetv.room.model.EpisodeProgressModel
import com.muedsa.agetv.room.model.FavoriteAnimeModel

@Database(entities = [FavoriteAnimeModel::class, EpisodeProgressModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteAnimeDao(): FavoriteAnimeDao

    abstract fun episodeProgressDao(): EpisodeProgressDao
}