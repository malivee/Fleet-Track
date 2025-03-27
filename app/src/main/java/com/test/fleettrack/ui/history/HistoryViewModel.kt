package com.test.fleettrack.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.test.fleettrack.data.StatusRepository
import com.test.fleettrack.data.VehicleStatusEntity

class HistoryViewModel(private val repository: StatusRepository) : ViewModel() {
    fun getAllData(): LiveData<List<VehicleStatusEntity>> = repository.getAllData()
}