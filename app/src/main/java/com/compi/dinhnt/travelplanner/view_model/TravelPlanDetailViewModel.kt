package com.compi.dinhnt.travelplanner.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.compi.dinhnt.travelplanner.database.getDatabase

class TravelPlanDetailViewModel(app: Application, travelPlanId: Long) : AndroidViewModel(app) {


    private val database = getDatabase(app)
    val travelPlanWithActivity = database.travelPlanDao.getTravelPlanWithActivity(travelPlanId)


    class Factory(private val app: Application, private val travelPlanId: Long) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TravelPlanDetailViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TravelPlanDetailViewModel(app, travelPlanId) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}