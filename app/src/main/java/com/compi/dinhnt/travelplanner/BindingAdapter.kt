package com.compi.dinhnt.travelplanner

import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.compi.dinhnt.travelplanner.adapter.TravelPlanListAdapter
import com.compi.dinhnt.travelplanner.model.*
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("subtitleTravelPlan")
fun bindSubtitle(textView: TextView, subtitleTravelPlan: TravelPlan) {
    val startDate = subtitleTravelPlan.startDate
    val endDate = subtitleTravelPlan.endDate
    val formatter = SimpleDateFormat("MMM dd")
    if (startDate == null || endDate == null) {
        if (subtitleTravelPlan.activityNumber == 0) {
            textView.text = "Empty"
        } else {
            textView.text = ""
        }
    } else if (startDate == endDate) {
        textView.text = formatter.format(startDate)
    } else {
        textView.text =
            "${formatter.format(startDate)} - ${formatter.format(endDate)}"
    }
}

@BindingAdapter("listTravelPlans")
fun bindListTravelPlans(recyclerView: RecyclerView, data: List<TravelPlan>?) {
    val adapter = recyclerView.adapter as TravelPlanListAdapter
    adapter.submitList(data)
}

//@BindingAdapter("listWeather")
//fun bindListTravelPlans(linearLayout: LinearLayout, data: List<WeatherDetail>?) {
//    val data = listOf(WeatherDetail.CLEAR_SKY, WeatherDetail.RAIN, WeatherDetail.BROKEN_CLOUDS)
//    linearLayout.removeAllViews()
//    data?.forEach {
//        val imageView = ImageView(linearLayout.context)
//        imageView.setImageResource(it.getIcon())
//        linearLayout.addView(imageView)
//    }
//}

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

@BindingAdapter("locationText")
fun bindLocationText(textView: TextView, location: Location?) {
    if (location != null) {
        textView.visibility = VISIBLE
        textView.text = "Location: ${location.locationName}"
    } else {
        textView.visibility = GONE
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

@BindingAdapter("location")
fun bindLocation(button: Button, locationName: String?) {
    locationName?.let {
        button.text = locationName
    } ?: run { button.text = "Location ?" }
}
