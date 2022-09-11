package com.compi.dinhnt.travelplanner.view_model

import android.app.Application
import androidx.lifecycle.*
import com.compi.dinhnt.travelplanner.database.getDatabase
import com.compi.dinhnt.travelplanner.model.TravelPlanWithActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TravelPlanDetailViewModel(
    app: Application,
    private val travelPlanId: Long
) :
    AndroidViewModel(app) {


    private val database = getDatabase(app)
    val travelPlanWithActivity = MutableLiveData<TravelPlanWithActivity>()

    fun refreshData() {
        viewModelScope.launch {
            travelPlanWithActivity.value =
                database.travelPlanDao.getTravelPlanWithActivity(travelPlanId)
        }
    }

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