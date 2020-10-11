package com.coronamap.www.ui.fragments

import com.coronamap.www.model.LocationItem
import com.google.android.libraries.maps.GoogleMap

data class GoogleMapSettings(
    val isMapReady: Boolean,
    val location: LocationItem?,
    val googleMap: GoogleMap?
)