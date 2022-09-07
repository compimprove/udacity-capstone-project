package com.compi.dinhnt.travelplanner

import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.compi.dinhnt.travelplanner.adapter.TravelPlanWithActivitiesPageAdapter
import com.compi.dinhnt.travelplanner.adapter.TravelPlanListAdapter
import com.compi.dinhnt.travelplanner.model.ActivityType
import com.compi.dinhnt.travelplanner.model.TravelPlan
import com.compi.dinhnt.travelplanner.model.TravelPlanWithActivity
import com.compi.dinhnt.travelplanner.model.WeatherDetail
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("startDate", "endDate")
fun bindDates(textView: TextView, startDate: Date, endDate: Date) {
    val formatter = SimpleDateFormat("MMM dd")
    textView.text =
        "${formatter.format(startDate)} - ${formatter.format(endDate)}"
}

@BindingAdapter("listTravelPlans")
fun bindListTravelPlans(recyclerView: RecyclerView, data: List<TravelPlan>?) {
    val adapter = recyclerView.adapter as TravelPlanListAdapter
    adapter.submitList(data)
}

@BindingAdapter("listWeather")
fun bindListTravelPlans(linearLayout: LinearLayout, data: List<WeatherDetail>?) {
    val data = listOf(WeatherDetail.CLEAR_SKY, WeatherDetail.RAIN, WeatherDetail.BROKEN_CLOUDS)
    linearLayout.removeAllViews()
    data?.forEach {
        val imageView = ImageView(linearLayout.context)
        imageView.setImageResource(it.getIcon())
        linearLayout.addView(imageView)
    }
}

@BindingAdapter("travelPlanWithActivity")
fun bindTravelPlanWithActivity(viewPager: ViewPager2, data: TravelPlanWithActivity?) {
    data?.run {
        val adapter = viewPager.adapter as TravelPlanWithActivitiesPageAdapter
        adapter.setTravelPlan(data)
    }
}

@BindingAdapter("activityTime")
fun bindTravelPlanActivityTime(textView: TextView, time: Long?) {
    time?.let {
        val time = if (it >= 13 * 60) {
            it - 12 * 60
        } else {
            it
        }
        textView.text = "${time / 60}:${String.format("%02d", time % 60)}"
    }
}

@BindingAdapter("activityTimeUnit")
fun bindActivityTimeUnit(textView: TextView, time: Long?) {
    time?.let { time ->
        textView.text = if (time >= 12 * 60) {
            "PM"
        } else {
            "AM"
        }
    }
}

@BindingAdapter("activityIcon")
fun bindActivityIcon(imageView: ImageView, activityType: ActivityType?) {
    if (activityType == null) {
        imageView.setImageResource(R.drawable.ic_playing)
    } else {
        imageView.setImageResource(activityType.getIcon())
    }
}

@BindingAdapter("detailActivityInfo")
fun bindDetailActivityInfo(textView: TextView, detailInfo: Map<String, String>?) {
    detailInfo?.let {
        var string = ""
        it.entries.forEachIndexed { index, entry ->
            run {
                string += if (index == it.entries.size - 1) {
                    "${entry.key}: ${entry.value}"
                } else {
                    "${entry.key}: ${entry.value}\n"
                }
            }
        }
        textView.text = string
    }
}