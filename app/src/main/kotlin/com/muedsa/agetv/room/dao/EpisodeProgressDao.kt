package com.muedsa.agetv.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.muedsa.agetv.room.model.EpisodeProgressModel

@Dao
interface EpisodeProgressDao {

    @Query("SELECT * FROM episode_progress WHERE aid = :aid")
    suspend fun getListByAid(aid: Int): List<EpisodeProgressModel>

    @Query("SELECT * FROM episode_progress WHERE aid = :aid and title_hash = :titleHash")
    suspend fun getOneByAidAndTitleHash(aid: Int, titleHash: Int): EpisodeProgressModel?

    @Upsert
    suspend fun upsert(model: EpisodeProgressModel)

    @Query("DELETE FROM episode_progress WHERE aid = :aid")
    suspend fun deleteByAid(aid: Int)
}