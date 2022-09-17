package com.compi.dinhnt.travelplanner.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.compi.dinhnt.travelplanner.databinding.ActivityItemBinding
import com.compi.dinhnt.travelplanner.model.TravelActivity


class ActivityListAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<TravelActivity, ActivityListAdapter.ActivityViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<TravelActivity>() {
        override fun areItemsTheSame(oldItem: TravelActivity, newItem: TravelActivity): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: TravelActivity, newItem: TravelActivity): Boolean {
            return oldItem === newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        return ActivityViewHolder(
            ActivityItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        val travelPlan = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(travelPlan)
        }
        holder.bind(travelPlan, position)
    }


    class ActivityViewHolder(private var binding: ActivityItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(travelActivity: TravelActivity, position: Int) {
            if (position == 0) {
                binding.lineTopImage.visibility = INVISIBLE
            } else {
                binding.lineTopImage.visibility = VISIBLE
            }
            travelActivity.weather?.weatherDetail?.let {
                binding.weatherIcon.setImageResource(it.getIcon())
            }
            binding.activity = travelActivity
            binding.executePendingBindings()
        }
    }

    class OnClickListener(val clickListener: (travelPlan: TravelActivity) -> Unit) {
        fun onClick(travelPlan: TravelActivity) = clickListener(travelPlan)
    }
}