package com.udacity.asteroidradar.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.api.NeoApi
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.models.Asteroid
import com.udacity.asteroidradar.models.PictureOfDay
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch

/**
 * Attach this [ViewModel] to [MainFragment].
 */

class MainViewModel(application: Application): AndroidViewModel(application) {

    enum class NeoApiStatus { LOADING, ERROR, DONE }

    private val _apiStatus = MutableLiveData<NeoApiStatus>()
    val apiStatus: LiveData<NeoApiStatus>
        get() = _apiStatus

    //access database singleton
    private val database = getDatabase(application)

    //construct asteroids repository
    private val asteroidsRepository = AsteroidsRepository(database)
    val asteroids = getLiveData()

    //picture of the day url
    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>?
        get() = _pictureOfDay

    init {
        updateAsteroids()
        getPictureOfDay()
    }

    // triggers api fetch
    private fun updateAsteroids() {
        viewModelScope.launch {
            asteroidsRepository.updateAsteroidsDatabase()
        }
    }

    //gets asteroids from database as live data
    private fun getLiveData(): LiveData<List<Asteroid>> {
        return asteroidsRepository.getLiveData()
    }

    //get picture of day from api endpoint
    private fun getPictureOfDay() {
        viewModelScope.launch {
            _apiStatus.value = NeoApiStatus.LOADING

            //on success
            try {
                _pictureOfDay.value = NeoApi.service.getPictureOfDay().also {
                    Log.i("MainViewModel.getPictureOfDay", "Picture of day successfully fetched from api. Url: ${it.url}.")
                    _apiStatus.value = NeoApiStatus.DONE
                }

            //on error
            } catch (e: Exception) {
                _apiStatus.value = NeoApiStatus.ERROR
                Log.e("MainViewModel.getPictureOfDay", "Failed to fetch picture of day with error: $e.")
            }
        }
    }

    /**
     * Factory for constructing this MainViewModel with application parameter
     */
    class Factory(val app: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewModel")
        }
    }
}