package com.compi.dinhnt.travelplanner.screen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.compi.dinhnt.travelplanner.adapter.TravelPlanWithActivitiesPageAdapter
import com.compi.dinhnt.travelplanner.databinding.FragmentTravelPlanDetailBinding
import com.compi.dinhnt.travelplanner.view_model.TravelPlanDetailViewModel
import kotlinx.android.synthetic.main.fragment_travel_plan_detail.view.*


class TravelPlanDetailFragment : Fragment() {

    private lateinit var viewModel: TravelPlanDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentTravelPlanDetailBinding.inflate(inflater, container, false)
        val activity = requireNotNull(this.activity)
        val travelPlanId = TravelPlanDetailFragmentArgs.fromBundle(requireArguments()).travelPlanId
        viewModel = ViewModelProvider(
            this,
            TravelPlanDetailViewModel.Factory(activity.application, travelPlanId)
        )[TravelPlanDetailViewModel::class.java]
        val adapter = TravelPlanWithActivitiesPageAdapter(
            requireActivity(),
            binding.tabLayout,
            binding.viewPager,
            { day -> onCreateActivity(day) },
            { day, activityId -> onEditActivity(day, activityId) }
        )
        binding.viewPager.adapter = adapter
        binding.emptyActivityConstraintLayout.buttonCreateActivity.setOnClickListener {
            onCreateActivity("")
        }
        viewModel.travelPlanWithActivity.observe(viewLifecycleOwner) { data ->
            binding.loadingLayout.visibility = GONE
            if (data.activities.isEmpty()) {
                binding.emptyActivityConstraintLayout.visibility = VISIBLE
                binding.detailActivityLinearLayout.visibility = GONE
            } else {
                binding.emptyActivityConstraintLayout.visibility = GONE
                binding.detailActivityLinearLayout.visibility = VISIBLE
                adapter.setTravelPlan(data)
            }
        }
        viewModel.refreshData()
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    private fun onCreateActivity(day: String) {
        viewModel.travelPlanWithActivity.value?.travelPlanCTO?.id?.let {
            this.findNavController().navigate(
                TravelPlanDetailFragmentDirections.actionToCreateEditActivity(it, day, "")
            )
        } ?: Log.e("Error", "navigate to createEditActivity")
    }

    private fun onEditActivity(day: String, activityId: String) {
        viewModel.travelPlanWithActivity.value?.travelPlanCTO?.id?.let {
            this.findNavController().navigate(
                TravelPlanDetailFragmentDirections.actionToCreateEditActivity(it, day, activityId)
            )
        } ?: Log.e("Error", "navigate to createEditActivity")
    }
}