package com.zorbeytorunoglu.playerlib

import android.app.Activity
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import com.zorbeytorunoglu.playerlib.listener.PlayerEvent
import com.zorbeytorunoglu.playerlib.model.PlaybackDuration
import com.zorbeytorunoglu.playerlib.model.Track

private const val DEFAULT_CHANNEL_ID = "player_lib_channel"
private const val DEFAULT_NOTIFICATION_ID = 5858
private const val DEFAULT_CHANNEL_NAME = "Player Lib Channel"

internal const val PLAY_HLS_COMMAND = "PLAYER_LIB_PLAY_SINGLE_HLS"
internal const val PLAY_PLAYLIST_HLS_COMMAND = "PLAYER_LIB_PLAY_PLAYLIST_HLS"
internal const val PAUSE_PLAYER_COMMAND = "PLAYER_LIB_PAUSE_PLAYER"
internal const val PLAY_PLAYER_COMMAND = "PLAYER_LIB_PLAY_PLAYER"
internal const val STOP_PLAYER_COMMAND = "PLAYER_LIB_STOP_PLAYER"
internal const val STOP_SERVICE_COMMAND = "PLAYER_LIB_STOP_SERVICE"
internal const val SEEK_TO_COMMAND = "PLAYER_LIB_SEEK_TO"
internal const val SEEK_TO_BUNDLE_KEY = "SEEK_TO"
internal const val SEEK_TO_NEXT_COMMAND = "PLAYER_LIB_SEEK_TO_NEXT"
internal const val SEEK_TO_PREVIOUS_COMMAND = "PLAYER_LIB_SEEK_TO_PREVIOUS"
internal const val SEEK_TO_NEXT_MEDIA_COMMAND = "PLAYER_LIB_SEEK_TO_NEXT_MEDIA"
internal const val SEEK_TO_PREVIOUS_MEDIA_COMMAND = "PLAYER_LIB_SEEK_TO_PREVIOUS_MEDIA"
internal const val ADD_TRACK_COMMAND = "PLAYER_LIB_ADD_TRACK"
internal const val ADD_TRACKS_COMMAND = "PLAYER_LIB_ADD_TRACKS"
internal const val REMOVE_TRACK_COMMAND = "PLAYER_LIB_REMOVE_TRACK"
internal const val REMOVE_TRACKS_COMMAND = "PLAYER_LIB_REMOVE_TRACKS"

