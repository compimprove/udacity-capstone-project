package com.compi.dinhnt.travelplanner.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.compi.dinhnt.travelplanner.model.Activity
import com.compi.dinhnt.travelplanner.model.ActivityType
import com.compi.dinhnt.travelplanner.model.TravelPlan
import com.compi.dinhnt.travelplanner.model.TravelPlanWithActivity
import java.util.*


@Dao
interface TravelPlanDao {
    @Transaction
    @Query("select * from travelplan")
    fun getTravelPlansWithActivity(): LiveData<List<TravelPlanWithActivity>>

    @Transaction
    @Query("select * from travelplan where travelplan.id = :id")
    fun getTravelPlanWithActivity(id: Long): LiveData<TravelPlanWithActivity>

    @Query("select * from travelplan")
    fun getTravelPlans(): LiveData<List<TravelPlan>>

    @Insert
    suspend fun insert(travelPlan: TravelPlan)

    @Query("DELETE FROM travelplan")
    suspend fun clear()
}

@Dao
interface ActivityDao {
    @Insert
    suspend fun insert(activity: Activity)

    @Query("DELETE FROM activity")
    suspend fun clear()
}


@Database(entities = [TravelPlan::class, Activity::class], version = 7)
@TypeConverters(
    ActivityDateConverter::class,
    MapStringStringConverter::class,
    ListWeatherConverter::class,
    LatLngConverter::class
)
abstract class LocalDatabase : RoomDatabase() {
    abstract val travelPlanDao: TravelPlanDao
    abstract val activityDao: ActivityDao
}

private lateinit var INSTANCE: LocalDatabase

fun getDatabase(context: Context): LocalDatabase {
    synchronized(LocalDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room
                .databaseBuilder(
                    context.applicationContext,
                    LocalDatabase::class.java,
                    "LocalDatabase"
                ).fallbackToDestructiveMigration()
//                .addCallback(object : RoomDatabase.Callback() {
//                    override fun onCreate(db: SupportSQLiteDatabase) {
//                        super.onCreate(db)
//                        val database = getDatabase(context)
//                        GlobalScope.launch {
//                            withContext(Dispatchers.IO) {
//                                seedDatabase(database)
//                            }
//                        }
//                    }
//                })
                .build()
        }
    }
    return INSTANCE
}

suspend fun seedDatabase(db: LocalDatabase) {
    db.travelPlanDao.clear()
    db.activityDao.clear()
    db.travelPlanDao.insert(
        TravelPlan(
            1,
            "TravelPlan 1",
            Date(2022 - 1900, 8, 15),
            Date(2022 - 1900, 8, 25)
        )
    )
    db.travelPlanDao.insert(
        TravelPlan(
            2,
            "TravelPlan 2",
            Date(2022 - 1900, 8, 15),
            Date(2022 - 1900, 8, 25)
        )
    )
    db.travelPlanDao.insert(
        TravelPlan(
            3,
            "TravelPlan 3",
            Date(2022 - 1900, 8, 15),
            Date(2022 - 1900, 8, 25)
        )
    )
    db.travelPlanDao.insert(
        TravelPlan(
            4,
            "TravelPlan 4",
            Date(2022 - 1900, 8, 15),
            Date(2022 - 1900, 8, 25)
        )
    )
    db.activityDao.insert(
        Activity(
            6,
            "Activity 1",
            ActivityType.FLIGHT,
            Date(2022 - 1900, 8, 15),
            8 * 60,
            1, null,
            mapOf("Vanilla" to "Vanilla", "Chocolate" to "Chocolate")
        )
    )
    db.activityDao.insert(
        Activity(
            1,
            "Activity 1",
            ActivityType.EATING,
            Date(2022 - 1900, 8, 15),
            8 * 60 + 30,
            1,
            null,
            mapOf("Vanilla" to "Vanilla", "Chocolate" to "Chocolate")
        )
    )
    db.activityDao.insert(
        Activity(
            2,
            "Activity 2",
            ActivityType.PLAYING,
            Date(2022 - 1900, 8, 15),
            12 * 60 + 30,
            1, null,
            mapOf("Vanilla" to "Vanilla", "Chocolate" to "Chocolate")
        )
    )
    db.activityDao.insert(
        Activity(
            3,
            "Activity 3",
            ActivityType.TAXI,
            Date(2022 - 1900, 8, 15),
            20 * 60,
            1, null,
            mapOf("Vanilla" to "Vanilla", "Chocolate" to "Chocolate")
        )
    )
    db.activityDao.insert(
        Activity(
            4,
            "Activity 4",
            ActivityType.FLIGHT,
            Date(2022 - 1900, 8, 16),
            8 * 60,
            1, null,
            mapOf("Rocky Road" to "Rocky Road", "Rocky Chocolate" to "Rocky Chocolate")
        )
    )
    db.activityDao.insert(
        Activity(
            5,
            "Activity 5",
            ActivityType.HOTEL,
            Date(2022 - 1900, 8, 17),
            8 * 60,
            1, null,
            mapOf("Rocky Road" to "Rocky Road", "Chocolate" to "Chocolate")
        )
    )
}