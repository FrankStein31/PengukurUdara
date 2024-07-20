package com.polinema.ta_iot

data class Data(
    val kadar_co2: Float = 0f,
    val skala: String = "",
    val suhu: Float = 0f,
    val kelembapan: Float = 0f,
    val waktu: String = "",

    val kadar_co2_value: Float = 0f,
    val skala_value: String = "",
    val suhu_value: Float = 0f,
    val kelembapan_value: Float = 0f,
    val waktu_value: String = ""
)

