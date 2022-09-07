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
import com.compi.dinhnt.travelplanner.model.Activity


class ActivityListAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<Activity, ActivityListAdapter.ActivityViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Activity>() {
        override fun areItemsTheSame(oldItem: Activity, newItem: Activity): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Activity, newItem: Activity): Boolean {
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
        fun bind(activity: Activity, position: Int) {
            if (position == 0) {
                binding.lineTopImage.visibility = INVISIBLE
            } else {
                binding.lineTopImage.visibility = VISIBLE
            }
            binding.activity = activity
            binding.executePendingBindings()
        }
    }

    class OnClickListener(val clickListener: (travelPlan: Activity) -> Unit) {
        fun onClick(travelPlan: Activity) = clickListener(travelPlan)
    }
}