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
): Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LocationItem

        if (latLng != other.latLng) return false

        return true
    }

    override fun hashCode(): Int {
        return latLng?.hashCode() ?: 0
    }
}