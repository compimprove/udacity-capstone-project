package com.compi.dinhnt.travelplanner.view_model

import android.app.Application
import androidx.lifecycle.*
import com.compi.dinhnt.travelplanner.database.getDatabase
import com.compi.dinhnt.travelplanner.database.seedDatabase
import com.compi.dinhnt.travelplanner.model.Activity
import com.compi.dinhnt.travelplanner.model.TravelPlan
import com.compi.dinhnt.travelplanner.model.TravelPlanWithActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class OverviewViewModel(app: Application) : AndroidViewModel(app) {
    private val database = getDatabase(app)
    private var _plans = database.travelPlanDao.getTravelPlansWithActivity()

    val plansWithActivity: LiveData<List<TravelPlanWithActivity>>
        get() = _plans

    val plans = Transformations.map(plansWithActivity) { _plansWithActivity ->
        _plansWithActivity.map {
            it.activities.sortWith { o1, o2 ->
                if (o1 != null && o2 != null) {
                    o1.date.compareTo(o2.date)
                } else {
                    0
                }
            }
            var startDate: Date? = null
            var endDate: Date? = null
            if (it.activities.isNotEmpty()) {
                startDate = it.activities[0].date
                endDate = it.activities[it.activities.size - 1].date
            }
            TravelPlan(
                it.travelPlanCTO.id,
                it.travelPlanCTO.name,
                it.activities.size,
                startDate,
                endDate,
                it.travelPlanCTO.weathers,
            )
        }
    }

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