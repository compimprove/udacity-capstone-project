package com.compi.dinhnt.travelplanner.screen

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.compi.dinhnt.travelplanner.ACTION_GEOFENCE_EVENT
import com.compi.dinhnt.travelplanner.BuildConfig
import com.compi.dinhnt.travelplanner.R
import com.compi.dinhnt.travelplanner.databinding.FragmentCreateEditActivityBinding
import com.compi.dinhnt.travelplanner.geofence.GeofenceBroadcastReceiver
import com.compi.dinhnt.travelplanner.model.TravelActivity
import com.compi.dinhnt.travelplanner.model.ActivityType
import com.compi.dinhnt.travelplanner.setDisplayHomeAsUpEnabled
import com.compi.dinhnt.travelplanner.view_model.CreateEditActivityViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_LOW_POWER
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import org.koin.android.ext.android.bind
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*

class CreateEditActivityFragment : Fragment() {

    private lateinit var geofencingClient: GeofencingClient
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(requireActivity(), GeofenceBroadcastReceiver::class.java)
        intent.action = ACTION_GEOFENCE_EVENT
        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
    private var pendingTravelActivityToAdd: TravelActivity? = null
    private lateinit var binding: FragmentCreateEditActivityBinding
    private val viewModel: CreateEditActivityViewModel by inject()
    private val formatter = SimpleDateFormat("yyyy-MM-dd")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateEditActivityBinding.inflate(inflater, container, false)
        try {
            val travelPlanId =
                CreateEditActivityFragmentArgs.fromBundle(requireArguments()).travelPlanId
            val day = CreateEditActivityFragmentArgs.fromBundle(requireArguments()).day
            val activityId =
                CreateEditActivityFragmentArgs.fromBundle(requireArguments()).activityId
            viewModel.init(travelPlanId, day, activityId)
        } catch (e: Exception) {
        }
        geofencingClient = LocationServices.getGeofencingClient(requireActivity())
        makeButtons()
        makeDateTimePicker()
        binding.buttonChooseLocation.setOnClickListener {
            findNavController().navigate(
                CreateEditActivityFragmentDirections.actionToChooseLocationFragment()
            )
        }
        viewModel.showSnackBarInt.observe(viewLifecycleOwner) {
            Snackbar.make(this.requireView(), getString(it), Snackbar.LENGTH_LONG).show()
        }
        viewModel.time.observe(viewLifecycleOwner) { time ->
            time?.let {
                binding.inputTime.text = "${time / 60}:${String.format("%02d", time % 60)}"
            } ?: run { binding.inputTime.text = "Choose Time" }
        }
        viewModel.date.observe(viewLifecycleOwner) { date ->
            date?.let {
                binding.inputDate.text = formatter.format(Date(date))
            } ?: run { binding.inputDate.text = "Choose Date" }
        }
        viewModel.createTravelActivityGeofence.observe(viewLifecycleOwner) {
            it?.let { travelActivity ->
                viewModel.receivedSaveEvent()
                checkPermissionAndAddGeofencingRequest(travelActivity)
            }
        }
        viewModel.navigateBack.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().popBackStack()
            }
        }
        setDisplayHomeAsUpEnabled(true)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    private fun loseFocus() {
        binding.nameTextField.clearFocus()
    }

    private fun makeDateTimePicker() {
        binding.inputTime.setOnClickListener {
            loseFocus()
            val picker =
                MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(9)
                    .setMinute(0)
                    .setTitleText("Choose Your Date")
                    .build()
            picker.show(childFragmentManager, CHOOSE_TIME)
            picker.addOnPositiveButtonClickListener {
                viewModel.time.value = (picker.hour * 60 + picker.minute).toLong()
            }
        }

        binding.inputDate.setOnClickListener {
            loseFocus()
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select date")
                    .build()
            datePicker.show(childFragmentManager, CHOOSE_DATE)
            datePicker.addOnPositiveButtonClickListener {
                viewModel.date.value = it
            }
        }
    }

    private fun makeButtons() {
        val mapButton = mapOf(
            binding.buttonEating to Triple(R.color.orange_500, R.color.white, ActivityType.EATING),
            binding.buttonFlight to Triple(R.color.green_500, R.color.black, ActivityType.FLIGHT),
            binding.buttonTaxi to Triple(R.color.blue_500, R.color.white, ActivityType.TAXI),
            binding.buttonHotel to Triple(R.color.yellow_500, R.color.black, ActivityType.HOTEL),
            binding.buttonPlaying to Triple(R.color.red_500, R.color.white, ActivityType.PLAYING),
        )
        val mapLabel = mapOf(
            ActivityType.HOTEL to "Hotel Name",
            ActivityType.FLIGHT to "Flight Name",
            ActivityType.EATING to "Restaurant Name",
            ActivityType.PLAYING to "Activity Name",
            ActivityType.TAXI to "Taxi Name",
        )
        for ((button, _) in mapButton) {
            button.setBackgroundColor(resources.getColor(R.color.white))
            button.setTextColor(resources.getColor(R.color.black))
            button.setOnClickListener {
                loseFocus()
                mapButton[button]?.let {
                    viewModel.activityType.value = it.third
                }
            }
        }
        viewModel.activityType.observe(viewLifecycleOwner) { activityType ->
            if (activityType == null) return@observe
            for ((e, value) in mapButton) {
                if (activityType == value.third) {
                    e.setBackgroundColor(resources.getColor(value.first))
                    e.setTextColor(resources.getColor(value.second))
                } else {
                    e.setBackgroundColor(resources.getColor(R.color.white))
                    e.setTextColor(resources.getColor(R.color.black))
                }
            }
            binding.labelNameActivityTextView.text = "${mapLabel[activityType]} *"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.reset()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TURN_DEVICE_LOCATION_ON) {
            checkDeviceLocationSettings(false) {
                pendingTravelActivityToAdd?.let { addGeofencingRequest(it) }
                viewModel.navigateBack()
            }
        }
    }

    private fun checkPermissionAndAddGeofencingRequest(travelActivity: TravelActivity) {
        pendingTravelActivityToAdd = travelActivity
        if (foregroundAndBackgroundLocationPermissionApproved()) {
            checkDeviceLocationSettings {
                addGeofencingRequest(travelActivity)
                viewModel.navigateBack()
            }
        } else {
            requestForegroundAndBackgroundLocationPermission()
        }
    }

    private fun foregroundAndBackgroundLocationPermissionApproved(): Boolean {
        context?.let {
            val foregroundLocationApproved = (
                    PackageManager.PERMISSION_GRANTED ==
                            ActivityCompat.checkSelfPermission(
                                it,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ))
            val backgroundPermissionApproved =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    PackageManager.PERMISSION_GRANTED ==
                            ActivityCompat.checkSelfPermission(
                                it, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                            )
                } else {
                    true
                }
            return foregroundLocationApproved && backgroundPermissionApproved
        } ?: return false
    }

    private fun requestForegroundAndBackgroundLocationPermission() {
        requestPermissions(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
            else arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) REQUEST_LOCATION_AND_BACKGROUND_LOCATION_PERMISSION
            else REQUEST_LOCATION_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (
            grantResults.isEmpty() ||
            grantResults[0] == PackageManager.PERMISSION_DENIED ||
            (requestCode == REQUEST_LOCATION_AND_BACKGROUND_LOCATION_PERMISSION &&
                    grantResults[1] ==
                    PackageManager.PERMISSION_DENIED)
        ) {
            Snackbar.make(
                binding.root,
                R.string.permission_denied_explanation, Snackbar.LENGTH_INDEFINITE
            )
                .setAction(R.string.settings) {
                    startActivity(Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                }.show()
        } else {
            checkDeviceLocationSettings {
                pendingTravelActivityToAdd?.let { addGeofencingRequest(it) }
                viewModel.navigateBack()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun checkDeviceLocationSettings(resolve: Boolean = true, onSuccess: () -> Unit = {}) {
        val locationRequest = LocationRequest.create().apply {
            priority = PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                try {
                    startIntentSenderForResult(
                        exception.resolution.intentSender,
                        REQUEST_TURN_DEVICE_LOCATION_ON,
                        null, 0, 0, 0, null
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d(
                        TAG,
                        "Error geting location settings resolution: " + sendEx.message
                    )
                }
            } else {
                Snackbar.make(
                    binding.root,
                    R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    checkDeviceLocationSettings {
                        pendingTravelActivityToAdd?.let { it1 -> addGeofencingRequest(it1) }
                        viewModel.navigateBack()
                    }
                }.show()
            }
        }
        locationSettingsResponseTask.addOnCompleteListener {
            if (it.isSuccessful) {
                onSuccess()
            }
        }
    }

    private fun addGeofencingRequest(travelActivity: TravelActivity) {
        if (travelActivity.location != null && context != null) {
            val geofence = Geofence.Builder()
                .setRequestId(travelActivity.id)
                .setCircularRegion(
                    travelActivity.location.latitude,
                    travelActivity.location.longitude,
                    GEOFENCE_RADIUS_IN_METERS
                )
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build()

            val geofencingRequest = GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build()

            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                    requireContext(), R.string.geofences_not_added,
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            val context = requireContext()
            val travelActivityId = travelActivity.id
            geofencingClient.removeGeofences(listOf(travelActivity.id)).run {
                addOnSuccessListener {
                    Log.i(TAG, "remove geofencing request success $travelActivityId")
                }
                addOnFailureListener {
                    Log.i(TAG, "remove geofencing request fail $travelActivityId")
                }
            }
            geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)?.run {
                addOnSuccessListener {
                    Toast.makeText(
                        context,
                        R.string.geofences_added,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
                addOnFailureListener {
                    Toast.makeText(
                        context,
                        R.string.geofences_not_added,
                        Toast.LENGTH_SHORT
                    ).show()
                    if ((it.message != null)) {
                        Log.w(TAG, it.message!!)
                    }
                }
            }
        }
    }

    companion object {
        val TAG = "CreateEditActivity"
        val REQUEST_LOCATION_PERMISSION = 0
        val REQUEST_LOCATION_AND_BACKGROUND_LOCATION_PERMISSION = 1
        private val CHOOSE_TIME = "chooseTime"
        private val CHOOSE_DATE = "chooseDate"
        private val REQUEST_TURN_DEVICE_LOCATION_ON = 29
        private val GEOFENCE_RADIUS_IN_METERS = 100f

        @JvmStatic
        fun newInstance() =
            CreateEditActivityFragment()
    }
}