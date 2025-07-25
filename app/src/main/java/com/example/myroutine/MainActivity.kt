package com.example.myroutine

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
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
import com.example.myroutine.ui.theme.MyRoutineTheme

class MainActivity : ComponentActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerBackPressDispatcher()
        setContent {
            MyRoutineTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPaddingValue ->
                    WebViewScreen(innerPaddingValue) { instance ->
                        webView = instance
                    }
                }
            }
        }
    }

    private fun registerBackPressDispatcher() {
        onBackPressedDispatcher.addCallback(this) {
            if (::webView.isInitialized && webView.canGoBack()) {
                webView.goBack()
            } else {
                finish()
            }
        }
    }
}

@Composable
private fun WebViewScreen(
    innerPaddingValue: PaddingValues,
    onWebViewReady: (WebView) -> Unit
) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
                loadUrl(AppConfig.WEB_URL)
                onWebViewReady(this)
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPaddingValue)
    )
}

