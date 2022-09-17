package com.compi.dinhnt.travelplanner.view_model

import android.app.Application
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.compi.dinhnt.travelplanner.database.LocalDatabase
import com.compi.dinhnt.travelplanner.model.TravelActivity
import com.compi.dinhnt.travelplanner.model.ActivityType
import com.compi.dinhnt.travelplanner.model.Location
import com.compi.dinhnt.travelplanner.network.Network
import com.compi.dinhnt.travelplanner.network.getWeather
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.await
import java.text.SimpleDateFormat
import java.util.*


class CreateEditActivityViewModel(app: Application, private val database: LocalDatabase) :
    AndroidViewModel(app) {
    private var activityId: String? = null
    private var travelPlanId: String = ""
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd")

    var activityType = MutableLiveData<ActivityType>()
    var locationName = MutableLiveData<String>()
    var activityName = MutableLiveData<String>()
    var note = MutableLiveData<String?>()
    var latitude = MutableLiveData<Double>()
    var longitude = MutableLiveData<Double>()
    var time = MutableLiveData<Long>()
    var date = MutableLiveData<Long>()
    var navigateBack = MutableLiveData(false)

    var createTravelActivityGeofence = MutableLiveData<TravelActivity?>()

    val showSnackBarInt = MutableLiveData<Int>()

    fun reset() {
        activityId = null
        travelPlanId = ""
        activityType = MutableLiveData<ActivityType>()
        activityName = MutableLiveData<String>()
        locationName = MutableLiveData<String>()
        latitude = MutableLiveData<Double>()
        longitude = MutableLiveData<Double>()
        time = MutableLiveData<Long>()
        date = MutableLiveData<Long>()
        navigateBack = MutableLiveData(false)
    }

    fun navigateBack() {
        navigateBack.value = true
    }

    fun init(travelPlanId: String, day: String, id: String) {
        this.travelPlanId = travelPlanId
        if (id != "") {
            activityId = id
            viewModelScope.launch {
                val activity = database.activityDao.get(id) ?: return@launch
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
                    longitude.value = activity.location?.longitude
                }
                if (latitude.value == null) {
                    latitude.value = activity.location?.latitude
                }
                if (note.value == null) {
                    note.value = activity.note
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
                Location(latitude.value!!, longitude.value!!, locationName.value!!)
            } else {
                null
            }
            if (activityId == null) {
                val travelActivity = TravelActivity(
                    activityName.value!!,
                    activityType.value!!,
                    Date(date.value!!),
                    time.value!!,
                    travelPlanId,
                    location,
                    note.value
                )
                viewModelScope.launch {
                    database.activityDao.insert(travelActivity)
                    createTravelActivityGeofence.value = travelActivity
                    updateWeather(travelActivity)
                }
            } else {
                val travelActivity = TravelActivity(
                    activityName.value!!,
                    activityType.value!!,
                    Date(date.value!!),
                    time.value!!,
                    travelPlanId,
                    location,
                    note.value,
                    null,
                    activityId!!
                )
                viewModelScope.launch {
                    val oldActivity = database.activityDao.get(activityId!!)
                    database.activityDao.update(travelActivity)
                    if (oldActivity?.location?.locationName != travelActivity.location?.locationName) {
                        createTravelActivityGeofence.value = travelActivity
                    } else {
                        navigateBack()
                    }
                    updateWeather(travelActivity)
                }
            }
        }
    }

    fun onCancel() {
        navigateBack()
    }

    fun receivedSaveEvent() {
        createTravelActivityGeofence.value = null
    }

    private suspend fun updateWeather(travelActivity: TravelActivity) {
        travelActivity.location?.let {
            val data = Network.weatherApiService.getWeatherByLatLong(
                it.latitude,
                it.longitude
            ).await()
            val weather = JSONObject(data).getWeather(travelActivity.date)
            database.activityDao.updateWeather(travelActivity.id, weather, 0)
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
//
//    class Factory(
//        private val app: Application
//    ) :
//        ViewModelProvider.Factory {
//        override fun <T : ViewModel> create(modelClass: Class<T>): T {
//            if (modelClass.isAssignableFrom(CreateEditActivityViewModel::class.java)) {
//                @Suppress("UNCHECKED_CAST")
//                return CreateEditActivityViewModel(app, get() as LocalDatabase) as T
//            }
//            throw IllegalArgumentException("Unable to construct viewmodel")
//        }
//    }
}