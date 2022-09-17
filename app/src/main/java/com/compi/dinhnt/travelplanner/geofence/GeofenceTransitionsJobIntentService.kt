package com.compi.dinhnt.travelplanner.geofence

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.compi.dinhnt.travelplanner.database.LocalDatabase
import com.compi.dinhnt.travelplanner.model.TravelActivity
import com.compi.dinhnt.travelplanner.utils.sendLocationReachedNotification
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import org.koin.android.ext.android.inject
import kotlin.math.log


class GeofenceTransitionsJobIntentService : JobIntentService(), CoroutineScope {

    private var coroutineJob: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + coroutineJob

    companion object {
        val TRAVEL_ACTIVITY_ID = "travel_activity_id"
        private const val JOB_ID = 573

        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(
                context,
                GeofenceTransitionsJobIntentService::class.java, JOB_ID,
                intent
            )
        }
    }

    override fun onHandleWork(intent: Intent) {
        intent.extras?.let {
            val travelActivityId = it.get(TRAVEL_ACTIVITY_ID) as String
            sendNotification(travelActivityId)
        }
    }

    private fun sendNotification(travelActivityId: String) {
        //Get the local repository instance
        val localDatabase: LocalDatabase by inject()
//        Interaction to the repository has to be through a coroutine scope
        CoroutineScope(coroutineContext).launch(SupervisorJob()) {
            //get the reminder with the request id
            try {
                val result = localDatabase.activityDao.get(travelActivityId) ?: return@launch
                //send a notification to the user with the reminder details
                sendLocationReachedNotification(
                    this@GeofenceTransitionsJobIntentService, result
                )
            } catch (e: Exception) {
                Log.e("GeofenceTransitions", e.stackTraceToString())
            }
        }

    }
}
