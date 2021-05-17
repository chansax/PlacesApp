package com.chansax.places.data.search.model

import androidx.recyclerview.widget.DiffUtil
import com.chansax.places.util.FavoriteStatus
import com.google.gson.annotations.SerializedName

/**
 *  Created by chandan on 5/2/21
 **/


data class NearbySearchResp(
    val results: List<Business>?,
    val status: String?
)

data class Business(
    @SerializedName("business_status")
    val businessStatus: String?,
    val geometry: BusinessGeometry,
    val icon: String?,
    val name: String?,
    val opening_hours: BusinessHours,
    val place_id: String?,
    val price_level: Int?,
    val rating: Double?,
    val types: List<String>?,
    val user_ratings_total: Int?,
    val photos: List<BusinessPhotos>?,
    var isFavorite: FavoriteStatus = FavoriteStatus.Uninitialized
)

data class BusinessGeometry(
    val location: BusinessLocation,
    val viewPort: ViewPort
)

data class BusinessLocation(
    val lat: Double,
    val lng: Double
)

data class ViewPort(
    val northeast: BusinessLocation,
    val southwest: BusinessLocation
)

data class BusinessHours(
    val open_now: Boolean
)

data class BusinessPhotos(
    val height: Int?,
    val width: Int?,
    val photo_reference: String?
)

object BusinessDiffCallback : DiffUtil.ItemCallback<Business>() {
    override fun areItemsTheSame(oldItem: Business, newItem: Business): Boolean =
        oldItem.place_id == newItem.place_id


    override fun areContentsTheSame(oldItem: Business, newItem: Business): Boolean =
        oldItem.name == newItem.name
                && oldItem.place_id == newItem.place_id
}
