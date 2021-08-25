package com.udacity.asteroidradar.ui.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.models.Asteroid
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
    val asteroids = getLiveData()

    init {
        updateAsteroids()
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
}