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
                                }
                            }

                            (resultMsg?.obj as? WebView.WebViewTransport)?.webView = newWebView
                            resultMsg?.sendToTarget()

                            return true
                        }

                        override fun onJsAlert(
                            view: WebView, url: String?, message: String?, result: JsResult
                        ): Boolean {
                            AlertDialog.Builder(view.context).setTitle("알림").setMessage(message)
                                .setPositiveButton("확인") { _: DialogInterface?, _: Int ->
                                    result.confirm()
                                }.setCancelable(false).create().show()
                            return true
                        }

                        override fun onJsConfirm(
                            view: WebView, url: String?, message: String?, result: JsResult
                        ): Boolean {
                            AlertDialog.Builder(view.context).setTitle("확인").setMessage(message)
                                .setPositiveButton("예") { _: DialogInterface?, _: Int ->
                                    result.confirm()
                                }.setNegativeButton("아니오") { _: DialogInterface?, _: Int ->
                                    result.cancel()
                                }.setCancelable(false).create().show()
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
            if (::webView.isInitialized && webView.canGoBack()) {
                webView.goBack()
                return@addCallback
            }

            val now = System.currentTimeMillis()
            if (now - lastBackPressedTime < 2000) {
                finish()
            } else {
                lastBackPressedTime = now
                Toast.makeText(
                    this@MainActivity, "뒤로 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
