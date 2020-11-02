package com.coronamap.www.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DangerousPlaces(
    var documentId: String = "",
    val name: String = "",
    val address: String = "",
    val latitude: String = "",
    val longitude: String = ""
) : Parcelable