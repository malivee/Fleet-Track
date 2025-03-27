package com.test.fleettrack.data

import androidx.lifecycle.LiveData

class StatusRepository(private val statusDao: StatusDao) {

    fun getStatus(): LiveData<VehicleStatusEntity> = statusDao.getLatestVehicleData()

    suspend fun insertStatus(statusEntity: VehicleStatusEntity) {
        statusDao.insert(statusEntity)
    }

    fun getAllData(): LiveData<List<VehicleStatusEntity>> = statusDao.getAllData()


    companion object {
        @Volatile
        private var instance: StatusRepository? = null
        fun getInstance(historyDao: StatusDao): StatusRepository =
            instance ?: synchronized(this) {
                instance ?: StatusRepository(historyDao)

            }.also { instance = it }
    }
}