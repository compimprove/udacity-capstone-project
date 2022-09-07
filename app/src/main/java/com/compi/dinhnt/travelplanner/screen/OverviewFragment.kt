package com.compi.dinhnt.travelplanner.screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.compi.dinhnt.travelplanner.R
import com.compi.dinhnt.travelplanner.adapter.TravelPlanListAdapter
import com.compi.dinhnt.travelplanner.database.getDatabase
import com.compi.dinhnt.travelplanner.database.seedDatabase
import com.compi.dinhnt.travelplanner.databinding.FragmentOverviewBinding
import com.compi.dinhnt.travelplanner.view_model.OverviewViewModel


class OverviewFragment : Fragment() {

    private val viewModel: OverviewViewModel by lazy {
        val activity = requireNotNull(this.activity)
        ViewModelProvider(
            this,
            OverviewViewModel.Factory(activity.application)
        )[OverviewViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentOverviewBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.travelPlanRecyclerView.adapter =
            TravelPlanListAdapter(TravelPlanListAdapter.OnClickListener {
                this.findNavController().navigate(OverviewFragmentDirections.actionShowDetailTravelPlan(it.id))
            })
        binding.lifecycleOwner = this
        return binding.root
    }
}