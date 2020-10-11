package com.coronamap.www.model

import android.os.Parcelable
import com.google.android.libraries.maps.model.LatLng
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationItem(
    val id: String,
    val name: String,
    val latLng: LatLng?,
    val address: String
): Parcelable