@UnstableApi
class PlayerLib(
    internal val redirectionActivityClass: Class<Activity>,
    internal val redirectionIntentBundle: Bundle? = null,
    internal val channelId: String = DEFAULT_CHANNEL_ID,
    internal val channelName: String = "Player Lib Channel",
    internal val notificationId: Int = DEFAULT_NOTIFICATION_ID,
    internal val smallIconResourceId: Int? = null,
    internal val periodicPositionUpdateEnabled: Boolean? = null,
    internal val setUseRewindAction: Boolean = true,
    internal val setUseFastForwardAction: Boolean = true,
    internal val setUseRewindActionInCompactView: Boolean = true,
    internal val setUseFastForwardActionInCompactView: Boolean = true,
    internal val setUseChronometer: Boolean = true,
    internal val setUsePlayPauseActions: Boolean = true,
    internal val setUseNextAction: Boolean = true,
    internal val setUsePreviousAction: Boolean = true,
    internal val setUseStopAction: Boolean = true,
    internal val setColorized: Boolean? = null,
    internal val setColor: Int? = null,
    internal val setShowPlayButtonIfPlaybackIsSuppressed: Boolean? = null,
    internal val onCreated: (() -> Unit)? = null,
    internal val onDestroy: (() -> Unit)? = null,
    internal val onPlaybackPositionUpdate: ((PlaybackDuration?, MediaItem?) -> Unit)? = null,
    internal val onTaskRemoved: (() -> Unit)? = null,
    internal val onGetSession: ((MediaSession?) -> Unit)? = null,
    internal val onStartCommand: (() -> Unit)? = null,
    internal val onPlayerEvent: ((PlayerEvent) -> Unit)? = null,
    internal val onAudioFocusLoss: (() -> Unit)? = null,
    internal val onAudioFocusGain: (() -> Unit)? = null,
    internal val onAudioFocusLossTransient: (() -> Unit)? = null,
    internal val onAudioFocusLossTransientCanDuck: (() -> Unit)? = null
) {

    internal var tracks: List<Track> = emptyList()

    internal var onNotificationOngoing: (() -> Unit)? = null
    internal var onNotificationStopped: (() -> Unit)? = null
    internal var onNotificationCancelled: (() -> Unit)? = null

    class Builder {

        private var redirectionActivityClass: Class<Activity>? = null
        private var redirectionIntentBundle: Bundle? = null
        private var channelId: String? = DEFAULT_CHANNEL_ID
        private var channelName: String? = DEFAULT_CHANNEL_NAME
        private var notificationId: Int? = DEFAULT_NOTIFICATION_ID
        private var smallIconResourceId: Int? = null
        private var periodicPositionUpdateEnabled: Boolean? = null
        private var setUseRewindAction: Boolean = true
        private var setUseFastForwardAction: Boolean = true
        private var setUseFastForwardActionInCompactView: Boolean = true
        private var setUseChronometer: Boolean = true
        private var setUsePlayPauseActions: Boolean = true
        private var setUseNextAction: Boolean = true
        private var setUsePreviousAction: Boolean = true
        private var setUseStopAction: Boolean = true
        private var setColorized: Boolean? = null
        private var setColor: Int? = null
        private var setShowPlayButtonIfPlaybackIsSuppressed: Boolean? = null
        private var onCreated: (() -> Unit)? = null
        private var onStarted: (() -> Unit)? = null
        private var onResumed: (() -> Unit)? = null
        private var onDestroyed: (() -> Unit)? = null
        private var onCancelled: (() -> Unit)? = null
        private var onTaskRemoved: (() -> Unit)? = null
        private var onGetSession: ((MediaSession?) -> Unit)? = null
        private var onStartCommand: (() -> Unit)? = null
        private var onPlayerEvent: ((PlayerEvent) -> Unit)? = null
        private var onAudioFocusLoss: (() -> Unit)? = null
        private var onAudioFocusGain: (() -> Unit)? = null
        private var onAudioFocusLossTransient: (() -> Unit)? = null
        private var onAudioFocusLossTransientCanDuck: (() -> Unit)? = null
        private var onPlaybackPositionUpdate: ((PlaybackDuration?, MediaItem?) -> Unit)? = null

        fun setChannelId(channelId: String) = apply {
            this.channelId = channelId
        }

        fun setChannelName(channelName: String) = apply {
            this.channelName = channelName
        }

        fun setNotificationId(notificationId: Int) = apply {
            this.notificationId = notificationId
        }

        fun setOnStarted(onStarted: () -> Unit) = apply {
            this.onStarted = onStarted
        }

        fun setOnResumed(onResumed: () -> Unit) = apply {
            this.onResumed = onResumed
        }

        fun setOnDestroyed(onDestroyed: () -> Unit) = apply {
            this.onDestroyed = onDestroyed
        }

        fun setOnCancelled(onCancelled: () -> Unit) = apply {
            this.onCancelled = onCancelled
        }

        fun setOnTaskRemoved(onTaskRemoved: () -> Unit) = apply {
            this.onTaskRemoved = onTaskRemoved
        }

        fun setOnCreated(onCreated: () -> Unit) = apply {
            this.onCreated = onCreated
        }

        fun setOnGetSession(onGetSession: (MediaSession?) -> Unit) = apply {
            this.onGetSession = onGetSession
        }

        fun setOnStartCommand(onStartCommand: () -> Unit) = apply {
            this.onStartCommand = onStartCommand
        }

        fun setOnPlayerEvent(onPlayerEvent: (PlayerEvent) -> Unit) = apply {
            this.onPlayerEvent = onPlayerEvent
        }

        fun setOnPlaybackPositionUpdate(onPlaybackPositionUpdate: (PlaybackDuration?, MediaItem?) -> Unit) = apply {
            this.onPlaybackPositionUpdate = onPlaybackPositionUpdate
        }

        fun setOnAudioFocusLoss(onAudioFocusLoss: () -> Unit) = apply {
            this.onAudioFocusLoss = onAudioFocusLoss
        }

        fun setOnAudioFocusGain(onAudioFocusGain: () -> Unit) = apply {
            this.onAudioFocusGain = onAudioFocusGain
        }

        fun setOnAudioFocusLossTransient(onAudioFocusLossTransient: () -> Unit) = apply {
            this.onAudioFocusLossTransient = onAudioFocusLossTransient
        }

        fun setOnAudioFocusLossTransientCanDuck(onAudioFocusLossTransientCanDuck: () -> Unit) = apply {
            this.onAudioFocusLossTransientCanDuck = onAudioFocusLossTransientCanDuck
        }

        fun setMainActivityClass(mainActivityClass: Class<Activity>) = apply {
            this.redirectionActivityClass = mainActivityClass
        }

        fun setRedirectionIntentBundle(redirectionIntentBundle: Bundle) = apply {
            this.redirectionIntentBundle = redirectionIntentBundle
        }

        fun setSmallIconResourceId(smallIconResourceId: Int) = apply {
            this.smallIconResourceId = smallIconResourceId
        }

        fun setPeriodicPositionUpdateEnabled(enabled: Boolean) = apply {
            this.periodicPositionUpdateEnabled = enabled
        }

        fun setUseRewindAction(enabled: Boolean) = apply {
            this.setUseNextAction = enabled
        }

        fun setUseFastForwardAction(enabled: Boolean) = apply {
            this.setUseNextAction = enabled
        }

        fun setUseRewindActionInCompactView(enabled: Boolean) = apply {
            this.setUseNextAction = enabled
        }

        fun setUseFastForwardActionInCompactView(enabled: Boolean) = apply {
            this.setUseNextAction = enabled
        }

        fun setUseChronometer(enabled: Boolean) = apply {
            this.setUseNextAction = enabled
        }

        fun setUsePlayPauseActions(enabled: Boolean) = apply {
            this.setUseNextAction = enabled
        }

        fun setUseNextAction(enabled: Boolean) = apply {
            this.setUseNextAction = enabled
        }

        fun setUsePreviousAction(enabled: Boolean) = apply {
            this.setUsePreviousAction = enabled
        }

        fun setUseStopAction(enabled: Boolean) = apply {
            this.setUseStopAction = enabled
        }

        fun setColorized(colorized: Boolean) = apply {
            this.setColorized = colorized
        }

        fun setColor(color: Int) = apply {
            this.setColor = color
        }

        fun setShowPlayButtonIfPlaybackIsSuppressed(showPlayButtonIfPlaybackIsSuppressed: Boolean) = apply {
            this.setShowPlayButtonIfPlaybackIsSuppressed = showPlayButtonIfPlaybackIsSuppressed
        }

        fun build(): PlayerLib {
            return PlayerLib(
                channelId = channelId ?: DEFAULT_CHANNEL_ID,
                channelName = channelName ?: DEFAULT_CHANNEL_NAME,
                notificationId = notificationId ?: DEFAULT_NOTIFICATION_ID,
                smallIconResourceId = smallIconResourceId,
                periodicPositionUpdateEnabled = periodicPositionUpdateEnabled,
                setUseRewindAction = setUseRewindAction,
                setUseFastForwardAction = setUseFastForwardAction,
                setUseFastForwardActionInCompactView = setUseFastForwardActionInCompactView,
                setUseChronometer = setUseChronometer,
                setUsePlayPauseActions = setUsePlayPauseActions,
                setUseNextAction = setUseNextAction,
                setUsePreviousAction = setUsePreviousAction,
                setUseStopAction = setUseStopAction,
                setColorized = setColorized,
                setColor = setColor,
                setShowPlayButtonIfPlaybackIsSuppressed = setShowPlayButtonIfPlaybackIsSuppressed,
                onDestroy = onDestroyed,
                onTaskRemoved = onTaskRemoved,
                onCreated = onCreated,
                onGetSession = onGetSession,
                onStartCommand = onStartCommand,
                onPlayerEvent = onPlayerEvent,
                onPlaybackPositionUpdate = onPlaybackPositionUpdate,
                onAudioFocusLoss = onAudioFocusLoss,
                onAudioFocusGain = onAudioFocusGain,
                onAudioFocusLossTransient = onAudioFocusLossTransient,
                onAudioFocusLossTransientCanDuck = onAudioFocusLossTransientCanDuck,
                redirectionActivityClass = redirectionActivityClass ?: throw IllegalArgumentException("MainActivityClass is required"),
                redirectionIntentBundle = redirectionIntentBundle
            ).also {
                PlayerLibSingleton.initialize(it)
            }
        }

    }

}