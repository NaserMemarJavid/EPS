package com.lern1.eps

import java.io.Serializable
data class SerializableLocation(
    val latitude: Double,
    val longitude: Double
) : Serializable