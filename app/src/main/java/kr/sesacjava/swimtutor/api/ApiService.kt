package com.example.health.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class CaloriesData (
    val value: Double,
    val stopTime: String,
    val startTime: String,
    val category: String = "calories"
)

data class SpeedData (
    val value: Double,
    val stopTime: String,
    val startTime: String,
    val category: String = "speed"
)


data class DistanceData (
    val value: Double,
    val stopTime: String,
    val startTime: String,
    val category: String = "distance"
)

data class HeartRateData (
    val value: Double,
    val stopTime: String,
    val startTime: String,
    val category: String = "heartRate"
)

interface ApiService {
    @POST("record/")
    fun sendCaloriesData(@Body data: CaloriesData): Call<Void>
    @POST("record/")
    fun sendSpeedData(@Body data: SpeedData): Call<Void>
    @POST("record/")
    fun sendDistanceData(@Body data: DistanceData): Call<Void>
    @POST("record/")
    fun sendHeartRateData(@Body data: HeartRateData): Call<Void>
}