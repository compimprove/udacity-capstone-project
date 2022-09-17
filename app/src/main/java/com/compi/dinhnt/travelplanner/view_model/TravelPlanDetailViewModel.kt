package com.compi.dinhnt.travelplanner.view_model

import android.app.Application
import androidx.lifecycle.*
import com.compi.dinhnt.travelplanner.database.getDatabase
import com.compi.dinhnt.travelplanner.model.TravelPlanCTO
import com.compi.dinhnt.travelplanner.model.TravelPlanWithActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TravelPlanDetailViewModel(
    app: Application,
    travelPlanId: String
) :
    AndroidViewModel(app) {

    fun editPlan(id: String, name: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                database.travelPlanDao.update(TravelPlanCTO(name, null, id))
            }
        }
    }

    private val database = getDatabase(app)
    val currentPage = MutableLiveData(0)
    val travelPlanWithActivity = database.travelPlanDao.travelPlanWithActivity(travelPlanId)

    class Factory(private val app: Application, private val travelPlanId: String) :
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