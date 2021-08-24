package com.udacity.asteroidradar.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch

/**
 * Attach this [ViewModel] to [MainFragment].
 */

class MainViewModel(application: Application): AndroidViewModel(application) {

    /**
     * Factory for constructing this MainViewModel with application parameter
     */
    class Factory(val app: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct MainViewModel")
        }
    }

    //access database singleton
    private val database = getDatabase(application)

    //construct asteroids repository
    private val asteroidsRepository = AsteroidsRepository(database)
    val asteroids = asteroidsRepository.getLiveData() // get data from room database as live data

    val loadingStatus = asteroidsRepository.status.value

    init {
        updateAsteroids() // triggers api fetch
    }

    private fun updateAsteroids() {
        viewModelScope.launch {
            asteroidsRepository.updateAsteroidsDatabase()
        }
    }
}