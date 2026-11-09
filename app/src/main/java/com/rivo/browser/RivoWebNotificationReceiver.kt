package com.rivo.browser

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationManager
import androidx.core.content.IntentCompat
import org.mozilla.geckoview.WebNotification

/**
 * Delivers notification clicks back to Gecko so web Notification.onclick handlers
 * receive the user interaction. The receiver is private to this application.
 */
class RivoWebNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notification = IntentCompat.getParcelableExtra(intent, EXTRA_NOTIFICATION, WebNotification::class.java) ?: return
        runCatching {
            notification.click()
            notification.dismiss()
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val id = ((notification.source ?: "") + "|" + notification.tag).hashCode()
            manager.cancel("rivo-web", id)
        }
    }

    companion object {
        const val EXTRA_NOTIFICATION = "com.rivo.browser.extra.WEB_NOTIFICATION"
    }
}
