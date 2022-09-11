package com.compi.dinhnt.travelplanner.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import androidx.viewpager2.widget.ViewPager2
import com.compi.dinhnt.travelplanner.model.Activity
import com.compi.dinhnt.travelplanner.model.TravelPlanWithActivity
import com.compi.dinhnt.travelplanner.screen.DayActivityDetail
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.text.ParseException
import java.text.SimpleDateFormat


class TravelPlanWithActivitiesPageAdapter(
    fa: FragmentActivity,
    private val tabLayout: TabLayout,
    private val viewPager: ViewPager2,
    private val onCreateActivity: (day: String) -> Unit,
    private val onEditActivity: (day: String, activityId: String) -> Unit
) : FragmentStateAdapter(fa) {
    private val formatter = SimpleDateFormat("yyyy-MM-dd")
    private var _travelPlanWithActivity: TravelPlanWithActivity? = null
    private var mapTravelPlanWithActivities = sortedMapOf<String, MutableList<Activity>>()
    private var tabLayoutMediator: TabLayoutMediator? = null
    private val createdFragment = mutableMapOf<Int, DayActivityDetail>()

    override fun createFragment(position: Int): Fragment {
        val entry = mapTravelPlanWithActivities.entries.toTypedArray()[position]
        val fragment = DayActivityDetail
            .newInstance(entry.key,
                entry.value,
                { day -> onCreateActivity(day) },
                { day, activityId -> onEditActivity(day, activityId) })
        createdFragment[position] = fragment
        return fragment
    }

    override fun onBindViewHolder(
        holder: FragmentViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
    }

    override fun getItemCount(): Int {
        return if (_travelPlanWithActivity == null) {
            0
        } else {
            mapTravelPlanWithActivities.entries.size
        }
    }

    fun setTravelPlan(travelPlanWithActivity: TravelPlanWithActivity) {
        if (travelPlanWithActivity.activities.isEmpty()) return
        _travelPlanWithActivity = travelPlanWithActivity
        mapTravelPlanWithActivities.clear()
        travelPlanWithActivity.activities.forEach {
            val dateStr = formatter.format(it.date)
            if (!mapTravelPlanWithActivities.containsKey(dateStr)) {
                mapTravelPlanWithActivities[dateStr] = mutableListOf(it)
            } else {
                mapTravelPlanWithActivities[dateStr]?.add(it)
            }
        }
        val tabs = mutableListOf<String>()
        val monthFormatter = SimpleDateFormat("MMM dd")
        mapTravelPlanWithActivities.entries.forEach { entry ->
            entry.value.sort()
            try {
                val date = formatter.parse(entry.key)
                if (date != null) tabs.add(monthFormatter.format(date))
            } catch (e: ParseException) {

            }
        }
        tabLayoutMediator?.detach()
        tabLayoutMediator = TabLayoutMediator(
            tabLayout, viewPager
        ) { tab: TabLayout.Tab, position: Int -> tab.text = tabs[position] }
        tabLayoutMediator?.attach()
        updateData()
    }

    private fun updateData() {
        createdFragment.entries.forEach {entry->
            val data = mapTravelPlanWithActivities.entries.toTypedArray()[entry.key]
            entry.value.updateData(data.value)
        }
    }

}