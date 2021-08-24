package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.models.Asteroid


/**
 * This is the local app Database
 * Handles database records management and queries
 */

@Dao
interface AsteroidDao {

    // get list of all asteroids from cache / room database
    @Query("select * from databaseasteroid")
    fun getAllAsteroids(): LiveData<List<DatabaseAsteroid>>

    // get filtered list of asteroids
    @Query("select * from databaseasteroid WHERE is_potentially_hazardous = :isPotentiallyHazardous")
    fun getAllHazardousAsteroids(isPotentiallyHazardous: Boolean): LiveData<List<DatabaseAsteroid>>

    // insert method to add new asteroid/s objects into database (when fetched from API)
    // is an upsert method to replace / overwrite existing key if same primary key already exists
    // vararg = variable argument (unknown number of arguments) to pass multiple asteroids without making a list
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)

}

// room database and dao interface
@Database(entities = [DatabaseAsteroid::class], version = 1) //must set version value or app will crash
abstract class AsteroidsDatabase: RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
}

private lateinit var INSTANCE: AsteroidsDatabase

fun getDatabase(context: Context): AsteroidsDatabase {

    synchronized(AsteroidsDatabase::class.java) { // use synchronized for thread safety

        //check is late init variable is initialized
        if (!::INSTANCE.isInitialized) {

            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AsteroidsDatabase::class.java,
                "asteroids"
            ).build()
        }
    }

    return INSTANCE
}