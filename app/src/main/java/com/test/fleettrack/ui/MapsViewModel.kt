package com.test.fleettrack.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.fleettrack.data.StatusRepository
import com.test.fleettrack.data.VehicleStatusEntity
import kotlinx.coroutines.launch

class MapsViewModel(private val statusRepository: StatusRepository) : ViewModel() {
    fun insertStatus(statusEntity: VehicleStatusEntity) {
        viewModelScope.launch {
            statusRepository.insertStatus(statusEntity)
        }
    }

    fun getData(): LiveData<VehicleStatusEntity> = statusRepository.getStatus()

}