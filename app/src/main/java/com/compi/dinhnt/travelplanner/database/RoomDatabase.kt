package com.compi.dinhnt.travelplanner.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.compi.dinhnt.travelplanner.NewUserConstant.FILE_NAME
import com.compi.dinhnt.travelplanner.NewUserConstant.IS_NEW_USER_DATA
import com.compi.dinhnt.travelplanner.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate.now
import java.util.*


@Dao
interface TravelPlanDao {
    @Transaction
    @Query("select * from travelplan")
    fun getTravelPlansWithActivity(): LiveData<List<TravelPlanWithActivity>>

    @Transaction
    @Query("select * from travelplan where travelplan.id = :id")
    fun travelPlanWithActivity(id: String): LiveData<TravelPlanWithActivity>

    @Transaction
    @Query("select * from travelplan where travelplan.id = :id")
    suspend fun getTravelPlanWithActivity(id: String): TravelPlanWithActivity

    @Query("select * from travelplan")
    fun getTravelPlans(): LiveData<List<TravelPlanCTO>>

    @Update
    suspend fun update(travelPlanCTO: TravelPlanCTO)

    @Insert
    suspend fun insert(travelPlanCTO: TravelPlanCTO)

    @Query("DELETE FROM travelplan")
    suspend fun clear()
}

@Dao
interface TravelActivityDao {
    @Insert
    suspend fun insert(travelActivity: TravelActivity)

    @Query("select * from travelactivity where travelactivity.id = :id")
    suspend fun get(id: String): TravelActivity?

    @Query("select * from travelactivity")
    suspend fun getAll(): List<TravelActivity>

    @Update
    suspend fun update(travelActivity: TravelActivity)

    @Query(
        "update travelactivity " +
                "set weatherDetail = :weatherDetail, weatherTimeStamp=:weatherTimeStamp " +
                "where id=:id"
    )
    suspend fun updateWeather(id: String, weatherDetail: WeatherDetail?, weatherTimeStamp: Long?)

    @Query("DELETE FROM travelactivity")
    suspend fun clear()
}


@Database(entities = [TravelPlanCTO::class, TravelActivity::class], version = 15)
@TypeConverters(
    ActivityDateConverter::class,
    MapStringStringConverter::class,
    ListWeatherConverter::class,
    LatLngConverter::class
)
abstract class LocalDatabase : RoomDatabase() {
    abstract val travelPlanDao: TravelPlanDao
    abstract val activityDao: TravelActivityDao
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
                .build()
        }
        val sharedPref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
//        if (true) {
        if (sharedPref.getBoolean(IS_NEW_USER_DATA, true)) {
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                seedSampleData(INSTANCE)
                with(sharedPref.edit()) {
                    putBoolean(IS_NEW_USER_DATA, false)
                    apply()
                }
            }
        }
    }
    return INSTANCE
}

