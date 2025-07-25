package com.example.myroutine

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.myroutine.ui.theme.MyRoutineTheme

class MainActivity : ComponentActivity() {

    private val WEB_URL = "https://calm-profiterole-ad8f27.netlify.app"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyRoutineTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPaddingValue ->
                    WebViewScreen(WEB_URL, innerPaddingValue)
                }
            }
        }
    }
}

@Composable
private fun WebViewScreen(url: String, innerPaddingValue: PaddingValues) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
                loadUrl(url)
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPaddingValue)
    )
}

