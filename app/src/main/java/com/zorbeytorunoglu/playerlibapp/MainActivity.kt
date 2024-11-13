package com.zorbeytorunoglu.playerlibapp

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.util.UnstableApi
import com.zorbeytorunoglu.playerlib.PlayerLib
import com.zorbeytorunoglu.playerlib.model.Track
import com.zorbeytorunoglu.playerlib.util.pausePlayer
import com.zorbeytorunoglu.playerlib.util.playTracks
import com.zorbeytorunoglu.playerlib.util.seekToNext
import com.zorbeytorunoglu.playerlib.util.seekToPrevious
import com.zorbeytorunoglu.playerlib.util.stopPlayer

class MainActivity : AppCompatActivity() {
    @UnstableApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 5858)
            }
        }

        PlayerLib.Builder().apply {
            setMainActivityClass(this@MainActivity.javaClass)
            setShouldStayAwake(true)
        }.build()

        findViewById<Button>(R.id.button).setOnClickListener {
            val track = Track(
                title = "Ramiz Dayı",
                description = "Herkes öldürür sevdiğini kardeş.",
                m3u8Url = "https://sesli-edergi.keove.com/birinci/tuncel-kurtiz-oysa-herkes-oldurur-sevdigini-siir-oscar-wilde.m3u8"
            )

            val tracks = mutableListOf<Track>().apply {
                repeat(10) { add(track) }
            }.toList()

            playTracks(tracks)
        }
        findViewById<Button>(R.id.stop).setOnClickListener {
            stopPlayer()
        }
        findViewById<Button>(R.id.pause).setOnClickListener {
            pausePlayer()
        }
        findViewById<Button>(R.id.seekToNext).setOnClickListener {
            seekToNext()
        }
        findViewById<Button>(R.id.seekToPrevious).setOnClickListener {
            seekToPrevious()
        }

    }
}