package com.zorbeytorunoglu.playerlib.listener

import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import com.zorbeytorunoglu.playerlib.PlayerLib

@OptIn(UnstableApi::class)
class PlayerListener(
    private val playerLib: PlayerLib
): Player.Listener {

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        playerLib.onPlayerEvent?.invoke(PlayerEvent.PlayerError(error))
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        playerLib.onPlayerEvent?.invoke(PlayerEvent.PlaybackStateChanged(playbackState))
    }

    override fun onDeviceVolumeChanged(volume: Int, muted: Boolean) {
        super.onDeviceVolumeChanged(volume, muted)
        playerLib.onPlayerEvent?.invoke(PlayerEvent.DeviceVolumeChanged(volume, muted))
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        playerLib.onPlayerEvent?.invoke(PlayerEvent.IsPlayingChanged(isPlaying))
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        playerLib.onPlayerEvent?.invoke(PlayerEvent.MediaItemTransition(mediaItem, reason))
    }

    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
        super.onMediaMetadataChanged(mediaMetadata)
        playerLib.onPlayerEvent?.invoke(PlayerEvent.MediaMetadataChanged(mediaMetadata))
    }

    override fun onPlayerErrorChanged(error: PlaybackException?) {
        super.onPlayerErrorChanged(error)
        playerLib.onPlayerEvent?.invoke(PlayerEvent.PlayerErrorChanged(error))
    }

    override fun onPositionDiscontinuity(
        oldPosition: Player.PositionInfo,
        newPosition: Player.PositionInfo,
        reason: Int
    ) {
        super.onPositionDiscontinuity(oldPosition, newPosition, reason)
        playerLib.onPlayerEvent?.invoke(
            PlayerEvent.OnPositionDiscontinuity(
                oldPosition,
                newPosition,
                reason
            )
        )
    }

}

sealed interface PlayerEvent {
    data class PlayerError(val exception: PlaybackException): PlayerEvent
    data class PlaybackStateChanged(val state: Int): PlayerEvent
    data class DeviceVolumeChanged(val volume: Int, val muted: Boolean): PlayerEvent
    data class IsPlayingChanged(val isPlaying: Boolean): PlayerEvent
    data class MediaItemTransition(val mediaItem: MediaItem?, val reason: Int): PlayerEvent
    data class MediaMetadataChanged(val mediaMetadata: MediaMetadata): PlayerEvent
    data class PlayerErrorChanged(val error: PlaybackException?): PlayerEvent
    data class OnPositionDiscontinuity(val oldPosition: Player.PositionInfo, val newPosition: Player.PositionInfo, val reason: Int):
        PlayerEvent
}