package com.udacity.asteroidradar.ui.detail

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.models.Asteroid
import com.udacity.asteroidradar.ui.main.MainViewModel


/**
 * The [ViewModel] that is associated with the [DetailFragment].
 */

class DetailViewModel(
    @Suppress("UNUSED_PARAMETER")
    app: Application,
    selectedAsteroid: Asteroid
): AndroidViewModel(app) {

    /**
     * Factory for constructing this DetailViewModel with parameters
     */
    class Factory(val app: Application, val asteroid: Asteroid): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DetailViewModel(app, asteroid) as T
            }
            throw IllegalArgumentException("Unable to construct viewModel")
        }
    }

    // asteroid object passed in on navigation
    private val _selectedAsteroid = MutableLiveData<Asteroid>()
    val selectedAsteroid: LiveData<Asteroid>
        get() = _selectedAsteroid

    init {
        _selectedAsteroid.value = selectedAsteroid
    }


}