package com.compi.dinhnt.travelplanner.screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.compi.dinhnt.travelplanner.R
import com.compi.dinhnt.travelplanner.databinding.FragmentCreateEditActivityBinding
import com.compi.dinhnt.travelplanner.model.ActivityType
import com.compi.dinhnt.travelplanner.view_model.CreateEditActivityViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

class CreateEditActivityFragment : Fragment() {

    private lateinit var binding: FragmentCreateEditActivityBinding
    private val viewModel: CreateEditActivityViewModel by lazy {
        val activity = requireNotNull(this.activity)
        ViewModelProvider(
            requireActivity(),
            CreateEditActivityViewModel.Factory(activity.application)
        )[CreateEditActivityViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateEditActivityBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        makeButtons()
        makeDateTimePicker()
        binding.buttonChooseLocation.setOnClickListener {
            findNavController().navigate(CreateEditActivityFragmentDirections.actionToChooseLocationFragment())
        }
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
                    .setHour(12)
                    .setMinute(10)
                    .setTitleText("Choose Your Date")
                    .build()
            picker.show(childFragmentManager, CHOOSE_TIME)
            picker.addOnPositiveButtonClickListener {
                binding.inputTime.text = "${picker.hour}: ${picker.minute}"
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
                binding.inputDate.text = "${datePicker.headerText}"
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
                    viewModel.setActivityType(it.third)
                }
            }
        }
        viewModel.activityType.observe(viewLifecycleOwner) { activityType ->
            for ((e, value) in mapButton) {
                if (activityType == value.third) {
                    e.setBackgroundColor(resources.getColor(value.first))
                    e.setTextColor(resources.getColor(value.second))
                } else {
                    e.setBackgroundColor(resources.getColor(R.color.white))
                    e.setTextColor(resources.getColor(R.color.black))
                }
            }
            binding.labelNameActivityTextView.text = mapLabel[activityType]
        }
    }


    companion object {
        private val CHOOSE_TIME = "chooseTime"
        private val CHOOSE_DATE = "chooseDate"

        @JvmStatic
        fun newInstance() =
            CreateEditActivityFragment()
    }
}