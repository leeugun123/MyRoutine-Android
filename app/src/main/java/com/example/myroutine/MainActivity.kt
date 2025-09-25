package com.example.myroutine

import android.os.Bundle
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.myroutine.bridge.WebBridge
import com.example.myroutine.ui.theme.MyRoutineTheme

class MainActivity : ComponentActivity() {

    private lateinit var webView: WebView
    private var lastBackPressedTime = 0L
    private val BACK_PRESS_INTERVAL_MS = 1000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initRoutineNotification()

        setContent {
            MyRoutineTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPaddingValue ->
                    WebViewScreen(innerPaddingValue) { instance ->
                        webView = instance
                        registerBackPressDispatcher()
                    }
                }
            }
        }
    }

    private fun initRoutineNotification() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 100)
        }
    }

    @Composable
    private fun WebViewScreen(
        innerPaddingValue: PaddingValues, onWebViewReady: (WebView) -> Unit
    ) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                    }

                    webChromeClient = object : WebChromeClient() {
                        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                            Log.e(
                                "WebViewConsole",
                                "${consoleMessage?.message()} -- From line ${consoleMessage?.lineNumber()} of ${consoleMessage?.sourceId()}"
                            )
                            return true
                        }
                    }
                    addJavascriptInterface(WebBridge(context), "webBridge")
                    loadUrl(AppConfig.WEB_URL)
                    onWebViewReady(this)
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPaddingValue)
        )
    }

    private fun registerBackPressDispatcher() {
        onBackPressedDispatcher.addCallback(this) {
            if (!::webView.isInitialized)
                return@addCallback

            val now = System.currentTimeMillis()
            if (now - lastBackPressedTime < BACK_PRESS_INTERVAL_MS) {
                Toast.makeText(
                    this@MainActivity, "앱을 종료합니다.", Toast.LENGTH_SHORT
                ).show()
                finish()
            } else {
                webView.goBack()
                lastBackPressedTime = now
            }
        }
    }
}
