package com.compi.dinhnt.travelplanner.view_model

import android.app.Application
import androidx.lifecycle.*
import com.compi.dinhnt.travelplanner.model.ActivityType

class CreateEditActivityViewModel(app: Application) : AndroidViewModel(app) {
    private val _activityType = MutableLiveData(ActivityType.HOTEL)


    val activityType: LiveData<ActivityType>
        get() = _activityType

    fun setActivityType(typeChosen: ActivityType) {
        if (_activityType.value == typeChosen) return
        _activityType.value = typeChosen
    }

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CreateEditActivityViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CreateEditActivityViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}