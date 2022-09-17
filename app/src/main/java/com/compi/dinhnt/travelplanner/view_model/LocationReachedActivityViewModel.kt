package com.compi.dinhnt.travelplanner.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.compi.dinhnt.travelplanner.database.LocalDatabase
import com.compi.dinhnt.travelplanner.model.TravelActivity
import kotlinx.coroutines.launch


class LocationReachedActivityViewModel(app: Application, private val database: LocalDatabase) :
    AndroidViewModel(app) {
    val currentTravelActivity = MutableLiveData<TravelActivity>()

    fun refreshTravelActivity(activityId: String) {
        viewModelScope.launch {
            currentTravelActivity.value = database.activityDao.getAll()[0]
        }
    }
}