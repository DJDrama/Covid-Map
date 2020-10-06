package com.coronamap.www.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import com.coronamap.www.R
import com.coronamap.www.databinding.FragmentMapBinding
import com.coronamap.www.ui.showToast
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.OnMapReadyCallback

class MapFragment : Fragment(R.layout.fragment_map), OnMapReadyCallback {

    companion object {
        const val ZOOM_LEVEL = 13f
        const val REQUESTING_LOCATION_UPDATES_KEY = "REQUESTING_LOCATION_UPDATES_KEY"
        const val REQUEST_CODE_PERMISSION = 101
        const val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        const val MAX_NUMBER_REQUEST_PERMISSIONS = 2
        const val UPDATE_INTERVAL = 10 * 1000L
        const val FASTEST_INTERVAL = 2000L
        const val REQUEST_CHECK_SETTINGS = 1011
        const val AUTOCOMPLETE_REQUEST_CODE = 1
    }

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private var mHasPermission: Boolean = false
    private var mPermissionRequestCount: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMapBinding.bind(view)
        requestPermissionsIfNecessary()

        //setGoogleMap()
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

    

    private fun setGoogleMap(){
        binding.apply {
            map.getMapAsync(this@MapFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun onMapReady(p0: GoogleMap?) {
    }
}