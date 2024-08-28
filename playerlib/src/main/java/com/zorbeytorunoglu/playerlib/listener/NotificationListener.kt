package com.zorbeytorunoglu.playerlib.listener

import android.app.Notification
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerNotificationManager

@UnstableApi
class NotificationListener(
    private val onOngoing: (() -> Unit)? = null,
    private val onStopped: (() -> Unit)? = null,
    private val onCancelled: (() -> Unit)? = null
): PlayerNotificationManager.NotificationListener {

    override fun onNotificationPosted(
        notificationId: Int,
        notification: Notification,
        ongoing: Boolean
    ) {
        if (ongoing) {
            onOngoing?.invoke()
        } else {
            onStopped?.invoke()
        }
    }

    override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
        onCancelled?.invoke()
    }

}