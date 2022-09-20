package com.compi.dinhnt.travelplanner.screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.compi.dinhnt.travelplanner.adapter.ActivityListAdapter
import com.compi.dinhnt.travelplanner.databinding.FragmentDayActivityDetailBinding
import com.compi.dinhnt.travelplanner.model.TravelActivity
import com.compi.dinhnt.travelplanner.view_model.TravelPlanDetailViewModel
import org.koin.android.ext.android.inject

class DayActivityDetail : Fragment() {
    private var day: String? = null
    private var position = 0
    private lateinit var adapter: ActivityListAdapter
    private val viewModel: TravelPlanDetailViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentDayActivityDetailBinding.inflate(inflater, container, false)
        binding.buttonCreateActivity.setOnClickListener { day?.let { viewModel.onCreateActivity(it) } }
        adapter = ActivityListAdapter(ActivityListAdapter.OnClickListener { activity ->
            day?.let { viewModel.onEditActivity(it, activity.id) }
        })
        viewModel.mapTravelPlanWithActivity.observe(viewLifecycleOwner) {
            val data = it.entries.toTypedArray()[position]
            day = data.key
            adapter.submitList(data.value)
        }
        binding.activityListRecyclerView.adapter = adapter
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(
            position: Int,
        ) =
            DayActivityDetail().apply {
                this.position = position
            }
    }
}