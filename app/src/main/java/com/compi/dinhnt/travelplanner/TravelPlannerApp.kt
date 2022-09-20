package com.compi.dinhnt.travelplanner

import android.app.Application
import com.compi.dinhnt.travelplanner.database.LocalDatabase
import com.compi.dinhnt.travelplanner.database.getDatabase
import com.compi.dinhnt.travelplanner.view_model.CreateEditActivityViewModel
import com.compi.dinhnt.travelplanner.view_model.LocationReachedActivityViewModel
import com.compi.dinhnt.travelplanner.view_model.OverviewViewModel
import com.compi.dinhnt.travelplanner.view_model.TravelPlanDetailViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel

class TravelPlannerApp : Application() {

    override fun onCreate() {
        super.onCreate()

        /**
         * use Koin Library as a service locator
         */
        val myModule = module {
            viewModel {
                LocationReachedActivityViewModel(
                    get(),
                    get() as LocalDatabase
                )
            }
            viewModel {
                OverviewViewModel(
                    get(),
                    get() as LocalDatabase
                )
            }
            single {
                CreateEditActivityViewModel(
                    get(),
                    get() as LocalDatabase
                )
            }
            single {
                TravelPlanDetailViewModel(
                    get(),
                    get() as LocalDatabase
                )
            }
            single { getDatabase(this@TravelPlannerApp) }
            single { LatLng(37.424, -122.079) }
        }

        startKoin {
            androidContext(this@TravelPlannerApp)
            modules(listOf(myModule))
        }
    }
}