package kr.sesacjava.swimtutor.health

import android.webkit.JavascriptInterface
import android.webkit.WebViewClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HealthConnectHandler(private val healthConnectService: HealthConnectService): WebViewClient() {

    @JavascriptInterface
    fun getDataRecords() {
        println("read")
        CoroutineScope(Dispatchers.IO).launch {
            healthConnectService.getRecords()
        }
    }

    @JavascriptInterface
    fun insert() {
        println("insert")
        CoroutineScope(Dispatchers.IO).launch {
            healthConnectService.insertSteps()
        }
    }

    @JavascriptInterface
    fun getExercise() {
        println("exercise")
        CoroutineScope(Dispatchers.IO).launch {
            healthConnectService.getExercise()
        }
    }
}