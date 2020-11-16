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

    private val _infoWindowClicked = MutableLiveData<Boolean>()
    val infoWindowClicked: LiveData<Boolean>
        get() = _infoWindowClicked

    private val _isMapReady = MutableLiveData<Boolean>()
    private val _googleMap = MutableLiveData<GoogleMap>()

    private val _mapReadyAndLocationMediatorLiveData = MediatorLiveData<GoogleMapSettings>()
    val mapReadyAndLocationMediatorLiveData: LiveData<GoogleMapSettings>
        get() = _mapReadyAndLocationMediatorLiveData

    private val _mapZoomLevel = MutableLiveData<Float>()
    private val _searchMyLocation = MutableLiveData<Boolean>()


    init {
        _mapZoomLevel.value = 13f //default zoom level
        _searchMyLocation.value = false
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
        _searchMyLocation.value?.let { isPressed ->
            if (isPressed) {
                _locationItem.value = it
                _searchMyLocation.value = false
            } else {
                if (_locationItem.value != it)
                    _locationItem.value = it
            }
        }
    }

    fun setSearchMyLocationClicked(value: Boolean) {
        _searchMyLocation.value = value
    }

    fun getLocationItem() = _locationItem.value

    fun setInfoWindowClicked(bool: Boolean) {
        _infoWindowClicked.value = bool
    }

    fun setMapReady(value: Boolean) {
        _isMapReady.value = value
    }

    fun setGoogleMap(map: GoogleMap) {
        _googleMap.value = map
    }

    fun setZoomLevel(zoomLevel: Float) {
        _mapZoomLevel.value = zoomLevel
    }

    fun getZoomLevel() = _mapZoomLevel.value


    fun clearGoogleMap() {
        _isMapReady.value = false
        _googleMap.value?.clear()
    }

}