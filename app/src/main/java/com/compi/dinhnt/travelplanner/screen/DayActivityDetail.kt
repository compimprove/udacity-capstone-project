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
    private val clickListener: (day: String) -> Unit,
    private val onEditActivity: (day: String, activityId: String) -> Unit
) : Fragment() {

    private lateinit var adapter: ActivityListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentDayActivityDetailBinding.inflate(inflater, container, false)
        binding.buttonCreateActivity.setOnClickListener { clickListener(day) }
        adapter = ActivityListAdapter(ActivityListAdapter.OnClickListener { activity ->
            onEditActivity(day, activity.id)
        })
        binding.activityListRecyclerView.adapter = adapter
        adapter.submitList(activities)
        return binding.root
    }

    fun updateData(activities: List<Activity>) {
        adapter.submitList(activities)
    }

    companion object {
        @JvmStatic
        fun newInstance(
            day: String,
            activities: List<Activity>,
            onCreateActivity: (day: String) -> Unit,
            onEditActivity: (day: String, activityId: String) -> Unit
        ) =
            DayActivityDetail(day, activities, onCreateActivity, onEditActivity)
    }
}