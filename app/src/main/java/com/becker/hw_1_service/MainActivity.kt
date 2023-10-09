package com.becker.hw_1_service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var musicPlayerService: MusicPlayerService
    private var isServiceBound: Boolean = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicPlayerService.MusicPlayerBinder
            musicPlayerService = binder.getService()
            isServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isServiceBound = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnPrevious: ImageButton = findViewById(R.id.btnPrevious)
        val btnPause: ImageButton = findViewById(R.id.btnPause)
        val btnPlay: ImageButton = findViewById(R.id.btnPlay)
        val btnNext: ImageButton = findViewById(R.id.btnNext)

        btnPrevious.setOnClickListener {
            musicPlayerService.previous()
        }

        btnPause.setOnClickListener {
            musicPlayerService.pause()
        }

        btnPlay.setOnClickListener {
            musicPlayerService.play()
        }

        btnNext.setOnClickListener {
            musicPlayerService.next()
        }

        val intent = Intent(this, MusicPlayerService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        musicPlayerService.stopPlayback()
        unbindService(serviceConnection)
    }
}