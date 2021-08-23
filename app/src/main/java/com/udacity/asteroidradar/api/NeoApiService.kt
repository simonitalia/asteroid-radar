package com.udacity.asteroidradar.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * Deserialize JSON response with preferred converter
 * App uses retrofit scalers.
 */

//moshi converter
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

// Gson converter
private val gson: Gson by lazy {
    GsonBuilder().setLenient().create()
}

// retrofit setup
val retrofit: Retrofit by lazy {
    Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()
}

/**
 * Nasa Near Earth Objects (NEO) API Endpoint/s interface
 */

interface NeoApiService {

    // Get requests

    // fetch all available data frm endpoint (last 7 days)
    @GET("neo/rest/v1/feed?")  // param = api endpoint
    suspend fun getAsteroidsData(
        @Query("api_key") api_key: String = Constants.API_KEY
    ): String
}

// api service singleton
object NeoApi {
    val service: NeoApiService by lazy {
        retrofit.create(NeoApiService::class.java)
    }
}