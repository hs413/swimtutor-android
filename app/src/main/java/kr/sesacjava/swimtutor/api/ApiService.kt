package com.example.health.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class StepsData(
    val count: Long,
    val timestamp: Long
)

interface ApiService {
    @POST("health")
    fun sendStepsData(@Body stepsData: StepsData): Call<Void>
}