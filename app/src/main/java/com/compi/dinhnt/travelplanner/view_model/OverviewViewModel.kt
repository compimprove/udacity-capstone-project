package com.compi.dinhnt.travelplanner.view_model

import android.app.Application
import androidx.lifecycle.*
import com.compi.dinhnt.travelplanner.database.LocalDatabase
import com.compi.dinhnt.travelplanner.database.seedSampleData
import com.compi.dinhnt.travelplanner.model.TravelActivity
import com.compi.dinhnt.travelplanner.model.TravelPlan
import com.compi.dinhnt.travelplanner.model.TravelPlanCTO
import com.compi.dinhnt.travelplanner.model.TravelPlanWithActivity
import com.compi.dinhnt.travelplanner.utils.TravelPlanUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class OverviewViewModel(app: Application, private val database: LocalDatabase) :
    AndroidViewModel(app) {
    private var _plans = database.travelPlanDao.getTravelPlansWithActivity()

    val plansWithActivity: LiveData<List<TravelPlanWithActivity>>
        get() = _plans

    val reachedTravelActivity = MutableLiveData<TravelActivity?>()
    fun refreshReachedTravelActivity(id: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                database.activityDao.get(id)?.let { travelActivity ->
                    reachedTravelActivity.value = travelActivity
                }
            }
        }
    }

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
                it.travelPlanCTO.imageUrl,
                startDate,
                endDate,
            )
        }
    }

    init {
        refreshTravelPlanWithActivity()
    }

    private fun refreshTravelPlanWithActivity() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
//                seedSampleData(database)
            }
        }
    }

    fun createPlan(name: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val imageUrl = TravelPlanUtils.searchImage(name)
                database.travelPlanDao.insert(TravelPlanCTO(name, imageUrl))
            }
        }
    }
}