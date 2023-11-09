package com.muedsa.agetv.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.muedsa.agetv.room.model.FavoriteAnimeModel
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteAnimeDao {

//    @Query("SELECT * FROM favorite_anime ORDER BY update_at DESC")
//    suspend fun getAll(): List<FavoriteAnimeModel>

    @Query("SELECT * FROM favorite_anime ORDER BY update_at DESC")
    fun flowAll(): Flow<List<FavoriteAnimeModel>>

    @Query("SELECT * FROM favorite_anime WHERE id = :id")
    suspend fun getById(id: Int): FavoriteAnimeModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg models: FavoriteAnimeModel)

    @Delete
    suspend fun delete(model: FavoriteAnimeModel)
}