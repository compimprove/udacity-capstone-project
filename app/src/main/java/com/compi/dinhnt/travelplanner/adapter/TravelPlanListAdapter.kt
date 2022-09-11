package com.compi.dinhnt.travelplanner.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.compi.dinhnt.travelplanner.databinding.TravelPlanItemBinding
import com.compi.dinhnt.travelplanner.model.TravelPlan


class TravelPlanListAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<TravelPlan, TravelPlanListAdapter.TravelPlanViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<TravelPlan>() {
        override fun areItemsTheSame(oldItem: TravelPlan, newItem: TravelPlan): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: TravelPlan, newItem: TravelPlan): Boolean {
            return oldItem === newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TravelPlanViewHolder {
        return TravelPlanViewHolder(TravelPlanItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: TravelPlanViewHolder, position: Int) {
        val travelPlan = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(travelPlan)
        }
        holder.bind(travelPlan)
    }


    class TravelPlanViewHolder(private var binding: TravelPlanItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(travelPlan: TravelPlan) {
            binding.travelPlan = travelPlan
            binding.executePendingBindings()
        }
    }

    class OnClickListener(val clickListener: (travelPlanCTO: TravelPlan) -> Unit) {
        fun onClick(travelPlanCTO: TravelPlan) = clickListener(travelPlanCTO)
    }
}