package com.example.composeversion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.composeversion.ui.theme.ComposeVersionTheme
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Read from stops.txt in res/raw/
        val stopsList = readStopsFromRaw()

        setContent {
            ComposeVersionTheme {
                // 2. Show the main composable
                MainScreen(stopsList = stopsList)
            }
        }
    }

    /**
     * Helper function to read stops from stops.txt in res/raw/
     */
    private fun readStopsFromRaw(): List<StopInfo> {
        val stopInfos = mutableListOf<StopInfo>()
        val inputStream = resources.openRawResource(R.raw.stops) // R.raw.stops (file name: stops.txt)
        val reader = BufferedReader(InputStreamReader(inputStream))
        reader.use {
            it.forEachLine { line ->
                val parts = line.split(",")
                if (parts.size == 4) {
                    val city = parts[0].trim()
                    val visa = parts[1].trim()
                    val distanceKm = parts[2].trim().toDoubleOrNull() ?: 0.0
                    val timeHours = parts[3].trim().toDoubleOrNull() ?: 0.0
                    val stopInfo = StopInfo(city, visa, distanceKm, timeHours)
                    stopInfos.add(stopInfo)
                }
            }
        }
        return stopInfos
    }
}

@Composable
fun MainScreen(stopsList: List<StopInfo>) {
    // Track whether distances are shown in KM (default) or Miles
    var isMiles by remember { mutableStateOf(false) }
    // Track the current stop index. Start at 0 (the first stop).
    var currentStopIndex by remember { mutableStateOf(0) }

    // Calculate total distance
    val totalDistanceKm = stopsList.sumOf { it.distanceToNextKm }

    // Distance covered so far:
    val distanceCoveredKm = stopsList.take(currentStopIndex).sumOf { it.distanceToNextKm }
    val distanceCovered = if (isMiles) kmToMiles(distanceCoveredKm) else distanceCoveredKm

    // Distance left:
    val distanceLeftKm = totalDistanceKm - distanceCoveredKm
    val distanceLeft = if (isMiles) kmToMiles(distanceLeftKm) else distanceLeftKm

    // Unit labels for display
    val distanceUnit = if (isMiles) "mi" else "km"

    // For the progress bar, we can define progress as fraction of total stops.
    // Or do it based on distance covered. Here, let’s do fraction of distance covered:
    val progressFraction = if (totalDistanceKm == 0.0) 0f else (distanceCoveredKm / totalDistanceKm).toFloat()

    // Current city info
    val currentStop = stopsList.getOrNull(currentStopIndex)

    // If no current stop, we've possibly completed the journey.
    val journeyCompleted = currentStopIndex >= stopsList.size

    // The time left to next stop
    val timeLeft = currentStop?.timeToNextHours ?: 0.0

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title
            Text(
                text = "Journey Progress",
//                style = MaterialTheme.typography.h5,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            // Progress bar
            LinearProgressIndicator(
                progress = progressFraction,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )

            // Distance covered / left
            Text(
                text = "Distance covered: %.2f $distanceUnit".format(distanceCovered),
//                style = MaterialTheme.typography.body1
            )
            Text(
                text = "Distance left: %.2f $distanceUnit".format(distanceLeft),
//                style = MaterialTheme.typography.body1
            )

            // Next stop info
            if (!journeyCompleted) {
                Text(
                    text = "Current Stop: ${currentStop?.cityName}",
//                    style = MaterialTheme.typography.body1
                )
                Text(
                    text = "Visa Requirement: ${currentStop?.visaRequirement}",
//                    style = MaterialTheme.typography.body1
                )
                Text(
                    text = "Distance to Next: %.2f %s".format(
                        if (isMiles) kmToMiles(currentStop?.distanceToNextKm ?: 0.0)
                        else (currentStop?.distanceToNextKm ?: 0.0),
                        distanceUnit
                    ),
//                    style = MaterialTheme.typography.body1
                )
                Text(
                    text = "Time to Next: %.1f hours".format(timeLeft),
//                    style = MaterialTheme.typography.body1
                )
            } else {
                Text(
                    text = "Journey Completed!",
//                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center
                )
            }

            // Row of two buttons: Switch units, Next Stop
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Button(onClick = { isMiles = !isMiles }) {
                    Text(if (isMiles) "Switch to KM" else "Switch to Miles")
                }
                Button(
                    onClick = {
                        // Move to next stop if not completed
                        if (!journeyCompleted) {
                            currentStopIndex++
                        }
                    },
                    enabled = !journeyCompleted
                ) {
                    Text("Next Stop")
                }
            }

            // Show a list of all stops (lazy list)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "All Stops:",
//                style = MaterialTheme.typography.subtitle1
            )
            LazyColumn(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(stopsList) { index, stop ->
                    StopItem(
                        stopInfo = stop,
                        isCurrent = (index == currentStopIndex && !journeyCompleted),
                        isMiles = isMiles
                    )
                }
            }
        }
    }
}

@Composable
fun StopItem(
    stopInfo: StopInfo,
    isCurrent: Boolean,
    isMiles: Boolean
) {
    val distance = if (isMiles) kmToMiles(stopInfo.distanceToNextKm) else stopInfo.distanceToNextKm
    val distanceUnit = if (isMiles) "mi" else "km"

    Card(
        modifier = Modifier.fillMaxWidth(),
//        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = "City: ${stopInfo.cityName}")
            Text(text = "Visa: ${stopInfo.visaRequirement}")
            Text(text = "Distance to Next: %.2f %s".format(distance, distanceUnit))
            Text(text = "Time to Next: %.1f hours".format(stopInfo.timeToNextHours))

            if (isCurrent) {
                Text(
                    text = "Currently Here",
//                    style = MaterialTheme.typography.body2,
//                    color = MaterialTheme.colors.primary
                )
            }
        }
    }
}

private fun kmToMiles(km: Double): Double {
    return km * 0.621371
}
