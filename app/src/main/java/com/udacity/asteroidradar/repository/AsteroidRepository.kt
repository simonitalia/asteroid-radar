package com.udacity.asteroidradar.repository

import android.util.Log
import com.udacity.asteroidradar.api.NeoApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.models.Asteroid
import com.udacity.asteroidradar.models.AsteroidDatabaseArrayList
import com.udacity.asteroidradar.models.asDatabaseModel
import com.udacity.asteroidradar.utilities.DateUtilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

// uses dependency injection to get reference to database from ViewModel instance to negate need
// to keep a reference to context and prevent leaks

class AsteroidsRepository(private val database: AsteroidsDatabase) {

    enum class AsteroidsFilter(val value: String) {
        SHOW_ALL("all"), SHOW_PAST_WEEK("week"), SHOW_TODAY("today")
    }

    // app wide accessible asteroids reference
    // transformation transforms one live data to another live data (database asteroids list object to asteroid (model) list object)
    // this transformation only runs if an activity or fragment is listening
    suspend fun getLiveData(filter: AsteroidsFilter): List<Asteroid> {

        var list: List<Asteroid>

        withContext(Dispatchers.IO) {

            // show all
            if (filter == AsteroidsFilter.SHOW_ALL) {
                list = database.asteroidDao.getAllAsteroids().asDomainModel()

            } else {

                var startDate = ""
                var endDate = ""

                when (filter) {

                    // show last 7 days
                    AsteroidsFilter.SHOW_PAST_WEEK -> {
                        startDate = DateUtilities.getPastSevenDaysFormattedDates().last()
                        endDate = DateUtilities.getPastSevenDaysFormattedDates().first()
                    }

                    // show today
                    else -> {
                        startDate = DateUtilities.getTodayFormattedDates().first()
                        endDate = DateUtilities.getTodayFormattedDates().last()
                    }
                }

                Log.i(
                    "AndroidRepository.getLiveData",
                    "Fetching asteroid objects from database for period: $startDate to $endDate."
                )

                list =
                    database.asteroidDao.getAsteroidsForPeriod(startDate, endDate).asDomainModel()
            }
        }
        
        return list
    }

    //refresh database
    //this handles fetching data from API endpoint and parsing response into Asteroid Model objects
    suspend fun updateAsteroidsDatabase() {
        withContext(Dispatchers.IO) {

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