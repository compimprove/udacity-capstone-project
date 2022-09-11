package com.compi.dinhnt.travelplanner.view_model

import android.app.Application
import androidx.lifecycle.*
import com.compi.dinhnt.travelplanner.database.getDatabase
import com.compi.dinhnt.travelplanner.model.Activity
import com.compi.dinhnt.travelplanner.model.ActivityType
import com.compi.dinhnt.travelplanner.model.Location
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*


class CreateEditActivityViewModel(app: Application) :
    AndroidViewModel(app) {
    private val database = getDatabase(app)
    private var activityId: String? = null
    private var travelPlanId: Long = 0
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd")

    var activityType = MutableLiveData(ActivityType.HOTEL)
    var locationName = MutableLiveData<String>()
    var activityName = MutableLiveData<String>()
    var note = MutableLiveData<String>()
    var latitude = MutableLiveData<Double>()
    var longitude = MutableLiveData<Double>()
    var time = MutableLiveData<Long>()
    var date = MutableLiveData<Long>()

    val showSnackBarInt = MutableLiveData<Int>()

    fun reset() {
        activityId = null
        travelPlanId = 0
        activityType = MutableLiveData(ActivityType.HOTEL)
        activityName = MutableLiveData<String>()
        locationName = MutableLiveData<String>()
        latitude = MutableLiveData<Double>()
        longitude = MutableLiveData<Double>()
        time = MutableLiveData<Long>()
        date = MutableLiveData<Long>()
    }

    fun init(travelPlanId: Long, day: String, id: String) {
        this.travelPlanId = travelPlanId
        if (id != "") {
            activityId = id
            viewModelScope.launch {
                val activity = database.activityDao.get(id)
                if (activityName.value == null) {
                    activityName.value = activity.name
                }
                if (activityType.value == null) {
                    activityType.value = activity.type
                }
                if (time.value == null) {
                    time.value = activity.time
                }
                if (date.value == null) {
                    date.value = activity.date.time
                }
                if (locationName.value == null) {
                    locationName.value = activity.location?.locationName
                }
                if (longitude.value == null) {
                    longitude.value = activity.location?.latLong?.longitude
                }
                if (latitude.value == null) {
                    latitude.value = activity.location?.latLong?.latitude
                }
            }
        } else if (day != "") {
            date.value = dateFormat.parse(day)?.time
        }
    }

    fun setLocation(latLong: LatLng, _locationName: String) {
        latitude.value = latLong.latitude
        longitude.value = latLong.longitude
        locationName.value = _locationName
    }

    fun saveActivity() {
        if (validateData()) {
            val location = if (locationName.value != null
                && longitude.value != null
                && latitude.value != null
            ) {
                Location(LatLng(longitude.value!!, latitude.value!!), locationName.value!!)
            } else {
                null
            }
            if (activityId == null) {
                val activity = Activity(
                    activityName.value!!,
                    activityType.value!!,
                    Date(date.value!!),
                    time.value!!,
                    travelPlanId,
                    location,
                    note.value
                )
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        database.activityDao.insert(activity)
                    }
                }
            } else {
                val activity = Activity(
                    activityName.value!!,
                    activityType.value!!,
                    Date(date.value!!),
                    time.value!!,
                    travelPlanId,
                    location,
                    note.value,
                    activityId!!
                )
                viewModelScope.launch {
                    withContext(Dispatchers.IO) {
                        database.activityDao.update(activity)
                    }
                }
            }
        }
    }

    private fun validateData(): Boolean {
        if (activityName.value == null || activityName.value == "") {
            return false
        } else if (time.value == null) {
            return false
        } else if (date.value == null) {
            return false
        } else {
            return true
        }
    }

    class Factory(
        private val app: Application
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CreateEditActivityViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CreateEditActivityViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}