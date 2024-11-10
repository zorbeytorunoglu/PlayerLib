package com.zorbeytorunoglu.playerlib.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import com.zorbeytorunoglu.playerlib.ADD_TRACKS_COMMAND
import com.zorbeytorunoglu.playerlib.ADD_TRACK_COMMAND
import com.zorbeytorunoglu.playerlib.PAUSE_PLAYER_COMMAND
import com.zorbeytorunoglu.playerlib.PLAY_HLS_COMMAND
import com.zorbeytorunoglu.playerlib.PLAY_PLAYER_COMMAND
import com.zorbeytorunoglu.playerlib.PLAY_PLAYLIST_HLS_COMMAND
import com.zorbeytorunoglu.playerlib.PlayerLib
import com.zorbeytorunoglu.playerlib.PlayerLibSingleton
import com.zorbeytorunoglu.playerlib.SEEK_TO_BUNDLE_KEY
import com.zorbeytorunoglu.playerlib.SEEK_TO_COMMAND
import com.zorbeytorunoglu.playerlib.SEEK_TO_INDEX_MEDIA_INDEX_BUNDLE_KEY
import com.zorbeytorunoglu.playerlib.SEEK_TO_INDEX_COMMAND
import com.zorbeytorunoglu.playerlib.SEEK_TO_INDEX_POSITION_BUNDLE_KEY
import com.zorbeytorunoglu.playerlib.SEEK_TO_NEXT_COMMAND
import com.zorbeytorunoglu.playerlib.SEEK_TO_NEXT_MEDIA_COMMAND
import com.zorbeytorunoglu.playerlib.SEEK_TO_PREVIOUS_COMMAND
import com.zorbeytorunoglu.playerlib.SEEK_TO_PREVIOUS_MEDIA_COMMAND
import com.zorbeytorunoglu.playerlib.STOP_PLAYER_COMMAND
import com.zorbeytorunoglu.playerlib.STOP_SERVICE_COMMAND
import com.zorbeytorunoglu.playerlib.adapter.MediaDescriptionAdapter
import com.zorbeytorunoglu.playerlib.listener.AudioFocusListener
import com.zorbeytorunoglu.playerlib.listener.NotificationListener
import com.zorbeytorunoglu.playerlib.listener.PlayerListener
import com.zorbeytorunoglu.playerlib.model.PlaybackDuration
import com.zorbeytorunoglu.playerlib.model.Track
import com.zorbeytorunoglu.playerlib.model.toMediaItem
import com.zorbeytorunoglu.playerlib.util.getTrack
import com.zorbeytorunoglu.playerlib.util.getTracks

@UnstableApi
class PlayerService: MediaSessionService() {

    private lateinit var playerLib: PlayerLib

    private lateinit var audioManager: AudioManager
    private var audioFocusRequest: AudioFocusRequest? = null
    private var player: ExoPlayer? = null
    private var mediaSession: MediaSession? = null
    private lateinit var playerNotificationManager: PlayerNotificationManager
    private lateinit var playerListener: PlayerListener

    private val onPositionUpdateHandler = Handler(Looper.getMainLooper())

    private val updatePositionRunnable = object : Runnable {
        override fun run() {
            playerLib.onPlaybackPositionUpdate?.invoke(
                if (player?.currentPosition == null || player?.duration == C.TIME_UNSET || player?.duration == null)
                    null
                else
                    PlaybackDuration(
                        player!!.duration,
                        player!!.currentPosition
                    ),
                player?.currentMediaItem
            )
            onPositionUpdateHandler.postDelayed(this, 1000L)
        }
    }

