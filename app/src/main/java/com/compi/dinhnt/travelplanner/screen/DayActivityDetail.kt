package com.compi.dinhnt.travelplanner.screen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.compi.dinhnt.travelplanner.adapter.ActivityListAdapter
import com.compi.dinhnt.travelplanner.databinding.FragmentDayActivityDetailBinding
import com.compi.dinhnt.travelplanner.model.Activity

class DayActivityDetail(
    private var day: String,
    private var activities: List<Activity>,
    private val clickListener: (day: String) -> Unit
) : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentDayActivityDetailBinding.inflate(inflater, container, false)
        binding.buttonCreateActivity.setOnClickListener { clickListener(day) }
        val adapter = ActivityListAdapter(ActivityListAdapter.OnClickListener {})
        binding.activityListRecyclerView.adapter = adapter
        adapter.submitList(activities)
        return binding.root
//        return if (activities.isEmpty()) {
//            val binding = FragmentDayActivityDetailEmptyBinding.inflate(inflater, container, false)
//            binding.buttonCreateActivity.setOnClickListener { clickListener(day) }
//            binding.root
//        } else {
//
//        }
    }

    companion object {
        @JvmStatic
        fun newInstance(
            day: String,
            activities: List<Activity>,
            clickListener: (day: String) -> Unit
        ) =
            DayActivityDetail(day, activities, clickListener)
    }
}