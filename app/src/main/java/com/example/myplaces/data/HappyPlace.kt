package com.example.myplaces.data

import android.media.Image

data class MyPlace(
    val id: Int,
    val name: String,
    val image: String,
    val description: String,
    val date: String,
    val location: String,
    val latitude: Double,
    val longitude: Double
)
