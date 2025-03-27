package com.test.fleettrack.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.test.fleettrack.data.StatusRepository
import com.test.fleettrack.data.VehicleStatusEntity

class DashboardViewModel(private val statusRepository: StatusRepository) : ViewModel() {

    fun getData(): LiveData<VehicleStatusEntity> = statusRepository.getStatus()
}