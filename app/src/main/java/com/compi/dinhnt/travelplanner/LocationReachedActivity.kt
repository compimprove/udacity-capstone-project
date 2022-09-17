package com.compi.dinhnt.travelplanner

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.compi.dinhnt.travelplanner.databinding.LocationReachedActivityBinding
import com.compi.dinhnt.travelplanner.model.TravelActivity
import com.compi.dinhnt.travelplanner.view_model.LocationReachedActivityViewModel
import org.koin.android.ext.android.inject

/**
 * Activity that displays the reminder details after the user clicks on the notification
 */
class LocationReachedActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context, travelActivity: TravelActivity): Intent {
            val intent = Intent(context, LocationReachedActivity::class.java)
            intent.putExtra(EXTRA_TravelActivityId, travelActivity.id)
            return intent
        }
    }

    private val viewModel: LocationReachedActivityViewModel by inject()

    private lateinit var binding: LocationReachedActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.location_reached_activity
        )
//        val travelActivityId = intent.extras?.get(EXTRA_TravelActivityId) as String
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        viewModel.refreshTravelActivity("travelActivityId")
        binding.goToPlanButton.setOnClickListener {
            viewModel.currentTravelActivity.value?.let {
                startActivity(MainActivity.newIntent(applicationContext, it.travelPlanId))
            }
        }
    }
}
