package com.zorbeytorunoglu.playerlib.listener

import android.media.AudioManager

class AudioFocusListener(
    private val onFocusLoss: (() -> Unit)? = null,
    private val onFocusLossTransient: (() -> Unit)? = null,
    private val onFocusLossCanDuck: (() -> Unit)? = null,
    private val onFocusGain: (() -> Unit)? = null,
): AudioManager.OnAudioFocusChangeListener {
    override fun onAudioFocusChange(focusChange: Int) {

        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                onFocusLoss?.invoke()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                onFocusLossTransient?.invoke()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                onFocusLossCanDuck?.invoke()
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                onFocusGain?.invoke()
            }
        }

    }
}