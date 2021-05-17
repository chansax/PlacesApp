package com.chansax.places.data.search

import com.chansax.places.BuildConfig
import com.chansax.places.data.favorite.FavoritesRepository
import com.chansax.places.data.search.api.SearchApi
import com.chansax.places.data.search.model.Business
import com.chansax.places.data.search.model.NearbySearchResp
import com.chansax.places.util.jsonResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

/**
 *  Created by chandan on 5/2/21
 **/
class SearchRepository @Inject constructor(
    private val api: SearchApi,
    private val favRepo: FavoritesRepository
) {
    suspend fun executeSearch(location: String, keyword: String? = null): List<Business>? {
        val queryMap = createQuery(location, keyword)
        val response = api.nearBy(queryMap)
        return response.body()?.results
    }

    private fun createQuery(location: String, keyword: String? = null): Map<String, String> {
        val queryMap = mutableMapOf<String, String>()
        queryMap["location"] = location
        queryMap["radius"] = "1500"
        queryMap["type"] = "restaurant"
        keyword?.let { queryMap["keyword"] = it }
        queryMap["key"] = BuildConfig.GOOGLE_PLACES_API_KEY
        return queryMap
    }

    fun inflateResponse(): List<Business>? {
        val sType = object : TypeToken<List<Business>>() {}.type
        val abc = Gson().fromJson<NearbySearchResp>(jsonResponse, NearbySearchResp::class.java)
        return abc?.results
    }
}
