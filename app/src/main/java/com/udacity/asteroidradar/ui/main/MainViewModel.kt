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

class MainViewModel(
    application: Application
): AndroidViewModel(application) {

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

    enum class NeoApiStatus { LOADING, ERROR, DONE }

    private val _apiStatus = MutableLiveData<NeoApiStatus>()
    val apiStatus: LiveData<NeoApiStatus>
        get() = _apiStatus

    // access database singleton
    private val database = getDatabase(application)

    // construct asteroids repository
    private val asteroidsRepository = AsteroidsRepository(database)
    val asteroids = getLiveData()

    // picture of the day url
    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>?
        get() = _pictureOfDay

    // asteroid tapped by user
    private val _selectedAsteroid = MutableLiveData<Asteroid>()
    val selectedAsteroid: LiveData<Asteroid>
        get() = _selectedAsteroid

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

    // gets asteroids from database as live data (default filter == SHOW_ALL)
    private fun getLiveData(filter: AsteroidsRepository.AsteroidsFilter = AsteroidsRepository.AsteroidsFilter.SHOW_ALL): LiveData<List<Asteroid>> {
        return asteroidsRepository.getLiveData(filter)
    }

    // get picture of day from api endpoint
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

    // navigation

    // call to set selectedAsteroid to trigger navigation
    fun showDetailFragment(asteroid: Asteroid) {
        _selectedAsteroid.value = asteroid
    }

    fun showDetailFragmentComplete() {
        _selectedAsteroid.value = null // prevent subsequent navigation
    }
}