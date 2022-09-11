package com.compi.dinhnt.travelplanner.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.compi.dinhnt.travelplanner.R
import com.compi.dinhnt.travelplanner.databinding.FragmentChooseLocationBinding
import com.compi.dinhnt.travelplanner.view_model.CreateEditActivityViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class ChooseLocationFragment : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val startLocation = LatLng(37.424, -122.079)
    private var locationMarker: Marker? = null
    private val _viewModel: CreateEditActivityViewModel by lazy {
        val activity = requireNotNull(this.activity)
        ViewModelProvider(
            requireActivity(),
            CreateEditActivityViewModel.Factory(activity.application)
        )[CreateEditActivityViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentChooseLocationBinding.inflate(inflater, container, false)
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        binding.saveButton.setOnClickListener{
            findNavController().popBackStack()
        }
        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        enableMyLocation()
        setPoiClick(map)
        setMapLongClick(map)
        if (_viewModel.latitude.value != null
            && _viewModel.longitude.value != null
            && _viewModel.locationName.value != null
        ) {
            val centerLocation = LatLng(_viewModel.latitude.value!!, _viewModel.longitude.value!!)
            val poiName = _viewModel.locationName.value!!
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(centerLocation, 15f))
            locationMarker?.remove()
            locationMarker = map.addMarker(
                MarkerOptions()
                    .position(centerLocation)
                    .title(poiName)
            )
        } else {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(startLocation, 15f))
        }
    }

    private fun setPoiClick(map: GoogleMap) {
        map.setOnPoiClickListener { poi ->
            locationMarker?.remove()
            locationMarker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            locationMarker?.showInfoWindow()
            _viewModel.setLocation(poi.latLng, poi.name)
        }
    }

    private fun setMapLongClick(map: GoogleMap) {
        map.setOnMapLongClickListener { latLng ->
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.5f, Long: %2$.5f",
                latLng.latitude,
                latLng.longitude
            )
            val title = "Place"
            locationMarker?.remove()
            locationMarker = map.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(title)
                    .snippet(snippet)

            )
            locationMarker?.showInfoWindow()
            _viewModel.setLocation(latLng, title)
        }
    }

    private fun enableMyLocation() {
        context?.let { context: Context ->
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSION
                )
                return
            } else {
                map.isMyLocationEnabled = true
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                @SuppressLint("MissingPermission")
                map.isMyLocationEnabled = true
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChooseLocationFragment()
        private val REQUEST_LOCATION_PERMISSION = 1
    }
}