package com.coronamap.www.api

import com.coronamap.www.model.LocalCounter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface CoronaApi {

    @GET("korea/")
    suspend fun getLocalCounter(
        @Query("serviceKey")
        serviceKey: String = ""
    ): LocalCounter


    companion object {
        private const val BASE_URL = "https://api.corona-19.kr/"
        fun create(): CoronaApi = create(BASE_URL.toHttpUrlOrNull()!!)
        fun create(httpUrl: HttpUrl): CoronaApi {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BASIC
            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(CoronaApi::class.java)
        }


    }

}