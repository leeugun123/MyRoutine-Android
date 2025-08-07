package com.example.myroutine

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
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
import com.example.myroutine.ui.theme.MyRoutineTheme

class MainActivity : ComponentActivity() {

    private lateinit var webView: WebView
    private var lastBackPressedTime = 0L
    private val BACK_PRESS_INTERVAL_MS = 1000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                        setSupportMultipleWindows(true)
                        javaScriptCanOpenWindowsAutomatically = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                    }

                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?, request: WebResourceRequest?
                        ): Boolean {
                            return false
                        }
                    }

                    webChromeClient = object : WebChromeClient() {
                        override fun onCreateWindow(
                            view: WebView?,
                            isDialog: Boolean,
                            isUserGesture: Boolean,
                            resultMsg: Message?
                        ): Boolean {
                            val newWebView = WebView(context)
                            newWebView.apply {
                                isFocusable = true
                                isFocusableInTouchMode = true
                                settings.javaScriptEnabled = true
                                settings.javaScriptCanOpenWindowsAutomatically = true
                                settings.domStorageEnabled = true
                                settings.useWideViewPort = true
                                settings.loadWithOverviewMode = true
                                settings.setSupportMultipleWindows(true)
                            }

                            val dialog = android.app.Dialog(context)
                            dialog.apply {
                                setContentView(newWebView)
                                setCancelable(true)
                                window?.setLayout(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                show()
                            }

                            newWebView.webChromeClient = object : WebChromeClient() {
                                override fun onCloseWindow(window: WebView?) {
                                    dialog.dismiss()
                                    window?.destroy()
                                }
                            }

                            (resultMsg?.obj as? WebView.WebViewTransport)?.webView = newWebView
                            resultMsg?.sendToTarget()

                            return true
                        }

                        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                            Log.d(
                                "WebViewConsole",
                                "${consoleMessage?.message()} -- From line ${consoleMessage?.lineNumber()} of ${consoleMessage?.sourceId()}"
                            )
                            return true
                        }
                    }
                    loadUrl(AppConfig.WEB_URL)
                    onWebViewReady(this)
                }
            }, modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPaddingValue)
        )
    }

    private fun registerBackPressDispatcher() {
        onBackPressedDispatcher.addCallback(this) {
            if (!::webView.isInitialized) {
                return@addCallback
            }

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
