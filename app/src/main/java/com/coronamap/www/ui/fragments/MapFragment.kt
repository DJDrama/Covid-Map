package com.coronamap.www.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.coronamap.www.R
import com.coronamap.www.databinding.FragmentMapBinding
import com.coronamap.www.model.LocationItem
import com.coronamap.www.ui.showToast
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.OnMapReadyCallback
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.Marker
import com.google.android.libraries.maps.model.MarkerOptions

class MapFragment : Fragment(R.layout.fragment_map), OnMapReadyCallback,
    GoogleMap.OnInfoWindowClickListener {

    companion object {
        const val REQUESTING_LOCATION_UPDATES_KEY = "REQUESTING_LOCATION_UPDATES_KEY"
        const val REQUEST_CODE_PERMISSION = 101
        const val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        const val MAX_NUMBER_REQUEST_PERMISSIONS = 2
        const val UPDATE_INTERVAL = 10 * 1000L
        const val FASTEST_INTERVAL = 2000L
        const val REQUEST_CHECK_SETTINGS = 1011
        const val AUTOCOMPLETE_REQUEST_CODE = 1
    }

    private val mapViewModel: MapViewModel by viewModels()
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private var mHasPermission: Boolean = false
    private var mPermissionRequestCount: Int = 0
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mLocationRequest: LocationRequest
    private var requestingLocationUpdates = false

    private var locationCallback: LocationCallback? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMapBinding.bind(view)
        requestPermissionsIfNecessary()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    // Update UI with location data

                    mapViewModel.setCurrentLocation(
                        LocationItem(
                            id = "CURRENT_LOCATION",
                            name = "현재 위치",
                            latLng = LatLng(location.latitude, location.longitude),
                            address = ""
                        )
                    )
                    //just do once so break
                    //stopLocationUpdates()
                    break
                }
            }
        }
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        updateValuesFromBundle(savedInstanceState)

        binding.map.let {
            it.onCreate(savedInstanceState)
            it.getMapAsync(this)
        }
        //setGoogleMap()
        subscribeObservers()
    }

    @SuppressLint("MissingPermission")
    private fun subscribeObservers() {
        mapViewModel.mapReadyAndLocationMediatorLiveData.observe(viewLifecycleOwner) {
            it?.run {
                if (isMapReady) {
                    this.googleMap?.let { gMap ->
                        this.location?.let { locationItem ->
                            Log.e("check", "check : L" + locationItem);
                            with(gMap) {
                                moveCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        locationItem.latLng,
                                        mapViewModel.getZoomLevel() ?: 13f
                                    )
                                )
                                clear()
                                val markerOptions =
                                    MarkerOptions().position(locationItem.latLng!!)
                                        .title(locationItem.name)
                                val marker = addMarker(markerOptions)
                                marker.tag = locationItem
                                marker.showInfoWindow()

                                setOnInfoWindowClickListener(this@MapFragment)

                                isMyLocationEnabled = true
                                uiSettings.isZoomControlsEnabled = true
                                setOnCameraMoveListener {
                                   //control zoom level
                                    mapViewModel.setZoomLevel(cameraPosition.zoom)
                                }

                                setOnMyLocationButtonClickListener {
                                    startLocationUpdates()
                                    mapViewModel.setSearchMyLocationClicked(true)
                                    //getLastLocation()
                                    false
                                }

                                //After Google map has been initialized, fetch my visited places
                                //fetchMyPlaces(this)
                            }
                        } ?: if (requestingLocationUpdates) {
                            startLocationUpdates()
                        } else {
                            setLocationSettings()
                        }
                    }
                }
            }
        }
    }

    private fun updateValuesFromBundle(savedInstanceState: Bundle?) {
        savedInstanceState ?: return
        // Update the value of requestingLocationUpdates from the Bundle.
        if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
            requestingLocationUpdates = savedInstanceState.getBoolean(
                REQUESTING_LOCATION_UPDATES_KEY
            )
        }
        // ...
        // Update UI to match restored state
        //updateUI()
    }

    private fun requestPermissionsIfNecessary() {
        mHasPermission = checkPermission()
        if (!mHasPermission) {
            if (mPermissionRequestCount < MAX_NUMBER_REQUEST_PERMISSIONS) {
                mPermissionRequestCount++
                requestPermissions(
                    arrayOf(locationPermission),
                    REQUEST_CODE_PERMISSION
                )
            } else {
                requireContext().showToast(
                    getString(R.string.set_permissions_in_settings)
                )
            }
        }
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            locationPermission
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Check if permissions were granted after a permissions request flow.
        if (requestCode == REQUEST_CODE_PERMISSION) {
            //just check index 0 since we have only 1 permission
            if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
                getLastLocation()
            } else {
                requireContext().showToast(
                    getString(R.string.set_permissions_in_settings)
                )
            }
        }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requireContext().showToast(
                getString(R.string.set_permissions_in_settings)
            )
            return
        }
        if (::fusedLocationProviderClient.isInitialized) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    mapViewModel.setCurrentLocation(
                        LocationItem(
                            id = "CURRENT_LOCATION",
                            name = "현재 위치",
                            latLng = LatLng(location.latitude, location.longitude),
                            address = ""
                        )
                    )
                } ?: setLocationSettings()
            }
        }
    }

    private fun setLocationSettings() {
        // Create the location request to start receiving updates
        // https://developer.android.com/training/location/change-location-settings
        mLocationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = UPDATE_INTERVAL
            fastestInterval = FASTEST_INTERVAL
        }

        // Create LocationSettingsRequest object using location request
        val builder = LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest)
        val locationSettingsRequest = builder.build()

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        val settingsClient = LocationServices.getSettingsClient(requireContext())
        val task = settingsClient.checkLocationSettings(locationSettingsRequest)
        task.addOnSuccessListener { locationSettingsResponse ->
            // All location settings are satisfied. The client can initialize
            // location requests here.
            requestingLocationUpdates = true
            startLocationUpdates()
        }
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(
                        requireActivity(),
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                    Log.e("Error", "SendIntentException:  $sendEx")

                }
            } else {
                requireContext().showToast("$exception")
            }
        }
    }

    private fun startLocationUpdates() {
        if (checkPermission()) {
            fusedLocationProviderClient.requestLocationUpdates(
                mLocationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if(::mLocationRequest.isInitialized)
            startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun setGoogleMap() {
        binding.apply {
            map.getMapAsync(this@MapFragment)
        }
    }

    private fun stopLocationUpdates() {
        requestingLocationUpdates = false
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, requestingLocationUpdates)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.map.onDestroy()
        mapViewModel.clearGoogleMap()
        if (locationCallback != null) {
            locationCallback = null
        }
        _binding = null
    }

    override fun onMapReady(p0: GoogleMap?) {
        p0?.let {
            mapViewModel.setMapReady(true)
            mapViewModel.setGoogleMap(it)
        }
    }

    override fun onInfoWindowClick(p0: Marker?) {

    }
}