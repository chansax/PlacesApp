package com.chansax.places.features.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chansax.places.BuildConfig
import com.chansax.places.R
import com.chansax.places.data.search.model.Business
import com.chansax.places.data.search.model.BusinessDiffCallback
import com.chansax.places.databinding.ItemBusinessBinding
import com.chansax.places.databinding.ItemListingBinding
import com.chansax.places.util.FavoriteStatus
import com.chansax.places.util.nextStatus
import com.chansax.places.util.toBoolean
import com.chansax.places.util.toFavoriteStatus

/**
 *  Created by chandan on 5/2/21
 **/
class SearchAdapter(val listener: FavoriteCallback) :
    ListAdapter<Business, SearchAdapter.UserViewHolder>(BusinessDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding =
            ItemListingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    inner class UserViewHolder(private val binding: ItemListingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(business: Business) {
            binding.apply {
                business.photos?.get(0)?.photo_reference?.let { reference ->
                    val imageUrl =
                        "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=${reference}&key=${BuildConfig.GOOGLE_PLACES_API_KEY}"
                    Glide.with(imageview).load(imageUrl).into(imageview)
                }

                placeName.text = business.name
                business.rating?.let {
                    ratings.rating = it.toFloat()
                }

                business.user_ratings_total?.let {
                    numRatings.text = "($it)"
                }

                favorite.apply {
                    if (business.isFavorite == null) {
                        business.isFavorite = checkFavorite(business.place_id)
                    }
                    handleFavorite(business.isFavorite)
                    setOnClickListener {
                        business.isFavorite = business.isFavorite.nextStatus()
                        handleFavorite(business.isFavorite)
                        business.place_id?.let {
                            listener.updateFavorite(
                                business.place_id,
                                business.isFavorite.toBoolean()
                            )
                        }
                    }
                }
            }
        }

        private fun handleFavorite(status: FavoriteStatus) {

            val res = when (status) {
                FavoriteStatus.Favoured -> R.drawable.ic_favoriate
                else -> R.drawable.ic_favoriate_gray
            }

            binding.favorite.setImageResource(res)
        }

        private fun checkFavorite(placeId: String?): FavoriteStatus {
            placeId?.let {
                listener.isFavorite(it)?.let { retVal ->
                    return retVal.toFavoriteStatus()
                }
            }

            return FavoriteStatus.Uninitialized
        }
    }

    interface FavoriteCallback {
        fun updateFavorite(id: String, isFav: Boolean)
        fun isFavorite(placeId: String): Boolean?
    }
}
