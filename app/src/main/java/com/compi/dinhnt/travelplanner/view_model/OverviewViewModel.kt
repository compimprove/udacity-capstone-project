package com.compi.dinhnt.travelplanner.view_model

import android.app.Application
import androidx.lifecycle.*
import com.compi.dinhnt.travelplanner.database.getDatabase
import com.compi.dinhnt.travelplanner.database.seedDatabase
import com.compi.dinhnt.travelplanner.model.TravelPlanWithActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OverviewViewModel(app: Application) : AndroidViewModel(app) {
    private val database = getDatabase(app)
    private var _plans = database.travelPlanDao.getTravelPlansWithActivity()

    val plansWithActivity: LiveData<List<TravelPlanWithActivity>>
        get() = _plans

    val plans = database.travelPlanDao.getTravelPlans()

    init {
        refreshTravelPlanWithActivity()
    }

    fun refreshTravelPlanWithActivity() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                seedDatabase(database)
            }
        }
    }

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(OverviewViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return OverviewViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}