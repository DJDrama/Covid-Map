package com.coronamap.www.ui.fragments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.coronamap.www.model.LocationItem
import com.google.android.libraries.maps.GoogleMap

class MapViewModel : ViewModel() {
    private val _locationItem: MutableLiveData<LocationItem> = MutableLiveData()
    val locationItem: LiveData<LocationItem>
        get() = _locationItem

    private val _infoWindowClicked = MutableLiveData<Boolean>()
    val infoWindowClicked: LiveData<Boolean>
        get() = _infoWindowClicked

    private val _isMapReady = MutableLiveData<Boolean>()
    private val _googleMap = MutableLiveData<GoogleMap>()

    private val _mapReadyAndLocationMediatorLiveData = MediatorLiveData<GoogleMapSettings>()
    val mapReadyAndLocationMediatorLiveData: LiveData<GoogleMapSettings>
        get() = _mapReadyAndLocationMediatorLiveData


    init {
        _locationItem.value = null
        _isMapReady.value = false
        _mapReadyAndLocationMediatorLiveData.addSource(_isMapReady) {
            _mapReadyAndLocationMediatorLiveData.value =
                GoogleMapSettings(it, _locationItem.value, _googleMap.value)
        }
        _mapReadyAndLocationMediatorLiveData.addSource(_locationItem) {
            _mapReadyAndLocationMediatorLiveData.value =
                GoogleMapSettings(_isMapReady.value!!, it, _googleMap.value)
        }
        _mapReadyAndLocationMediatorLiveData.addSource(_googleMap) {
            _mapReadyAndLocationMediatorLiveData.value =
                GoogleMapSettings(_isMapReady.value!!, _locationItem.value, it)
        }
    }

    fun setCurrentLocation(it: LocationItem) {
        _locationItem.value = it
    }

    fun getLocationItem() = locationItem.value

    fun setInfoWindowClicked(bool: Boolean) {
        _infoWindowClicked.value = bool
    }

    fun setMapReady(value: Boolean) {
        _isMapReady.value = value
    }

    fun setGoogleMap(map: GoogleMap) {
        _googleMap.value = map
    }


    fun clearGoogleMap(){
        _isMapReady.value =false
        _googleMap.value?.clear()
    }

}