package com.zorbeytorunoglu.playerlib

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionToken
import com.zorbeytorunoglu.playerlib.listener.PlayerEvent
import com.zorbeytorunoglu.playerlib.listener.PlayerListener
import com.zorbeytorunoglu.playerlib.model.PlaybackDuration
import com.zorbeytorunoglu.playerlib.model.Track
import com.zorbeytorunoglu.playerlib.model.toMediaItem
import com.zorbeytorunoglu.playerlib.service.PlaybackService

private const val DEFAULT_CHANNEL_ID = "player_lib_channel"
private const val DEFAULT_NOTIFICATION_ID = 5858
private const val DEFAULT_CHANNEL_NAME = "Player Lib Channel"

class PlayerLib(internal val config: Config) {

    private val context
        get() = config.context

    private val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))

    private var mediaController: MediaController? = null

    internal val playerListener = PlayerListener(config)

    init {

        val controllableFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllableFuture.addListener({
            mediaController = if (controllableFuture.isDone) {
                controllableFuture.get().also {
                    if (config.periodicPositionUpdateEnabled == true) {
                        PositionUpdateTracker(
                            updateIntervalMs = config.positionUpdateDelay,
                            mediaController = it,
                            onUpdate = config.onPlaybackPositionUpdate
                        ).start()
                    }
                }
            } else {
                null
            }
        }, ContextCompat.getMainExecutor(context))

    }

    fun play(track: Track) {
        mediaController?.setMediaItem(track.toMediaItem())
        mediaController?.prepare()
        mediaController?.playWhenReady = true
    }

    fun play(tracks: List<Track>) {
        mediaController?.setMediaItems(tracks.map(Track::toMediaItem))
        mediaController?.prepare()
        mediaController?.playWhenReady = true
    }

    fun pause() {
        mediaController?.pause()
    }

    fun seekToNextMediaItem() {
        mediaController?.seekToNextMediaItem()
    }

    fun seekToNext() {
        mediaController?.seekToNext()
    }

    fun seekToPrevious() {
        mediaController?.seekToPrevious()
    }

    fun seekToPreviousMediaItem() {
        mediaController?.seekToPreviousMediaItem()
    }

    fun seekTo(position: Long) {
        mediaController?.seekTo(position)
    }

    fun seekToIndex(mediaIndex: Int, position: Long = 0L) {
        mediaController?.seekTo(mediaIndex, position)
    }

    fun addTracks(tracks: List<Track>) {
        mediaController?.addMediaItems(tracks.map(Track::toMediaItem))
    }

    fun addTrack(track: Track) {
        mediaController?.addMediaItem(track.toMediaItem())
    }

    fun release() {
        mediaController?.release()
    }

    fun stop() {
        mediaController?.stop()
    }

    fun removeTracks(fromIndex: Int, toIndex: Int) {
        mediaController?.removeMediaItems(fromIndex, toIndex)
    }

    fun clearMediaItems() {
        mediaController?.clearMediaItems()
    }

    fun addPlayerListener(listener: Player.Listener) {
        mediaController?.addListener(listener)
    }

    fun removePlayerListener(listener: Player.Listener) {
        mediaController?.removeListener(listener)
    }

    fun moveMediaItem(fromIndex: Int, toIndex: Int) {
        mediaController?.moveMediaItem(fromIndex, toIndex)
    }

    fun moveMediaItems(fromIndex: Int, toIndex: Int, newIndex: Int) {
        mediaController?.moveMediaItems(fromIndex, toIndex, newIndex)
    }

    fun setMediaItems(mediaItems: List<MediaItem>) {
        mediaController?.setMediaItems(mediaItems)
    }

    fun setMediaItem(mediaItem: MediaItem) {
        mediaController?.setMediaItem(mediaItem)
    }

    fun setMediaItems(mediaItems: List<MediaItem>, startIndex: Int, startPositionMs: Long) {
        mediaController?.setMediaItems(mediaItems, startIndex, startPositionMs)
    }

    fun play() {
        mediaController?.play()
    }

    fun setPlayWhenReady(playWhenReady: Boolean) {
        mediaController?.playWhenReady = playWhenReady
    }

    class Config private constructor(
        internal val context: Context,
        internal val sessionActivity: Class<out Activity>?,
        internal val largeIcon: Bitmap?,
        internal val smallIcon: IconCompat?,
        internal val positionUpdateDelay: Long,
        internal val redirectionIntentBundle: Bundle?,
        internal val channelId: String,
        internal val channelName: String,
        internal val notificationId: Int,
        internal val smallIconResourceId: Int?,
        internal val periodicPositionUpdateEnabled: Boolean?,
        internal val setUseRewindAction: Boolean,
        internal val setUseFastForwardAction: Boolean,
        internal val setUseRewindActionInCompactView: Boolean,
        internal val setUseFastForwardActionInCompactView: Boolean,
        internal val setUseChronometer: Boolean,
        internal val setUsePlayPauseActions: Boolean,
        internal val setUseNextAction: Boolean,
        internal val setUsePreviousAction: Boolean,
        internal val setUseStopAction: Boolean,
        internal val setColorized: Boolean?,
        internal val setColor: Int?,
        internal val setShowPlayButtonIfPlaybackIsSuppressed: Boolean?,
        internal val setShouldStayAwake: Boolean,
        internal val onCreated: (() -> Unit)?,
        internal val onDestroy: (() -> Unit)?,
        internal val onPlaybackPositionUpdate: ((PlaybackDuration?, MediaItem?) -> Unit)?,
        internal val onTaskRemoved: (() -> Unit)?,
        internal val onGetSession: ((MediaSession?) -> Unit)?,
        internal val onStartCommand: (() -> Unit)?,
        internal val onPlayerEvent: ((PlayerEvent) -> Unit)?,
        internal val onAudioFocusLoss: (() -> Unit)?,
        internal val onAudioFocusGain: (() -> Unit)?,
        internal val onAudioFocusLossTransient: (() -> Unit)?,
        internal val onAudioFocusLossTransientCanDuck: (() -> Unit)?
    ) {
        class Builder {
            private var context: Context? = null
            private var sessionActivity: Class<out Activity>? = null
            private var largeIcon: Bitmap? = null
            private var smallIcon: IconCompat? = null
            private var positionUpdateDelay: Long = 1000L
            private var redirectionIntentBundle: Bundle? = null
            private var channelId: String = DEFAULT_CHANNEL_ID
            private var channelName: String = DEFAULT_CHANNEL_NAME
            private var notificationId: Int = DEFAULT_NOTIFICATION_ID
            private var smallIconResourceId: Int? = null
            private var periodicPositionUpdateEnabled: Boolean? = null
            private var useRewindAction: Boolean = true
            private var rewindActionInCompactView: Boolean = true
            private var useFastForwardAction: Boolean = true
            private var useFastForwardActionInCompactView: Boolean = true
            private var useChronometer: Boolean = true
            private var usePlayPauseActions: Boolean = true
            private var useNextAction: Boolean = true
            private var usePreviousAction: Boolean = true
            private var useStopAction: Boolean = true
            private var colorized: Boolean? = null
            private var color: Int? = null
            private var showPlayButtonIfPlaybackIsSuppressed: Boolean? = null
            private var shouldStayAwake: Boolean = true
            private var onCreated: (() -> Unit)? = null
            private var onDestroy: (() -> Unit)? = null
            private var onPlaybackPositionUpdate: ((PlaybackDuration?, MediaItem?) -> Unit)? = null
            private var onTaskRemoved: (() -> Unit)? = null
            private var onGetSession: ((MediaSession?) -> Unit)? = null
            private var onStartCommand: (() -> Unit)? = null
            private var onPlayerEvent: ((PlayerEvent) -> Unit)? = null
            private var onAudioFocusLoss: (() -> Unit)? = null
            private var onAudioFocusGain: (() -> Unit)? = null
            private var onAudioFocusLossTransient: (() -> Unit)? = null
            private var onAudioFocusLossTransientCanDuck: (() -> Unit)? = null

            fun setContext(context: Context) = apply {
                this.context  = context.applicationContext
            }

            fun setSessionActivity(activityClass: Class<out Activity>) {
                this.sessionActivity = activityClass
            }

            fun setLargeIcon(bitmap: Bitmap) {
                this.largeIcon = bitmap
            }

            fun setSmallIcon(icon: IconCompat) {
                this.smallIcon = icon
            }

            fun setPositionUpdateDelay(delay: Long) {
                this.positionUpdateDelay = delay
            }

            fun setShouldStayAwake(shouldStayAwake: Boolean) = apply {
                this.shouldStayAwake = shouldStayAwake
            }

            fun setChannelId(channelId: String) = apply {
                this.channelId = channelId
            }

            fun setChannelName(channelName: String) = apply {
                this.channelName = channelName
            }

            fun setNotificationId(notificationId: Int) = apply {
                this.notificationId = notificationId
            }

            fun setOnDestroyed(onDestroy: () -> Unit) = apply {
                this.onDestroy = onDestroy
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
                this.useRewindAction = enabled
            }

            fun setUseFastForwardAction(enabled: Boolean) = apply {
                this.useFastForwardAction = enabled
            }

            fun setUseRewindActionInCompactView(enabled: Boolean) = apply {
                this.rewindActionInCompactView = enabled
            }

            fun setUseFastForwardActionInCompactView(enabled: Boolean) = apply {
                this.useFastForwardActionInCompactView = enabled
            }

            fun setUseChronometer(enabled: Boolean) = apply {
                this.useChronometer = enabled
            }

            fun setUsePlayPauseActions(enabled: Boolean) = apply {
                this.usePlayPauseActions = enabled
            }

            fun setUseNextAction(enabled: Boolean) = apply {
                this.useNextAction = enabled
            }

            fun setUsePreviousAction(enabled: Boolean) = apply {
                this.usePreviousAction = enabled
            }

            fun setUseStopAction(enabled: Boolean) = apply {
                this.useStopAction = enabled
            }

            fun setColorized(colorized: Boolean) = apply {
                this.colorized = colorized
            }

            fun setColor(color: Int) = apply {
                this.color = color
            }

            fun setShowPlayButtonIfPlaybackIsSuppressed(showPlayButtonIfPlaybackIsSuppressed: Boolean) = apply {
                this.showPlayButtonIfPlaybackIsSuppressed = showPlayButtonIfPlaybackIsSuppressed
            }

            fun build(): Config {
                requireNotNull(context) { "Context is required." }

                val config = Config(
                    context = context!!,
                    sessionActivity = sessionActivity,
                    largeIcon = largeIcon,
                    smallIcon = smallIcon,
                    positionUpdateDelay = positionUpdateDelay,
                    redirectionIntentBundle = redirectionIntentBundle,
                    channelId = channelId,
                    channelName = channelName,
                    notificationId = notificationId,
                    smallIconResourceId = smallIconResourceId,
                    periodicPositionUpdateEnabled = periodicPositionUpdateEnabled,
                    setUseRewindAction = useRewindAction,
                    setUseFastForwardAction = useFastForwardAction,
                    setUseRewindActionInCompactView = rewindActionInCompactView,
                    setUseFastForwardActionInCompactView = useFastForwardActionInCompactView,
                    setUseChronometer = useChronometer,
                    setUsePlayPauseActions = usePlayPauseActions,
                    setUseNextAction = useNextAction,
                    setUsePreviousAction = usePreviousAction,
                    setUseStopAction = useStopAction,
                    setColorized = colorized,
                    setColor = color,
                    setShowPlayButtonIfPlaybackIsSuppressed = showPlayButtonIfPlaybackIsSuppressed,
                    setShouldStayAwake = shouldStayAwake,
                    onCreated = onCreated,
                    onDestroy = onDestroy,
                    onPlaybackPositionUpdate = onPlaybackPositionUpdate,
                    onTaskRemoved = onTaskRemoved,
                    onGetSession = onGetSession,
                    onStartCommand = onStartCommand,
                    onPlayerEvent = onPlayerEvent,
                    onAudioFocusLoss = onAudioFocusLoss,
                    onAudioFocusGain = onAudioFocusGain,
                    onAudioFocusLossTransient = onAudioFocusLossTransient,
                    onAudioFocusLossTransientCanDuck = onAudioFocusLossTransientCanDuck
                )

                return config
            }
        }
    }

    private class PositionUpdateTracker(
        private val updateIntervalMs: Long = 1000L,
        private val mediaController: MediaController?,
        private val onUpdate: ((PlaybackDuration?, MediaItem?) -> Unit)?
    ) {
        private val handler = Handler(Looper.getMainLooper())

        private val updateRunnable = object : Runnable {
            override fun run() {
                mediaController?.let { controller ->
                    val duration = controller.duration
                    val position = controller.currentPosition

                    val playbackDuration = if (duration != C.TIME_UNSET) {
                        PlaybackDuration(duration, position)
                    } else null

                    onUpdate?.invoke(playbackDuration, controller.currentMediaItem)
                } ?: onUpdate?.invoke(null, null)

                handler.postDelayed(this, updateIntervalMs)
            }
        }

        fun start() {
            handler.post(updateRunnable)
        }

        fun stop() {
            handler.removeCallbacks(updateRunnable)
        }
    }

    object Command {
        data object Rewind {
            const val DISPLAY_NAME = "Rewind"
            const val COMMAND = Player.COMMAND_SEEK_BACK
        }
        data object FastForward {
            const val DISPLAY_NAME = "Fast Forward"
            const val COMMAND = Player.COMMAND_SEEK_FORWARD
        }
    }

    companion object {
        @Volatile
        private var _instance: PlayerLib? = null

        val instance: PlayerLib
            get() = _instance ?: throw UninitializedPropertyAccessException("PlayerLib is not initialized. Use PlayerLibFactory first.")

        internal fun initialize(playerLib: PlayerLib) {
            if (_instance == null) {
                synchronized(this) {
                    if (_instance == null) {
                        _instance = playerLib
                    }
                }
            }
        }

        val isInitialized: Boolean
            get() = _instance != null
    }

}