package kr.sesacjava.swimtutor.health

import android.webkit.JavascriptInterface
import android.webkit.WebViewClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HealthConnectJsInterface(private val healthConnectService: HealthConnectService): WebViewClient() {
    @JavascriptInterface
    fun getDataRecords(date: String) {
        println("read")
        CoroutineScope(Dispatchers.IO).launch {
            val response = healthConnectService.getRecords(date)

            for (exerciseRecord in response.records) {
                healthConnectService.sendDistanceData(exerciseRecord.startTime, exerciseRecord.endTime)
                healthConnectService.sendCaloriesData(exerciseRecord.startTime, exerciseRecord.endTime)
                healthConnectService.sendSpeedData(exerciseRecord.startTime, exerciseRecord.endTime)
                healthConnectService.sendHeartRateData(exerciseRecord.startTime, exerciseRecord.endTime)
            }
        }
    }


}