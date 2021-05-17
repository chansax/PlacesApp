package com.chansax.places.features.map

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.chansax.places.R
import com.chansax.places.databinding.FragmentMapsBinding
import com.chansax.places.features.viewmodel.SearchViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import androidx.lifecycle.observe
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.chansax.places.BuildConfig
import com.chansax.places.data.search.model.Business
import com.chansax.places.databinding.ItemListingBinding
import com.chansax.places.util.*
import com.google.android.gms.maps.model.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapsFragment : Fragment() {

    private val viewModel: SearchViewModel by activityViewModels()
    private lateinit var binding: FragmentMapsBinding
    private var map: GoogleMap? = null
    private var lastSelectedMarker: Marker? = null

    private val activePinIcon: BitmapDescriptor by lazy {
        BitmapHelper.vectorToBitmap(requireContext(), R.drawable.ic_selected_pin)
    }

    private val inactivePinIcon: BitmapDescriptor by lazy {
        BitmapHelper.vectorToBitmap(requireContext(), R.drawable.ic_non_selected_pin)
    }

    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap

        map?.apply {
            uiSettings.isZoomControlsEnabled = false
            setInfoWindowAdapter(CustomInfoWindowAdapter())
//            isMyLocationEnabled = true
            setOnMarkerClickListener { marker ->
                lastSelectedMarker?.let {
                    it.setIcon(inactivePinIcon)
                }
                lastSelectedMarker = marker
                marker.apply {
                    setIcon(activePinIcon)
                }
                val business = marker.tag
                return@setOnMarkerClickListener false
            }
        }
        handleLocation()
    }

    companion object {
        fun newInstance() = MapsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMap()
        viewModel.location.observe(viewLifecycleOwner) {
            handleLocation()
        }

        viewModel.business.observe(viewLifecycleOwner) {
            displayResults()
        }

        binding.buttonList.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_map_to_list);
        }
    }

    private fun displayResults() {
        viewModel.business.value?.let {
            val boundsBuilder = LatLngBounds.builder()
            it.forEach { business ->
                val latLng = LatLng(
                    business.geometry.location.lat,
                    business.geometry.location.lng
                )
                boundsBuilder.include(latLng)
                val marker = map?.addMarker(
                    MarkerOptions()
                        .title(business.name)
                        .position(latLng)
                        .icon(inactivePinIcon)
                        .infoWindowAnchor(0.5f, 0.5f)
                        .draggable(false)
                )
                marker?.tag = business
            }
            map?.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 20))
        }
    }

    private fun setupMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun handleLocation() {
        viewModel.location.value?.let {
            val currentLocation = LatLng(it.latitude, it.longitude)
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14.5F))
            when (viewModel.business.value) {
                null -> viewModel.search()
//                null -> viewModel.mockSearch()
                else -> displayResults()
            }
        }
    }

    internal inner class CustomInfoWindowAdapter : GoogleMap.InfoWindowAdapter {

        private val binding =
            ItemListingBinding.inflate(layoutInflater, null, false)

        override fun getInfoWindow(marker: Marker): View? {
            render(marker, binding.root)
            return binding.root
        }

        override fun getInfoContents(marker: Marker): View? {
            return null
        }

        private fun render(marker: Marker, view: View) {

            val business = marker.tag as Business

            binding.apply {
                business.photos?.get(0)?.photo_reference?.let { reference ->
                    val imageUrl =
                        "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=${reference}&key=${BuildConfig.GOOGLE_PLACES_API_KEY}"
                    Glide.with(imageview).load(imageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .fallback(R.drawable.ic_logo)
                        .into(imageview)
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
                            viewModel.updateFavorite(
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
                viewModel.isFavorite(it)?.let { retVal ->
                    return retVal.toFavoriteStatus()
                }
            }

            return FavoriteStatus.Uninitialized
        }
    }
}
