package com.chansax.places.data.favorite.cache

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chansax.places.data.favorite.model.FavoritePlace

/**
 *  Created by chandan on 5/16/21
 **/

@Dao
interface FavPlaceDao {
    @Query("SELECT * FROM favorite_place WHERE place_id = :placeId LIMIT 1")
    suspend fun favoritePlace(placeId: String): FavoritePlace?

    @Query("SELECT * FROM favorite_place WHERE place_id IN(:placeIds)")
    suspend fun favoritePlaces(placeIds: Array<String>): List<FavoritePlace>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(users: FavoritePlace)

    @Query("DELETE FROM favorite_place")
    suspend fun clear()
}
