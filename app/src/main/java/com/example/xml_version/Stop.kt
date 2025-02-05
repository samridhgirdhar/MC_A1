package com.example.xml_version

data class Stop(
    val name: String,
    val distanceKm: Int,
    val timeMinutes: Int,
    val transitVisaRequired: Boolean
)