    override fun onCreate() {
        super.onCreate()

        if (!PlayerLibSingleton.isInitialized)
            throw IllegalStateException("PlayerLib must be initialized before starting the service. Read the Wiki.")

        playerLib = PlayerLibSingleton.instance

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(audioAttributes)
                .setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener(
                    AudioFocusListener(
                        onFocusGain = { playerLib.onAudioFocusGain },
                        onFocusLoss = { playerLib.onAudioFocusLoss },
                        onFocusLossTransient = { playerLib.onAudioFocusLossTransient },
                        onFocusLossCanDuck = { playerLib.onAudioFocusLossTransientCanDuck }
                    )
                )
                .build()
            val result = audioManager.requestAudioFocus(audioFocusRequest!!)
            if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                player?.pause()
            }
        }

        player = ExoPlayer.Builder(this).apply {
            if (playerLib.setShouldStayAwake) {
                setWakeMode(C.WAKE_MODE_NETWORK)
                setPauseAtEndOfMediaItems(false)
            }
        }.build()

        mediaSession = MediaSession.Builder(this, player!!).apply {
            playerLib.periodicPositionUpdateEnabled?.let { setPeriodicPositionUpdateEnabled(it) }
        }.build()

        playerNotificationManager = PlayerNotificationManager.Builder(
            this,
            playerLib.notificationId,
            playerLib.channelId
        ).apply {
            setMediaDescriptionAdapter(
                MediaDescriptionAdapter(
                    this@PlayerService,
                    playerLib.tracks,
                    playerLib.redirectionActivityClass
                )
            )
            setNotificationListener(
                NotificationListener(
                    onOngoing = { playerLib.onNotificationOngoing?.invoke() },
                    onCancelled = { playerLib.onNotificationCancelled?.invoke() },
                    onStopped = { playerLib.onNotificationStopped?.invoke() }
                )
            )
            playerLib.smallIconResourceId?.let { setSmallIconResourceId(it) }
        }.build().apply {
            setPlayer(player)
            setMediaSessionToken(mediaSession!!.platformToken)
            setUseRewindAction(playerLib.setUseRewindAction)
            setUseFastForwardAction(playerLib.setUseFastForwardAction)
            setUseRewindActionInCompactView(playerLib.setUseRewindActionInCompactView)
            setUseFastForwardActionInCompactView(playerLib.setUseFastForwardActionInCompactView)
            setUseChronometer(playerLib.setUseChronometer)
            setUsePlayPauseActions(playerLib.setUsePlayPauseActions)
            setUseNextAction(playerLib.setUseNextAction)
            setUsePreviousAction(playerLib.setUsePreviousAction)
            setUseStopAction(playerLib.setUseStopAction)
            if (playerLib.setColorized != null && playerLib.setColor != null) {
                setColorized(playerLib.setColorized!!)
                setColor(playerLib.setColor!!)
            }
            playerLib.setShowPlayButtonIfPlaybackIsSuppressed?.let { setShowPlayButtonIfPlaybackIsSuppressed(it) }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                playerLib.channelId,
                playerLib.channelName,
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        playerListener = PlayerListener(playerLib)

        player?.addListener(playerListener)

        onPositionUpdateHandler.post(updatePositionRunnable)

        playerLib.onCreated?.invoke()

    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        onPositionUpdateHandler.removeCallbacksAndMessages(null)
        mediaSession?.player?.let {
            if (!it.playWhenReady
                || it.mediaItemCount == 0
                || it.playbackState == ExoPlayer.STATE_ENDED)
                stopSelf()
        }
        playerLib.onTaskRemoved?.invoke()
    }

    override fun onDestroy() {
        if (!playerLib.setShouldStayAwake) {
            releaseService()
            playerLib.onDestroy?.invoke()
            super.onDestroy()
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        playerLib.onGetSession?.invoke(mediaSession)
        return mediaSession
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {

            when (intent.action) {

                PLAY_PLAYER_COMMAND -> {
                    player?.play()
                }
                PAUSE_PLAYER_COMMAND -> {
                    player?.pause()
                }
                PLAY_HLS_COMMAND -> {
                    intent.extras?.getTrack()?.let { track -> playHls(track) }
                }
                PLAY_PLAYLIST_HLS_COMMAND -> {
                    intent.extras?.getTracks()?.let { tracks -> playHls(tracks) }
                }
                STOP_PLAYER_COMMAND -> {
                    player?.stop()
                }
                STOP_SERVICE_COMMAND -> {
                    releaseService()
                }
                ADD_TRACK_COMMAND -> {
                    intent.extras?.getTrack()?.let { track -> player?.addMediaItem(track.toMediaItem()) }
                }
                SEEK_TO_NEXT_COMMAND -> {
                    player?.seekToNext()
                }
                SEEK_TO_PREVIOUS_COMMAND -> {
                    player?.seekToPrevious()
                }
                ADD_TRACKS_COMMAND -> {
                    intent.extras?.getTracks()?.let { tracks -> player?.addMediaItems(tracks.map { it.toMediaItem() }) }
                }
                SEEK_TO_COMMAND -> {
                    intent.extras?.getLong(SEEK_TO_BUNDLE_KEY)?.let { position -> player?.seekTo(position) }
                }
                SEEK_TO_INDEX_COMMAND -> {
                    intent.extras?.getInt(SEEK_TO_INDEX_MEDIA_INDEX_BUNDLE_KEY)?.let { index ->
                        val position = intent.extras?.getLong(SEEK_TO_INDEX_POSITION_BUNDLE_KEY) ?: 0L
                        player?.seekTo(index, position)
                    }
                }
                SEEK_TO_NEXT_MEDIA_COMMAND -> {
                    player?.seekToNextMediaItem()
                }
                SEEK_TO_PREVIOUS_MEDIA_COMMAND -> {
                    player?.seekToPreviousMediaItem()
                }

                else -> throw IllegalArgumentException("Unknown PlayerService command.")
            }

        }

        playerLib.onStartCommand?.invoke()

        return super.onStartCommand(intent, flags, startId)

    }

    private fun playHls(track: Track) {
        val dataSourceFactory = DefaultDataSource.Factory(this)
        val mediaSource = HlsMediaSource.Factory(dataSourceFactory)
            .createMediaSource(track.toMediaItem())
        player?.setMediaSource(mediaSource)
        player?.prepare()
        player?.playWhenReady = true
    }

    private fun playHls(trackList: List<Track>) {
        val dataSourceFactory = DefaultDataSource.Factory(this)
        player?.setMediaSources(
            trackList.map { track ->
                HlsMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(track.toMediaItem())
            }
        )
        player?.prepare()
        player?.playWhenReady = true
    }

    private fun releaseService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
        }
        playerNotificationManager.setPlayer(null)
        mediaSession?.run {
            player.removeListener(playerListener)
            player.release()
            release()
            mediaSession = null
        }
        onPositionUpdateHandler.removeCallbacksAndMessages(null)
        stopSelf()
    }

}