suspend fun seedSampleData(db: LocalDatabase) {
    db.travelPlanDao.clear()
    db.activityDao.clear()
    val newYorkSample = TravelPlanCTO(
        "NewYork Sample Trip",
    )
    db.travelPlanDao.insert(
        newYorkSample
    )

    val today = Date(Date().year, Date().month, Date().date)
    val tomorrow = Date(Date().year, Date().month, Date().date + 1)
    val twoDayAfter = Date(Date().year, Date().month, Date().date + 2)
    db.activityDao.insert(
        TravelActivity(
            "American Airlines",
            ActivityType.FLIGHT,
            today,
            8 * 60,
            newYorkSample.id, null,
            "Flight to NewYork\nPrepare packing the night before"
        )
    )
    db.activityDao.insert(
        TravelActivity(
            "Eating at Sant Ambroeus",
            ActivityType.EATING,
            today,
            10 * 60 + 30,
            newYorkSample.id,
            Location(40.712776, -74.005974, "Sant Ambroeus"), null
        )
    )
    db.activityDao.insert(
        TravelActivity(
            "Visit Statue of Liberty",
            ActivityType.PLAYING,
            today,
            13 * 60 + 30,
            newYorkSample.id,
            Location(40.712776, -74.005974, "Statue of Liberty"),
            null
        )
    )
    db.activityDao.insert(
        TravelActivity(
            "Visit Central Park",
            ActivityType.PLAYING,
            today,
            16 * 60,
            newYorkSample.id,
            Location(40.712776, -74.005974, "Central Park"), null
        )
    )
    db.activityDao.insert(
        TravelActivity(
            "Dinner at Liberty Bistro",
            ActivityType.EATING,
            today,
            20 * 60 + 30,
            newYorkSample.id,
            Location(40.712776, -74.005974, "Liberty Bistro"), null
        )
    )
    db.activityDao.insert(
        TravelActivity(
            "Back to Hotel Edison",
            ActivityType.HOTEL,
            today,
            23 * 60 + 30,
            newYorkSample.id,
            Location(40.712776, -74.005974, "Hotel Edison"), null
        )
    )

    db.activityDao.insert(
        TravelActivity(
            "Visit The Empire State Building",
            ActivityType.FLIGHT,
            tomorrow,
            8 * 60,
            newYorkSample.id, Location(
                40.712776, -74.005974,
                "The Empire State Building"
            ),
            null
        )
    )
    db.activityDao.insert(
        TravelActivity(
            "Lunch at Kochi",
            ActivityType.EATING,
            tomorrow,
            11 * 60,
            newYorkSample.id, Location(
                40.712776, -74.005974,
                "Kochi"
            ),
            null
        )
    )
    db.activityDao.insert(
        TravelActivity(
            "Visit Fifth Avenue",
            ActivityType.PLAYING,
            tomorrow,
            14 * 60,
            newYorkSample.id, Location(
                40.712776, -74.005974,
                "Fifth Avenue"
            ),
            null
        )
    )
    db.activityDao.insert(
        TravelActivity(
            "Dinner at Dave's Hot Chicken",
            ActivityType.EATING,
            tomorrow,
            18 * 60,
            newYorkSample.id, Location(
                40.712776, -74.005974,
                "Dave's Hot Chicken"
            ),
            null
        )
    )
    db.activityDao.insert(
        TravelActivity(
            "Back to Hotel Edison",
            ActivityType.HOTEL,
            tomorrow,
            23 * 60 + 30,
            newYorkSample.id,
            Location(40.712776, -74.005974, "Hotel Edison"), null
        )
    )



    db.activityDao.insert(
        TravelActivity(
            "Visit Rockefeller Center",
            ActivityType.FLIGHT,
            twoDayAfter,
            8 * 60,
            newYorkSample.id, Location(
                40.712776, -74.005974,
                "Rockefeller Center"
            ),
            null
        )
    )
    db.activityDao.insert(
        TravelActivity(
            "Visit Metropolitan Museum of Art",
            ActivityType.FLIGHT,
            twoDayAfter,
            10 * 60,
            newYorkSample.id, Location(
                40.712776, -74.005974,
                "Metropolitan Museum of Art"
            ),
            null
        )
    )
    db.activityDao.insert(
        TravelActivity(
            "Lunch at Ellen's Stardust Diner",
            ActivityType.EATING,
            twoDayAfter,
            12 * 60,
            newYorkSample.id, Location(
                40.712776, -74.005974,
                "Ellen's Stardust Diner"
            ),
            null
        )
    )
    db.activityDao.insert(
        TravelActivity(
            "Visit Grand Central Station",
            ActivityType.PLAYING,
            twoDayAfter,
            14 * 60,
            newYorkSample.id, Location(
                40.712776, -74.005974,
                "Grand Central Station"
            ),
            null
        )
    )
    db.activityDao.insert(
        TravelActivity(
            "Dinner at Gardenia Terrace",
            ActivityType.EATING,
            twoDayAfter,
            18 * 60,
            newYorkSample.id, Location(
                40.712776, -74.005974,
                "Gardenia Terrace"
            ),
            null
        )
    )
    db.activityDao.insert(
        TravelActivity(
            "Back to Hotel Edison",
            ActivityType.HOTEL,
            twoDayAfter,
            23 * 60 + 30,
            newYorkSample.id,
            Location(40.712776, -74.005974, "Hotel Edison"), null
        )
    )

}