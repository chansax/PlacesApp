package com.chansax.places.features

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import com.chansax.places.R
import com.chansax.places.databinding.ActivityMainBinding
import com.chansax.places.features.viewmodel.SearchViewModel
import com.chansax.places.util.REQUEST_LOCATION_PERM
import com.chansax.places.util.REQUIRED_LOC_PERMISSIONS
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.vmadalin.easypermissions.EasyPermissions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupStatusBar()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        assurePermissions()
        setupSearch()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
        assurePermissions()
    }

    private fun setupStatusBar() {
        this.window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            statusBarColor = Color.TRANSPARENT
        }
    }

    private fun search(keyword: String) {
        viewModel.location.observe(this) { location ->
            location?.let {
                viewModel.search(keyword)
            }
        }
    }

    private fun setupSearch() {
        binding.editText.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                val keyword = v.text.toString()
                keyword.isNotBlank().let {
                    viewModel.search(keyword)
                }
                hideKeyboard(v)
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun assurePermissions() {
        if (hasPermissions()) {
            requestLocation()
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.permission_location_rationale_message),
                REQUEST_LOCATION_PERM,
                *REQUIRED_LOC_PERMISSIONS
            )
        }
    }

    private fun hasPermissions(): Boolean {
        return EasyPermissions.hasPermissions(this, *REQUIRED_LOC_PERMISSIONS)
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val locationRequest = LocationRequest.create().apply {
            interval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                val location = locationResult?.locations?.last()
                location?.let {
//                    viewModel.location.value = LatLng(location.latitude, location.longitude)
                    // Inject SFO location
                    // 37.7773757807523, -122.39484771253102
                    viewModel.location.value = LatLng(37.7773757807523, -122.39484771253102)
                }

                fusedLocationClient.removeLocationUpdates(this)
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun hideKeyboard(view: View) {
        view.clearFocus()
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
