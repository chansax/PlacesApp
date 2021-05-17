package com.chansax.places.di

import android.app.Application
import androidx.room.Room
import com.chansax.places.data.favorite.cache.FavPlaceDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 *  Created by chandan on 5/16/21
 **/
@Module
@InstallIn(SingletonComponent::class)
object CacheModule {

    @Provides
    @Singleton
    fun provideFavPlaceDb(app: Application): FavPlaceDb =
        Room.databaseBuilder(app, FavPlaceDb::class.java, "favorite_place_db")
            .build()

}
