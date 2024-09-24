package Interfaces

import com.google.android.gms.maps.model.LatLng

interface OnScoreSelected {
    fun onScoreSelected(latLng: LatLng,date: String)
}