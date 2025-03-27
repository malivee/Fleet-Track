package com.test.fleettrack.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [VehicleStatusEntity::class], version = 3, exportSchema = false)
abstract class StatusDatabase : RoomDatabase() {

    abstract fun statusDao(): StatusDao

    companion object {
        @Volatile
        private var instance: StatusDatabase? = null

        fun getInstance(context: Context): StatusDatabase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    StatusDatabase::class.java,
                    "StatusDB"
                ).fallbackToDestructiveMigration().build()

                instance = newInstance
                newInstance
            }
        }
    }


}