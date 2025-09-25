package com.example.myroutine.bridge

import android.content.Context
import android.webkit.JavascriptInterface
import com.example.myroutine.notification.AlarmScheduler
import com.example.myroutine.notification.NotificationHelper
import org.json.JSONObject

class WebBridge(val context: Context) {
    @JavascriptInterface
    fun postMessage(message: String) {
        val json = JSONObject(message)
        val type = json.getString("type")

        if (type == "TODAY_WORKOUT_EXISTS") {
            val payload = json.getJSONArray("payload")

            val msg = if (payload.length() > 0) {
                "오늘 운동이 이미 등록되어 있습니다!"
            } else {
                "오늘 운동을 시작해보세요!"
            }
            NotificationHelper.createNotificationChannel(context)
            AlarmScheduler.setDailyAlarm(context)
        }
    }
}