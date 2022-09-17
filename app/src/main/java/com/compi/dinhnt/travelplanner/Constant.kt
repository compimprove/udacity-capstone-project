package com.compi.dinhnt.travelplanner

object Constant {
    const val WEATHER_API_KEY = BuildConfig.WEATHER_API_KEY
    const val OPEN_WEATHER_URL = "https://api.openweathermap.org/"
}

object NewUserConstant {
    const val FILE_NAME = "newUserFile"
    const val IS_NEW_USER_DATA = "newUserData"
}

val ACTION_GEOFENCE_EVENT = "travelplanner.action.ACTION_GEOFENCE_EVENT"
val EXTRA_TravelActivityId = "EXTRA_ReminderDataItem"
val EXTRA_TravelPlanId = "EXTRA_TravelPlanId"