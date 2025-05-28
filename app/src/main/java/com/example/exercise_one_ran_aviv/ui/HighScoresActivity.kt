package com.example.exercise_one_ran_aviv.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.widget.Button
import com.example.exercise_one_ran_aviv.ui.adapters.HighScoreAdapter
import com.example.exercise_one_ran_aviv.R
import com.example.exercise_one_ran_aviv.data.HighScoreManager

class HighScoresActivity : AppCompatActivity(), OnMapReadyCallback {


    private lateinit var recyclerView: RecyclerView
    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null
    private val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_high_scores)

        val backButton: Button = findViewById(R.id.button_back)

        backButton.setOnClickListener {
            finish()
        }

        recyclerView = findViewById(R.id.recycler_high_scores)
        mapView = findViewById(R.id.mapView)

        val scores = HighScoreManager.getHighScores().take(10)

        if (scores.isEmpty()) {
            Toast.makeText(this, "No scores yet. Play a game first!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val adapter = HighScoreAdapter(scores) { highScore ->
            val position = LatLng(highScore.latitude, highScore.longitude)
            googleMap?.clear()
            googleMap?.addMarker(MarkerOptions().position(position).title("Score: ${highScore.score}"))
            googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 5f))
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY)
        }

        mapView.onCreate(mapViewBundle)
        mapView.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle)
        }
        mapView.onSaveInstanceState(mapViewBundle)
    }
}
