package com.compi.dinhnt.travelplanner.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.compi.dinhnt.travelplanner.ACTION_GEOFENCE_EVENT
import com.compi.dinhnt.travelplanner.R
import com.compi.dinhnt.travelplanner.geofence.GeofenceTransitionsJobIntentService.Companion.TRAVEL_ACTIVITY_ID
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

/**
 * Triggered by the Geofence.  Since we can have many Geofences at once, we pull the request
 * ID from the first Geofence, and locate it within the cached data in our Room DB
 *
 * Or users can add the reminders and then close the app, So our app has to run in the background
 * and handle the geofencing in the background.
 * To do that you can use https://developer.android.com/reference/android/support/v4/app/JobIntentService to do that.
 *
 */

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_GEOFENCE_EVENT) {
            val geofencingEvent = GeofencingEvent.fromIntent(intent)

            if (geofencingEvent.hasError()) {
                val errorMessage = errorMessage(context, geofencingEvent.errorCode)
                Log.e(TAG, errorMessage)
                return
            }

            if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER
                && geofencingEvent.triggeringGeofences.isNotEmpty()
            ) {
                geofencingEvent.triggeringGeofences.forEach { geofence ->
                    val travelActivityId = geofence.requestId
                    Log.i(TAG, "geofence entered with activityId $travelActivityId")
                    val geofenceServiceIntent =
                        Intent(context, GeofenceTransitionsJobIntentService::class.java)
                    geofenceServiceIntent.putExtra(TRAVEL_ACTIVITY_ID, travelActivityId)
                    GeofenceTransitionsJobIntentService.enqueueWork(context, geofenceServiceIntent)
                }
            }
        }
    }
}

fun errorMessage(context: Context, errorCode: Int): String {
    val resources = context.resources
    return when (errorCode) {
        GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> resources.getString(
            R.string.geofence_not_available
        )
        GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> resources.getString(
            R.string.geofence_too_many_geofences
        )
        GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> resources.getString(
            R.string.geofence_too_many_pending_intents
        )
        else -> resources.getString(R.string.unknown_geofence_error)
    }
}

private const val TAG = "GeofenceReceiver"
