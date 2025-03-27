package com.test.fleettrack.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("vehicle")
class VehicleStatusEntity(
    @ColumnInfo("id")
    @PrimaryKey
    val id: Long,

    @ColumnInfo("engine_status")
    val engineStatus: Boolean,

    @ColumnInfo("door_status")
    val doorStatus: Boolean,

    @ColumnInfo("speed")
    val speed: Double
)
