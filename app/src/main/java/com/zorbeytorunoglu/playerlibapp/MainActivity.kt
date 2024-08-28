package com.zorbeytorunoglu.playerlibapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 5858)
//            }
//        }
//
//        PlayerLib.Builder().apply {
//            setMainActivityClass(this@MainActivity.javaClass)
//        }.build()
//
//        findViewById<Button>(R.id.button).setOnClickListener {
//            val track = Track(
//                title = "Ramiz Dayı",
//                description = "Herkes öldürür sevdiğini kardeş.",
//                m3u8Url = "https://sesli-edergi.keove.com/birinci/tuncel-kurtiz-oysa-herkes-oldurur-sevdigini-siir-oscar-wilde.m3u8"
//            )
//            playTrack(track)
//        }

    }
}