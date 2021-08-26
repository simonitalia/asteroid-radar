package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.api.NeoApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.models.Asteroid
import com.udacity.asteroidradar.models.AsteroidDatabaseArrayList
import com.udacity.asteroidradar.models.asDatabaseModel
import kotlinx.coroutines.withContext
import org.json.JSONObject

// uses dependency injection to get reference to database from ViewModel instance to negate need
// to keep a reference to context and prevent leaks
class AsteroidsRepository(private val database: AsteroidsDatabase) {

    // app wide accessible asteroids reference
    // transformation transforms one live data to another live data (database asteroids list object to asteroid (model) list object)
    // this transformation only runs if an activity or fragment is listening
    fun getLiveData(): LiveData<List<Asteroid>> {
            return Transformations.map(database.asteroidDao.getAllAsteroids()) {
                it.asDomainModel()
            }
    }

    //refresh database
    //this handles fetching data from API endpoint and parsing response into Asteroid Model objects
    suspend fun updateAsteroidsDatabase() {
        withContext(kotlinx.coroutines.Dispatchers.IO) {

            // attempt to fetch asteroids data from api endpoint,
            // parse response,
            // and insert into database
            try {
                val response = NeoApi.service.getAsteroidsData()
                val jsonObject = JSONObject(response)
                val asteroids = parseAsteroidsJsonResult(jsonObject)

                /**
                 * Update NEO Database Table
                 */

                // clear all existing records from database table
                database.asteroidDao.clear()

                //insert fetched
                //transform Asteroids to DatabaseAsteroid and insert to database
                val dbAsteroids = AsteroidDatabaseArrayList(asteroids).asDatabaseModel()
                database.asteroidDao.insertAll(*dbAsteroids)

                Log.i("AndroidRepository", "Asteroid Json objects successfully fetched from api: ${asteroids.count()}.")

            //on error
            }  catch (e: Exception) {
                Log.e("AndroidRepository", "Failed to fetch asteroids data with error: $e.")
            }
        }
    }
}