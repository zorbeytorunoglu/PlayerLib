package com.zorbeytorunoglu.playerlib.adapter

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerNotificationManager
import com.zorbeytorunoglu.playerlib.model.Track

private const val DEFAULT_TITLE = "E-Dergi"
private const val DEFAULT_DESCRIPTION = "Tüm dergi ve gazeteler artık kulağınızda!"

@UnstableApi
class MediaDescriptionAdapter(
    private val context: Context,
    private val tracks: List<Track>,
    private val redirectionActivityClass: Class<Activity>,
    private val redirectionIntentBundle: Bundle? = null
): PlayerNotificationManager.MediaDescriptionAdapter {
    override fun getCurrentContentTitle(player: Player): CharSequence {
        return player.currentMediaItem?.mediaMetadata?.title ?: DEFAULT_TITLE
    }

    override fun createCurrentContentIntent(player: Player): PendingIntent? {
        val intent = Intent(context, redirectionActivityClass).apply {
            redirectionIntentBundle?.let { bundle ->
                putExtras(bundle)
            }
        }
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    override fun getCurrentContentText(player: Player): CharSequence? {
        return player.currentMediaItem?.mediaMetadata?.description
    }

    override fun getCurrentLargeIcon(
        player: Player,
        callback: PlayerNotificationManager.BitmapCallback
    ): Bitmap? {
        // TODO: Will implement later
        return null
    }

}