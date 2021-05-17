package com.chansax.places.features.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chansax.places.data.favorite.FavoritesRepository
import com.chansax.places.data.favorite.model.FavoritePlace
import com.chansax.places.data.search.SearchRepository
import com.chansax.places.data.search.model.Business
import com.chansax.places.util.toInt
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 *  Created by chandan on 5/3/21
 **/
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: SearchRepository,
    private val favRepo: FavoritesRepository
) : ViewModel() {

    private val _business = MutableLiveData<List<Business>>()
    val business = _business as LiveData<List<Business>>

    var location = MutableLiveData<LatLng?>()
    var keyword = MutableLiveData<String>()

    private val favoritesMap = mutableMapOf<String, Boolean>()

    fun search(keyword: String? = null) {
        keyword?.let {
            this.keyword.value = it
        }

        val location = location.value?.let { latlng ->
            "${latlng.latitude}, ${latlng.longitude}"
        }

        location?.let {
            viewModelScope.launch {
                val businessList = repository.executeSearch(it, keyword)
                businessList?.let {
                    favRepo.checkFavorites(it)?.let { favMap ->
                        favoritesMap.putAll(favMap)
                    }
                    _business.value = it
                }
            }
        }
    }

    fun mockSearch() {
        viewModelScope.launch {
            val businessList = repository.inflateResponse()
            businessList.let {
                businessList?.let {
                    favRepo.checkFavorites(it)?.let { favMap ->
                        favoritesMap.putAll(favMap)
                    }
                    _business.value = it
                }
            }
        }
    }

    fun updateFavorite(id: String, fav: Boolean) {
        viewModelScope.launch {
            favoritesMap[id] = fav
            favRepo.setFavorite(FavoritePlace(placeId = id, isFavorite = fav.toInt()))
        }
    }

    fun isFavorite(place: String): Boolean? {
        return favoritesMap[place]
    }

}
