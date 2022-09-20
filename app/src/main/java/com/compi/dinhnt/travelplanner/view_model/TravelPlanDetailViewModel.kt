package com.compi.dinhnt.travelplanner.view_model

import android.app.Application
import androidx.lifecycle.*
import com.compi.dinhnt.travelplanner.database.LocalDatabase
import com.compi.dinhnt.travelplanner.model.TravelActivity
import com.compi.dinhnt.travelplanner.model.TravelPlanCTO
import com.compi.dinhnt.travelplanner.model.TravelPlanWithActivity
import com.compi.dinhnt.travelplanner.utils.TravelPlanUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class TravelPlanDetailViewModel(
    app: Application,
    private val database: LocalDatabase
) :
    AndroidViewModel(app) {
    private lateinit var travelPlanId: String
    val currentPage = MutableLiveData(0)
    private val formatter = SimpleDateFormat("yyyy-MM-dd")
    private lateinit var _travelPlanWithActivity: LiveData<TravelPlanWithActivity>

    val travelPlanWithActivity: LiveData<TravelPlanWithActivity>
        get() = _travelPlanWithActivity
    val createActivity = MutableLiveData("")
    val editActivity = MutableLiveData(Pair("", ""))

    lateinit var mapTravelPlanWithActivity: LiveData<SortedMap<String, MutableList<TravelActivity>>>

    fun setTravelPlanId(id: String) {
        travelPlanId = id
        _travelPlanWithActivity = database.travelPlanDao.travelPlanWithActivity(travelPlanId)
        mapTravelPlanWithActivity =
            Transformations.map(_travelPlanWithActivity) { travelPlanWithActivity ->
                val map = sortedMapOf<String, MutableList<TravelActivity>>()
                travelPlanWithActivity.activities.forEach {
                    val dateStr = formatter.format(it.date)
                    if (!map.containsKey(dateStr)) {
                        map[dateStr] = mutableListOf(it)
                    } else {
                        map[dateStr]?.add(it)
                    }
                }
                map
            }
    }

    fun onCreateActivity(day: String) {
        createActivity.value = day
    }

    fun onEditActivity(day: String, activityId: String) {
        editActivity.value = Pair(day, activityId)
    }

    fun editPlan(id: String, name: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val imageUrl = TravelPlanUtils.searchImage(name)
                database.travelPlanDao.update(TravelPlanCTO(name, imageUrl, id))
            }
        }
    }

    fun receiveCreateActivityEvent() {
        createActivity.value = ""
    }

    fun receiveEditActivityEvent() {
        editActivity.value = Pair("", "")
    }
}