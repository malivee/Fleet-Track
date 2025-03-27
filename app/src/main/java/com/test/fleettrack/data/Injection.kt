package com.test.fleettrack.data

import android.content.Context

object Injection {
    fun provideRepository(context: Context): StatusRepository {
        val database = StatusDatabase.getInstance(context)
        val dao = database.statusDao()
        return StatusRepository.getInstance(dao)
    }
}