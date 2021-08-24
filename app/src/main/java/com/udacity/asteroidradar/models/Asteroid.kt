package com.udacity.asteroidradar.models

import android.os.Parcelable
import com.udacity.asteroidradar.database.DatabaseAsteroid
import kotlinx.android.parcel.Parcelize


/**
 * Asteroid represent an asteroid used by the app and
 * for parsing json object fetched from API endpoint
 */
@Parcelize
data class Asteroid(
    val id: Long,
    val codename: String,
    val closeApproachDate: String,
    val absoluteMagnitude: Double,
    val estimatedDiameter: Double,
    val relativeVelocity: Double,
    val distanceFromEarth: Double,
    val isPotentiallyHazardous: Boolean
): Parcelable

/**
 * extension helper function to convert Array list of asteroid objects to list of database asteroid objects
 */

data class AsteroidDatabaseArrayList(val asteroids: ArrayList<Asteroid>)

fun AsteroidDatabaseArrayList.asDatabaseModel(): Array<DatabaseAsteroid> {
    return asteroids.map {
        DatabaseAsteroid(
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }.toTypedArray()
}