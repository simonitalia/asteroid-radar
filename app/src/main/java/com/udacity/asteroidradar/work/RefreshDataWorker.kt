package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import retrofit2.HttpException

class RefreshDataWorker(
    appContext: Context, params: WorkerParameters
): CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = AsteroidsRepository(database)

        //on network request success
        return try {
            repository.updateAsteroidsDatabase()
            Result.success()

            // on network request failure, retry at some time in the future
        } catch (e: HttpException) {
            Result.retry()
        }
    }
}