package com.zorbeytorunoglu.playerlib

import androidx.media3.common.util.UnstableApi

@UnstableApi
object PlayerLibSingleton {

    lateinit var instance: PlayerLib
        private set

    fun initialize(playerLib: PlayerLib) {
        instance = playerLib
    }

    val isInitialized: Boolean
        get() = this::instance.isInitialized

}