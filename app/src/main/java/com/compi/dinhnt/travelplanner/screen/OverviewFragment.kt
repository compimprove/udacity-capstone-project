package com.compi.dinhnt.travelplanner.screen

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.compi.dinhnt.travelplanner.*
import com.compi.dinhnt.travelplanner.adapter.TravelPlanListAdapter
import com.compi.dinhnt.travelplanner.databinding.FragmentOverviewBinding
import com.compi.dinhnt.travelplanner.view_model.OverviewViewModel
import kotlinx.android.synthetic.main.popup_create_travel_plan.view.*
import kotlinx.coroutines.delay
import org.koin.android.ext.android.inject


class OverviewFragment : Fragment() {

    private lateinit var popupWindow: PopupWindow
    private val viewModel: OverviewViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setTitle(getString(R.string.app_name))
        val binding = FragmentOverviewBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.travelPlanRecyclerView.adapter =
            TravelPlanListAdapter(TravelPlanListAdapter.OnClickListener {
                this.findNavController()
                    .navigate(OverviewFragmentDirections.actionShowDetailTravelPlan(it.id))
            })
        val popupView: View =
            inflater.inflate(com.compi.dinhnt.travelplanner.R.layout.popup_create_travel_plan, null)
        val width = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.MATCH_PARENT
        val focusable = true
        popupWindow = PopupWindow(popupView, width, height, focusable)
        popupView.setOnTouchListener { _, _ ->
            popupView.planNameTF.text?.clear()
            popupWindow.dismiss()
            popupView.performClick()
        }
        popupView.saveTravelPlanButton.setOnClickListener {
            viewModel.createPlan(popupView.planNameTF.text.toString())
        }
        binding.createPlanButton.setOnClickListener {
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
        }
        setDisplayHomeAsUpEnabled(false)
        binding.lifecycleOwner = this
        try {
            val travelPlanId =
                requireActivity().intent.extras?.get(EXTRA_TravelPlanId) as String
            requireActivity().intent.removeExtra(EXTRA_TravelPlanId)
            this.findNavController()
                .navigate(OverviewFragmentDirections.actionShowDetailTravelPlan(travelPlanId))
        } catch (e: Exception) {

        }
        return binding.root
    }
}