package com.example.xml_version.utils

import android.content.Context
import androidx.annotation.RawRes
import com.example.xml_version.Stop

fun parseStops(context: Context, @RawRes rawResId: Int): List<Stop> {
    val inputStream = context.resources.openRawResource(rawResId)
    val stops = mutableListOf<Stop>()

    inputStream.bufferedReader().useLines { lines ->
        lines.forEach { line ->
            if (line.isBlank() || line.startsWith("StopName")) return@forEach

            val parts = line.split(",")
            if (parts.size == 4) {
                val name = parts[0].trim()
                val distance = parts[1].trim().toIntOrNull() ?: 0
                val time = parts[2].trim().toIntOrNull() ?: 0
                val visaRequired = parts[3].trim().equals("Yes", ignoreCase = true)

                stops.add(Stop(name, distance, time, visaRequired))
            }
        }
    }

    return stops
}
