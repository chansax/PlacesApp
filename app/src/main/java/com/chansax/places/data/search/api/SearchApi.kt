package com.chansax.places.data.search.api

import com.chansax.places.data.search.model.NearbySearchResp
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

/**
 *  Created by chandan on 5/2/21
 **/
interface SearchApi {
    companion object {
        const val PLACES_URL = "https://maps.googleapis.com/maps/api/place/"
    }

    @GET("nearbysearch/json")
    suspend fun nearBy(@QueryMap query: Map<String, String>): Response<NearbySearchResp>

}
