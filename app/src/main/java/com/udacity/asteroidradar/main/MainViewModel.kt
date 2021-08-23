package com.udacity.asteroidradar.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.NeoApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.launch
import org.json.JSONObject

/**
 * Attach this [ViewModel] to [MainFragment].
 */

class MainViewModel: ViewModel() {

    enum class NeoApiStatus { LOADING, ERROR, DONE }

    // live data object to observe api request status
    private val _status = MutableLiveData<NeoApiStatus>()
    val status: LiveData<NeoApiStatus>  //externally accessible property
        get() = _status

    // live data (mars) property list object
    private val _asteroids = MutableLiveData<ArrayList<Asteroid>>()
    val asteroids: LiveData<ArrayList<Asteroid>>
        get() = _asteroids

    init {
        getAsteroids()
    }

    private fun getAsteroids() {
        viewModelScope.launch {
            _status.value = NeoApiStatus.LOADING

            try { //on successful response

                val response = NeoApi.service.getAsteroidsData()

                //convert string result to json object and parse jsonObject
                val jsonObject = JSONObject(response)
                _asteroids.value = parseAsteroidsJsonResult(jsonObject)
                _status.value = NeoApiStatus.DONE

                Log.i("MainViewModel", "Asteroid JSON objects fetched successfully with objects: {${_asteroids.value?.count()}}")

            // on error
            } catch (e: Exception) {
                _status.value = NeoApiStatus.ERROR
                _asteroids.value = ArrayList() // set to empty list
                Log.e("MainViewModel", "Failed to fetch Asteroids Data with error: {$e}.")
            }
        }
    }
}