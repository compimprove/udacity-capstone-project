package com.compi.dinhnt.travelplanner.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.compi.dinhnt.travelplanner.model.Activity
import com.compi.dinhnt.travelplanner.model.ActivityType
import com.compi.dinhnt.travelplanner.model.TravelPlanCTO
import com.compi.dinhnt.travelplanner.model.TravelPlanWithActivity
import java.util.*


@Dao
interface TravelPlanDao {
    @Transaction
    @Query("select * from travelplan")
    fun getTravelPlansWithActivity(): LiveData<List<TravelPlanWithActivity>>

    @Transaction
    @Query("select * from travelplan where travelplan.id = :id")
    fun travelPlanWithActivity(id: Long): LiveData<TravelPlanWithActivity>

    @Transaction
    @Query("select * from travelplan where travelplan.id = :id")
    suspend fun getTravelPlanWithActivity(id: Long): TravelPlanWithActivity

    @Query("select * from travelplan")
    fun getTravelPlans(): LiveData<List<TravelPlanCTO>>

    @Insert
    suspend fun insert(travelPlanCTO: TravelPlanCTO)

    @Query("DELETE FROM travelplan")
    suspend fun clear()
}

@Dao
interface ActivityDao {
    @Insert
    suspend fun insert(activity: Activity)

    @Query("select * from activity where activity.id = :id")
    suspend fun get(id: String): Activity

    @Update
    suspend fun update(activity: Activity)

    @Query("DELETE FROM activity")
    suspend fun clear()
}


@Database(entities = [TravelPlanCTO::class, Activity::class], version = 9)
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
        TravelPlanCTO(
            1,
            "TravelPlan 1",
        )
    )
    db.travelPlanDao.insert(
        TravelPlanCTO(
            2,
            "TravelPlan 2",
        )
    )
    db.travelPlanDao.insert(
        TravelPlanCTO(
            3,
            "TravelPlan 3",
        )
    )
    db.travelPlanDao.insert(
        TravelPlanCTO(
            4,
            "TravelPlan 4",
        )
    )
    db.activityDao.insert(
        Activity(
            "Activity 1",
            ActivityType.FLIGHT,
            Date(2022 - 1900, 8, 15),
            8 * 60,
            1, null,
            "Do nothing"
        )
    )
    db.activityDao.insert(
        Activity(
            "Activity 1",
            ActivityType.EATING,
            Date(2022 - 1900, 8, 15),
            8 * 60 + 30,
            1,
            null,
            "Do nothing"
        )
    )
    db.activityDao.insert(
        Activity(
            "Activity 2",
            ActivityType.PLAYING,
            Date(2022 - 1900, 8, 15),
            12 * 60 + 30,
            1, null,
            "Do nothing"
        )
    )
    db.activityDao.insert(
        Activity(
            "Activity 3",
            ActivityType.TAXI,
            Date(2022 - 1900, 8, 15),
            20 * 60,
            1, null,
            "Do nothing"
        )
    )
    db.activityDao.insert(
        Activity(
            "Activity 4",
            ActivityType.FLIGHT,
            Date(2022 - 1900, 8, 16),
            8 * 60,
            1, null,
            "Do nothing"
        )
    )
    db.activityDao.insert(
        Activity(
            "Activity 5",
            ActivityType.HOTEL,
            Date(2022 - 1900, 8, 17),
            8 * 60,
            1, null,
            "Do nothing"
        )
    )
}