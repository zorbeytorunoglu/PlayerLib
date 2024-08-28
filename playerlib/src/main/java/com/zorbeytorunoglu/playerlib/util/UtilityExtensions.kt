package com.zorbeytorunoglu.playerlib.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.zorbeytorunoglu.playerlib.ADD_TRACKS_COMMAND
import com.zorbeytorunoglu.playerlib.ADD_TRACK_COMMAND
import com.zorbeytorunoglu.playerlib.PAUSE_PLAYER_COMMAND
import com.zorbeytorunoglu.playerlib.PLAY_HLS_COMMAND
import com.zorbeytorunoglu.playerlib.PLAY_PLAYER_COMMAND
import com.zorbeytorunoglu.playerlib.PLAY_PLAYLIST_HLS_COMMAND
import com.zorbeytorunoglu.playerlib.SEEK_TO_BUNDLE_KEY
import com.zorbeytorunoglu.playerlib.SEEK_TO_COMMAND
import com.zorbeytorunoglu.playerlib.SEEK_TO_NEXT_COMMAND
import com.zorbeytorunoglu.playerlib.SEEK_TO_NEXT_MEDIA_COMMAND
import com.zorbeytorunoglu.playerlib.SEEK_TO_PREVIOUS_COMMAND
import com.zorbeytorunoglu.playerlib.SEEK_TO_PREVIOUS_MEDIA_COMMAND
import com.zorbeytorunoglu.playerlib.STOP_PLAYER_COMMAND
import com.zorbeytorunoglu.playerlib.STOP_SERVICE_COMMAND
import com.zorbeytorunoglu.playerlib.model.Track
import com.zorbeytorunoglu.playerlib.model.serialize
import com.zorbeytorunoglu.playerlib.service.PlayerService

fun Bundle.putTrack(track: Track) {
    putString(Track.TRACK_BUNDLE_KEY, track.serialize())
}

fun Bundle.putTracks(tracks: List<Track>) {
    putStringArray(Track.TRACKS_BUNDLE_KEY, tracks.map { it.serialize() }.toTypedArray())
}

fun Intent.putTrack(track: Track) {
    action = PLAY_HLS_COMMAND
    putExtra(Track.TRACK_BUNDLE_KEY, track.serialize())
}

@OptIn(UnstableApi::class)
fun Context.playTrack(track: Track) {
    Intent(this, PlayerService::class.java).apply {
        action = PLAY_HLS_COMMAND
        putTrack(track)
        startService(this)
    }
}

@OptIn(UnstableApi::class)
fun Context.playTracks(trackList: List<Track>) {
    Intent(this, PlayerService::class.java).apply {
        action = PLAY_PLAYLIST_HLS_COMMAND
        putTracks(trackList)
        startService(this)
    }
}

@OptIn(UnstableApi::class)
fun Context.addTrack(track: Track) {
    Intent(this, PlayerService::class.java).apply {
        action = ADD_TRACK_COMMAND
        putTrack(track)
        startService(this)
    }
}

@OptIn(UnstableApi::class)
fun Context.addTracks(trackList: List<Track>) {
    Intent(this, PlayerService::class.java).apply {
        action = ADD_TRACKS_COMMAND
        putTracks(trackList)
        startService(this)
    }
}

@OptIn(UnstableApi::class)
fun Context.pausePlayer() {
    Intent(this, PlayerService::class.java).apply {
        action = PAUSE_PLAYER_COMMAND
        startService(this)
    }
}

@OptIn(UnstableApi::class)
fun Context.stopPlayer() {
    Intent(this, PlayerService::class.java).apply {
        action = STOP_PLAYER_COMMAND
        startService(this)
    }
}

@OptIn(UnstableApi::class)
fun Context.stopPlayerService() {
    Intent(this, PlayerService::class.java).apply {
        action = STOP_SERVICE_COMMAND
        startService(this)
    }
}

@OptIn(UnstableApi::class)
fun Context.seekToNext() {
    Intent(this, PlayerService::class.java).apply {
        action = SEEK_TO_NEXT_COMMAND
        startService(this)
    }
}

@OptIn(UnstableApi::class)
fun Context.seekToPrevious() {
    Intent(this, PlayerService::class.java).apply {
        action = SEEK_TO_PREVIOUS_COMMAND
        startService(this)
    }
}

@OptIn(UnstableApi::class)
fun Context.seekToNextMedia() {
    Intent(this, PlayerService::class.java).apply {
        action = SEEK_TO_NEXT_MEDIA_COMMAND
        startService(this)
    }
}

@OptIn(UnstableApi::class)
fun Context.seekToPreviousMedia() {
    Intent(this, PlayerService::class.java).apply {
        action = SEEK_TO_PREVIOUS_MEDIA_COMMAND
        startService(this)
    }
}

@OptIn(UnstableApi::class)
fun Context.playPlayer() {
    Intent(this, PlayerService::class.java).apply {
        action = PLAY_PLAYER_COMMAND
        startService(this)
    }
}

@OptIn(UnstableApi::class)
fun Context.seekTo(position: Long) {
    Intent(this, PlayerService::class.java).apply {
        action = SEEK_TO_COMMAND
        putExtra(SEEK_TO_BUNDLE_KEY, position)
        startService(this)
    }
}

fun Intent.putTracks(tracks: List<Track>) {
    action = PLAY_PLAYLIST_HLS_COMMAND
    putExtra(Track.TRACKS_BUNDLE_KEY, tracks.map { it.serialize() }.toTypedArray())
}

fun Bundle.getTrack(): Track? = getString(Track.TRACK_BUNDLE_KEY)?.let { Track.from(it) }

fun Bundle.getTracks(): List<Track> = getStringArray(Track.TRACKS_BUNDLE_KEY)?.mapNotNull {
    Track.from(
        it
    )
} ?: emptyList()