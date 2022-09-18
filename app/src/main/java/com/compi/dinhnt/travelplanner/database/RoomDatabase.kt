package com.compi.dinhnt.travelplanner.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.compi.dinhnt.travelplanner.NewUserConstant.FILE_NAME
import com.compi.dinhnt.travelplanner.NewUserConstant.IS_NEW_USER_DATA
import com.compi.dinhnt.travelplanner.model.*
import com.compi.dinhnt.travelplanner.network.Network
import com.compi.dinhnt.travelplanner.network.getWeather
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.await
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


@Database(entities = [TravelPlanCTO::class, TravelActivity::class], version = 17)
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
        "https://images.pexels.com/photos/313782/pexels-photo-313782.jpeg?auto=compress&cs=tinysrgb&h=350"
    )
    db.travelPlanDao.insert(
        newYorkSample
    )

    val fistDay = Date(Date().year, Date().month, Date().date + 1)
    val secondDay = Date(Date().year, Date().month, Date().date + 2)
    val thirdDay = Date(Date().year, Date().month, Date().date + 3)
    db.activityDao.insert(
        TravelActivity(
            "American Airlines",
            ActivityType.FLIGHT,
            fistDay,
            8 * 60,
            newYorkSample.id, null,
            "Flight to NewYork\nPrepare packing the night before"
        )
    )
    db.activityDao.insert(
        TravelActivity(
            "Eating at Sant Ambroeus",
            ActivityType.EATING,
            fistDay,
            10 * 60 + 30,
            newYorkSample.id,
            Location(40.73551117438808, -74.00312630295032, "Sant Ambroeus"), null
        )
    )
    db.activityDao.insert(
        TravelActivity(
            "Visit Statue of Liberty",
            ActivityType.PLAYING,
            fistDay,
            13 * 60 + 30,
            newYorkSample.id,
            Location(40.689247, -74.044502, "Statue of Liberty"),
            null
        )
    )
    db.activityDao.insert(
        TravelActivity(
            "Visit Central Park",
            ActivityType.PLAYING,
            fistDay,
            16 * 60,
            newYorkSample.id,
            Location(40.773243500601325, -73.97303083041399, "Central Park"), null
        )
    )
    db.activityDao.insert(
        TravelActivity(
            "Dinner at Liberty Bistro",
            ActivityType.EATING,
            fistDay,
            20 * 60 + 30,
            newYorkSample.id,
            Location(40.71325080815677, -74.01521596524701, "Liberty Bistro"), null
        )
    )
    db.activityDao.insert(
        TravelActivity(
            "Back to Hotel Edison",
            ActivityType.HOTEL,
            fistDay,
            23 * 60 + 30,
            newYorkSample.id,
            Location(40.75988988445104, -73.98616183084272, "Hotel Edison"), null
        )
    )

    db.activityDao.insert(
        TravelActivity(
            "Visit The Empire State Building",
            ActivityType.PLAYING,
            secondDay,
            8 * 60,
            newYorkSample.id, Location(
                40.74856239882947, -73.98564294433514,
                "The Empire State Building"
            ),
            null
        )
    )
    db.activityDao.insert(
        TravelActivity(
            "Lunch at Kochi",
            ActivityType.EATING,
            secondDay,
            11 * 60,
            newYorkSample.id, Location(
                40.762130660779476, -73.99352286112716,
                "Kochi"
            ),
            null
        )
    )
    db.activityDao.insert(
        TravelActivity(
            "Visit Fifth Avenue",
            ActivityType.PLAYING,
            secondDay,
            14 * 60,
            newYorkSample.id, Location(
                40.7746095742669, -73.96560697317032,
                "Fifth Avenue"
            ),
            null
        )
    )
    db.activityDao.insert(
        TravelActivity(
            "Dinner at Dave's Hot Chicken",
            ActivityType.EATING,
            secondDay,
            18 * 60,
            newYorkSample.id, Location(
                40.76596097268966, -73.98313161399606,
                "Dave's Hot Chicken"
            ),
            null
        )
    )
    db.activityDao.insert(
        TravelActivity(
            "Back to Hotel Edison",
            ActivityType.HOTEL,
            secondDay,
            23 * 60 + 30,
            newYorkSample.id,
            Location(40.75988988445104, -73.98616183084272, "Hotel Edison"), null
        )
    )



    db.activityDao.insert(
        TravelActivity(
            "Visit Rockefeller Center",
            ActivityType.PLAYING,
            thirdDay,
            8 * 60,
            newYorkSample.id, Location(
                40.758959600355716, -73.97863068666301,
                "Rockefeller Center"
            ),
            null
        )
    )
    db.activityDao.insert(
        TravelActivity(
            "Visit Metropolitan Museum of Art",
            ActivityType.PLAYING,
            thirdDay,
            10 * 60,
            newYorkSample.id, Location(
                40.77959093867678, -73.96330837502194,
                "Metropolitan Museum of Art"
            ),
            null
        )
    )
    db.activityDao.insert(
        TravelActivity(
            "Lunch at Ellen's Stardust Diner",
            ActivityType.EATING,
            thirdDay,
            12 * 60,
            newYorkSample.id, Location(
                40.762083563703754, -73.9834356020067,
                "Ellen's Stardust Diner"
            ),
            null
        )
    )
    db.activityDao.insert(
        TravelActivity(
            "Visit Grand Central Station",
            ActivityType.PLAYING,
            thirdDay,
            14 * 60,
            newYorkSample.id, Location(
                40.75602417970425, -73.9766618101,
                "Grand Central Station"
            ),
            null
        )
    )
    db.activityDao.insert(
        TravelActivity(
            "Dinner at Gardenia Terrace",
            ActivityType.EATING,
            thirdDay,
            18 * 60,
            newYorkSample.id, Location(
                40.767719389337245, -73.98940361735038,
                "Gardenia Terrace"
            ),
            null
        )
    )
    db.activityDao.insert(
        TravelActivity(
            "Back to Hotel Edison",
            ActivityType.HOTEL,
            thirdDay,
            23 * 60 + 30,
            newYorkSample.id,
            Location(40.75988988445104, -73.98616183084272, "Hotel Edison"), null
        )
    )
    db.activityDao.getAll().forEach {
        updateWeather(it, db.activityDao)
    }
}

private suspend fun updateWeather(travelActivity: TravelActivity, activityDao: TravelActivityDao) {
    travelActivity.location?.let {
        val data = Network.weatherApiService.getWeatherByLatLong(
            it.latitude,
            it.longitude
        ).await()
        val weather = JSONObject(data).getWeather(travelActivity.date)
        activityDao.updateWeather(travelActivity.id, weather, 0)
    }
}