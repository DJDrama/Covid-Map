package com.coronamap.www.model

data class SiDoByul(
    val resultCode: String,
    val resultMessage: String,
    val korea: SiDoByulDetail
)

data class SiDoByulDetail(
    val countryName: String,
    val newCase: String,
    val newCcase: String,
    val newFcase: String
)