package com.example.xml_version

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.xml_version.utils.parseStops

class MainActivity : AppCompatActivity() {

    private lateinit var rvStops: RecyclerView
    private lateinit var adapter: StopsAdapter
    private lateinit var btnToggleUnit: Button
    private lateinit var btnNextStop: Button
    private lateinit var tvProgressInfo: TextView
    private lateinit var progressBar: ProgressBar

    private var stopsList: List<Stop> = emptyList()
    private var isMiles = false

    // Variables to track progress
    private var totalDistance = 0
    private var totalTime = 0

    private var distanceCovered = 0
    private var timeCovered = 0

    private var currentStopIndex = 0  // which segment of the journey has been completed?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI
        rvStops = findViewById(R.id.rvStops)
        btnToggleUnit = findViewById(R.id.btnToggleUnit)
        btnNextStop = findViewById(R.id.btnNextStop)
        tvProgressInfo = findViewById(R.id.tvProgressInfo)
        progressBar = findViewById(R.id.progressBar)

        // Parse data
        stopsList = parseStops(this, R.raw.stops)

        // Calculate total distance/time
        totalDistance = stopsList.sumBy { it.distanceKm }
        totalTime = stopsList.sumBy { it.timeMinutes }

        // Setup RecyclerView
        rvStops.layoutManager = LinearLayoutManager(this)
        adapter = StopsAdapter(stopsList, isMiles)
        rvStops.adapter = adapter

        // Initialize progress bar
        progressBar.max = 100
        updateProgressUI()

        // Toggle unit button
        btnToggleUnit.setOnClickListener {
            isMiles = !isMiles
            btnToggleUnit.text = if (isMiles) "Switch to KM" else "Switch to Miles"
            // Re-bind data with updated distance unit
            adapter.updateData(stopsList, isMiles)
            updateProgressUI() // so the textView also toggles if needed
        }

        // Next Stop button
        btnNextStop.setOnClickListener {
            if (currentStopIndex < stopsList.size) {
                // Assume the user has reached the next stop
                val nextSegment = stopsList[currentStopIndex]
                distanceCovered += nextSegment.distanceKm
                timeCovered += nextSegment.timeMinutes
                currentStopIndex++

                // Update the progress bar
                updateProgressUI()
            } else {
                Toast.makeText(this, "Journey complete!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    private fun updateProgressUI() {
        val distanceUnitText = if (isMiles) {
            String.format("%.2f miles", distanceCovered * 0.621371)
        } else {
            "$distanceCovered km"
        }

        val distanceLeft = totalDistance - distanceCovered
        val distanceLeftText = if (isMiles) {
            String.format("%.2f miles", distanceLeft * 0.621371)
        } else {
            "$distanceLeft km"
        }

        val progressDistanceRatio = distanceCovered.toFloat() / totalDistance.toFloat()
        val progressTimeRatio = timeCovered.toFloat() / totalTime.toFloat()
        // Combine the two ratios, for example by averaging them:
        val combinedProgress = (progressDistanceRatio + progressTimeRatio) / 2f
        val progressPercent = (combinedProgress * 100).toInt()

        tvProgressInfo.text = "Distance Covered: $distanceUnitText | Time Covered: $timeCovered min\n" +
                "Distance Left: $distanceLeftText | Time Left: ${totalTime - timeCovered} min"
        progressBar.progress = progressPercent
    }
}
