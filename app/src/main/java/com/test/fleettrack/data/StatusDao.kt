package com.test.fleettrack.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StatusDao {
    @Query("SELECT * FROM vehicle ORDER BY id DESC LIMIT 1")
    fun getLatestVehicleData(): LiveData<VehicleStatusEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(statusEntity: VehicleStatusEntity)

    @Query("SELECT * FROM vehicle ORDER BY id DESC")
    fun getAllData(): LiveData<List<VehicleStatusEntity>>

}