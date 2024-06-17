package kr.sesacjava.swimtutor

import android.os.Bundle
import android.webkit.WebView
import androidx.activity.ComponentActivity
import kr.sesacjava.swimtutor.health.HealthConnectHandler
import kr.sesacjava.swimtutor.health.HealthConnectManager
import kr.sesacjava.swimtutor.health.HealthConnectService

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val healthConnectManager = HealthConnectManager(applicationContext)

        if (!healthConnectManager.getSdkStatus()) return

        setContentView(R.layout.activity_main)
        val myWebView: WebView = findViewById(R.id.webview)
        val connectClient = healthConnectManager.getConnectClient()
        val service = HealthConnectService(applicationContext, connectClient, myWebView)

        myWebView.settings.javaScriptEnabled = true
        myWebView.settings.domStorageEnabled = true

        myWebView.addJavascriptInterface(HealthConnectHandler(service), "HealthConnect")
        myWebView.loadUrl("file:///android_asset/web.html")
    }
}
