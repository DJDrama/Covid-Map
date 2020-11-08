package com.coronamap.www.ui.fragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coronamap.www.api.CoronaApi
import com.coronamap.www.model.LocalCounter
import com.coronamap.www.model.SiDoByul
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class DashBoardViewModel(private val coronaApi: CoronaApi) : ViewModel() {
    private val _localCounterMutableLiveData = MutableLiveData<LocalCounter>()
    val localCounterLiveData
        get() = _localCounterMutableLiveData


    private val _siDoByulMutableLiveData = MutableLiveData<SiDoByul>()
    val siDoByulLiveData
        get() = _siDoByulMutableLiveData

    init {
        fetchLocalCounter()
        fetchSiDoByulData()
    }

    private fun fetchLocalCounter() {
        viewModelScope.launch(IO) {
            val localCounter = coronaApi.getLocalCounter()
            if(localCounter.resultCode == "0") {
                _localCounterMutableLiveData.postValue(localCounter)
            }
        }
    }
    private fun fetchSiDoByulData(){
        viewModelScope.launch(IO) {
            val siDoByul = coronaApi.getSiDoByul()
            if(siDoByul.resultCode == "0"){
                _siDoByulMutableLiveData.postValue(siDoByul)
            }

        }
    }

}