package com.compi.dinhnt.travelplanner.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import androidx.viewpager2.widget.ViewPager2
import com.compi.dinhnt.travelplanner.model.TravelActivity
import com.compi.dinhnt.travelplanner.model.TravelPlanWithActivity
import com.compi.dinhnt.travelplanner.screen.DayActivityDetail
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class TravelPlanWithActivitiesPageAdapter(
    fa: FragmentManager,
    lifecycle: Lifecycle,
    private val tabLayout: TabLayout,
    private val viewPager: ViewPager2,
) : FragmentStateAdapter(fa, lifecycle) {
    private val formatter = SimpleDateFormat("yyyy-MM-dd")
    private var mapTravelPlanWithActivities = sortedMapOf<String, MutableList<TravelActivity>>()
    private var tabLayoutMediator: TabLayoutMediator? = null


    override fun createFragment(position: Int): Fragment {
        return DayActivityDetail.newInstance(position)
    }

    override fun onBindViewHolder(
        holder: FragmentViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
    }

    override fun getItemCount(): Int {
        return mapTravelPlanWithActivities.entries.size
    }

    fun updateTabs(mapTravelPlanWithActivities: SortedMap<String, MutableList<TravelActivity>>) {
        this.mapTravelPlanWithActivities = mapTravelPlanWithActivities
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
    }

}