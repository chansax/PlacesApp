package com.chansax.places.data.favorite

import com.chansax.places.data.favorite.model.FavoritePlace
import com.chansax.places.data.favorite.cache.FavPlaceDb
import com.chansax.places.data.search.model.Business
import com.chansax.places.util.toBoolean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 *  Created by chandan on 5/16/21
 **/
class FavoritesRepository @Inject constructor(
    db: FavPlaceDb
) {
    private val favPlaceDao = db.favPlaceDao()

    suspend fun setFavorite(value: FavoritePlace) {
        GlobalScope.launch(Dispatchers.IO) {
            favPlaceDao.insert(value)
        }
    }

    suspend fun checkFavorite(place: String): FavoritePlace? {
            return favPlaceDao.favoritePlace(place)
    }

    suspend fun checkFavorites(places: Array<String>): List<FavoritePlace>? {
        return favPlaceDao.favoritePlaces(places)
    }

    suspend fun checkFavorites(business: List<Business>): Map<String, Boolean>? {
        val placeIds = business.mapNotNull { it.place_id }.toTypedArray()
        return placeIds.isNotEmpty().let {
            checkFavorites(placeIds)?.let { placeList ->
                placeList.associateBy(
                    keySelector = { it.placeId },
                    valueTransform = { it.isFavorite.toBoolean() })
            }
        }
    }
}
