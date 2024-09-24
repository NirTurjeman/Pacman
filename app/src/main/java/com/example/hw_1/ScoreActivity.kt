package com.example.hw_1

import Interfaces.OnScoreSelected
import Models.ScoreList
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.hw_1.Fragments.ScoreListFragment
import com.example.hw_1.Fragments.MapFragment
import com.google.android.gms.maps.model.LatLng

class ScoreActivity : AppCompatActivity(),OnScoreSelected{
    private lateinit var score_FRAME_list: FrameLayout
    private lateinit var score_FRAME_map: FrameLayout
    private lateinit var mapFragment : MapFragment
    private lateinit var scoreListFragment : ScoreListFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_high_score)
        findViews()
        loadFragments()
    }
    private fun findViews() {
        this.score_FRAME_list = findViewById(R.id.score_FRAME_list)
        this.score_FRAME_map = findViewById(R.id.score_FRAME_map)
    }
    private fun loadFragments() {
        this.scoreListFragment = ScoreListFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.score_FRAME_list, this.scoreListFragment)
            .commit()

        this.mapFragment = MapFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.score_FRAME_map, this.mapFragment)
            .commit()
    }
    override fun onScoreSelected(latLng: LatLng, date: String) {
        val latLng = LatLng(latLng.latitude,latLng.longitude)
        val mapFragment = MapFragment.newInstance(latLng.latitude.toDouble(),latLng.longitude.toDouble(),date)
            supportFragmentManager.beginTransaction()
                .replace(R.id.map, mapFragment)
                .commit()
    }
}
