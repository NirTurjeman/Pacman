package com.example.hw_1.Fragments
import Interfaces.OnScoreSelected
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hw_1.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var currentDate: String = "No Date"
    private lateinit var googleMap: GoogleMap
    private lateinit var mapView : MapView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let {
            this.latitude = it.getDouble("latitude", 0.0)
            this.longitude = it.getDouble("longitude", 0.0)
            this.currentDate = it.getString("date", "No Date") ?: "No Date"
        }
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        this.mapView = view.findViewById(R.id.map)
        this.mapView.onCreate(savedInstanceState)
        this.mapView.getMapAsync(this)
        return view
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        val initialLatLng = LatLng(latitude, longitude)
        googleMap.addMarker(
            MarkerOptions()
                .position(initialLatLng)
                .title("Date: $currentDate")
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLatLng, 12f))
    }
    private fun addMarker(latLng: LatLng, date: String) {
        val markerOptions = MarkerOptions()
            .position(latLng)
            .title("Date: $date")
            .snippet("Location: (${latLng.latitude}, ${latLng.longitude})")

        googleMap.addMarker(markerOptions)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
    }
    companion object {
        fun newInstance(latitude: Double, longitude: Double, date: String): MapFragment {
            val fragment = MapFragment()
            val args = Bundle()
            args.putDouble("latitude", latitude)
            args.putDouble("longitude", longitude)
            args.putString("date", date)
            fragment.arguments = args
            return fragment
        }
    }
}
