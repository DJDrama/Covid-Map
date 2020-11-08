package com.coronamap.www.ui.fragments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.coronamap.www.api.CoronaApi

class DashBoardViewModelFactory(private val coronaApi: CoronaApi) : ViewModelProvider.Factory{

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashBoardViewModel::class.java)) {
            return DashBoardViewModel(coronaApi = coronaApi) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}