package com.chansax.places.data.favorite.cache

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.chansax.places.data.favorite.model.FavoritePlace

/**
 *  Created by chandan on 5/16/21
 **/
@Database(entities = [FavoritePlace::class], version = 1, exportSchema = false)
abstract class FavPlaceDb : RoomDatabase() {
    abstract fun favPlaceDao(): FavPlaceDao
}
