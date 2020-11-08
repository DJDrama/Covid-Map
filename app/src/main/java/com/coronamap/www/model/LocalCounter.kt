package com.coronamap.www.model

import com.squareup.moshi.Json

data class LocalCounter(
    @Json(name="NowCase")
    val nowCase: String,
    @Json(name="TodayDeath")
    val todayDeath: String,
    @Json(name="TodayRecovered")
    val todayRecovered: String,
    @Json(name="TotalCase")
    val totalCase: String,
    @Json(name="TotalCaseBefore")
    val totalCaseBefore: String,
    @Json(name="TotalChecking")
    val totalChecking: String,
    @Json(name="TotalDeath")
    val totalDeath: String,
    @Json(name="TotalRecovered")
    val totalRecovered: String,
    val caseCount: String,
    val casePercentage: String,
    val checkingCounter: String,
    val checkingPercentage: String,
    val city1n: String,
    val city1p: String,
    val city2n: String,
    val city2p: String,
    val city3n: String,
    val city3p: String,
    val city4n: String,
    val city4p: String,
    val city5n: String,
    val city5p: String,
    val deathPercentage: Double,
    @Json(name="notcaseCount")
    val notCaseCount: String,
    @Json(name="notcasePercentage")
    val notCasePercentage: String,
    val recoveredPercentage: Double,
    val resultCode: String,
    val resultMessage: String,
    val updateTime: String
)