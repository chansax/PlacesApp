package com.chansax.places.data.favorite.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *  Created by chandan on 5/16/21
 **/
@Entity(tableName = "favorite_place")
data class FavoritePlace(
    @PrimaryKey
    @ColumnInfo(name = "place_id")
    val placeId: String,

    @ColumnInfo(name = "favorite")
    val isFavorite: Int
)
