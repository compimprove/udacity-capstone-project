package com.compi.dinhnt.travelplanner.screen

import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.compi.dinhnt.travelplanner.R
import com.compi.dinhnt.travelplanner.adapter.TravelPlanWithActivitiesPageAdapter
import com.compi.dinhnt.travelplanner.databinding.FragmentTravelPlanDetailBinding
import com.compi.dinhnt.travelplanner.setDisplayHomeAsUpEnabled
import com.compi.dinhnt.travelplanner.setTitle
import com.compi.dinhnt.travelplanner.view_model.TravelPlanDetailViewModel
import kotlinx.android.synthetic.main.fragment_travel_plan_detail.view.*
import kotlinx.android.synthetic.main.popup_create_travel_plan.view.*
import org.koin.android.ext.android.inject


class TravelPlanDetailFragment : Fragment() {

    private lateinit var popupView: View
    private lateinit var popupWindow: PopupWindow
    private lateinit var travelPlanId: String
    private val viewModel: TravelPlanDetailViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentTravelPlanDetailBinding.inflate(inflater, container, false)
        travelPlanId = TravelPlanDetailFragmentArgs.fromBundle(requireArguments()).travelPlanId
        viewModel.setTravelPlanId(travelPlanId)
        val adapter = TravelPlanWithActivitiesPageAdapter(
            childFragmentManager,
            viewLifecycleOwner.lifecycle,
            binding.tabLayout,
            binding.viewPager
        )
        binding.viewPager.adapter = adapter
        binding.emptyActivityConstraintLayout.buttonCreateActivity.setOnClickListener {
            onCreateActivity("")
        }
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewModel.currentPage.value = position
                super.onPageSelected(position)
            }
        })
        viewModel.travelPlanWithActivity.observe(viewLifecycleOwner) { data ->
            if (data == null) return@observe
            if (data.activities.isEmpty()) {
                binding.emptyActivityConstraintLayout.visibility = VISIBLE
                binding.detailActivityLinearLayout.visibility = GONE
            } else {
                binding.emptyActivityConstraintLayout.visibility = GONE
                binding.detailActivityLinearLayout.visibility = VISIBLE
                setTitle(data.travelPlanCTO.name)
            }
        }
        viewModel.mapTravelPlanWithActivity.observe(viewLifecycleOwner) { data ->
            adapter.updateTabs(data)
        }
        viewModel.createActivity.observe(viewLifecycleOwner) {
            if (it != "") {
                viewModel.receiveCreateActivityEvent()
                onCreateActivity(it)
            }
        }
        viewModel.editActivity.observe(viewLifecycleOwner) {
            if (it.first != "" && it.second != "") {
                viewModel.receiveEditActivityEvent()
                onEditActivity(it.first, it.second)
            }
        }
        makePopupEdit(inflater)
        setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    private fun makePopupEdit(inflater: LayoutInflater) {
        popupView =
            inflater.inflate(R.layout.popup_create_travel_plan, null)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.travel_plan_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_edit_travel_plan -> {
            popupView.editTravelPlanTitle.text = getString(R.string.edit_travel_plan)
            val travelPlanCTO = viewModel.travelPlanWithActivity.value?.travelPlanCTO
            if (travelPlanCTO == null) true
            popupView.planNameTF.setText(travelPlanCTO!!.name)
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
                popupView.saveTravelPlanButton.isEnabled = false
                viewModel.editPlan(travelPlanCTO.id, popupView.planNameTF.text.toString())
                popupWindow.dismiss()
            }
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }


    private fun onCreateActivity(day: String) {
        viewModel.travelPlanWithActivity.value?.travelPlanCTO?.id?.let {
            this.findNavController().navigate(
                TravelPlanDetailFragmentDirections.actionToCreateEditActivity(it, day, "")
            )
        } ?: Log.e("Error", "navigate to createEditActivity")
    }

    private fun onEditActivity(day: String, activityId: String) {
        viewModel.travelPlanWithActivity.value?.travelPlanCTO?.id?.let {
            this.findNavController().navigate(
                TravelPlanDetailFragmentDirections.actionToCreateEditActivity(it, day, activityId)
            )
        } ?: Log.e("Error", "navigate to createEditActivity")
    }
}