package com.becker.hw_1_service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi

class MusicPlayerService : Service() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var songs: ArrayList<Int>
    private var currentIndex = 0
    private val binder: IBinder = MusicPlayerBinder()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

        songs = arrayListOf(
            R.raw.sample_song,
            R.raw.sample_song2,
            R.raw.sample_song3,
            R.raw.this_love,
            R.raw.mockingbird
        )

        mediaPlayer = MediaPlayer.create(applicationContext, songs[currentIndex])
        createNotificationChannel()
        showNotification()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.becker_music_player),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotification() {

        val playIntent = Intent(this, MusicPlayerService::class.java)
        playIntent.action = "PLAY"
        val playPendingIntent = PendingIntent.getService(
            this,
            0,
            playIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val pauseIntent = Intent(this, MusicPlayerService::class.java)
        pauseIntent.action = "PAUSE"
        val pausePendingIntent = PendingIntent.getService(
            this,
            0,
            pauseIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val nextIntent = Intent(this, MusicPlayerService::class.java)
        nextIntent.action = "NEXT"
        val nextPendingIntent = PendingIntent.getService(
            this,
            0,
            nextIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val previousIntent = Intent(this, MusicPlayerService::class.java)
        previousIntent.action = "PREVIOUS"
        val previousPendingIntent = PendingIntent.getService(
            this,
            0,
            previousIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("track name")
            .setContentText("singer")
            .setSmallIcon(R.drawable.ic_music_note)
            .addAction(R.drawable.ic_previous, "previous", previousPendingIntent)
            .addAction(R.drawable.ic_pause, "pause", pausePendingIntent)
            .addAction(R.drawable.ic_play, "play", playPendingIntent)
            .addAction(R.drawable.ic_next, "next", nextPendingIntent)
            .setStyle(Notification.MediaStyle().setShowActionsInCompactView(0, 1, 2))
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                "PLAY" -> {
                    play()
                }
                "PAUSE" -> {
                    pause()
                }
                "NEXT" -> {
                    next()
                }
                "PREVIOUS" -> {
                    previous()
                }
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    fun play() {
        mediaPlayer.start()
    }

    fun pause() {
        mediaPlayer.pause()
    }

    fun next() {
        if (currentIndex < songs.size - 1) {
            currentIndex++
        } else {
            currentIndex = 0
        }

        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }

        mediaPlayer = MediaPlayer.create(applicationContext, songs[currentIndex])
        mediaPlayer.start()
    }

    fun previous() {
        if (currentIndex > 0) {
            currentIndex--
        } else {
            currentIndex = songs.size - 1
        }

        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }

        mediaPlayer = MediaPlayer.create(applicationContext, songs[currentIndex])
        mediaPlayer.start()
    }

    fun stopPlayback() {
        mediaPlayer.release()
    }

    inner class MusicPlayerBinder : Binder() {
        fun getService(): MusicPlayerService = this@MusicPlayerService
    }

    override fun onDestroy() {
        super.onDestroy()
        stopSelf()
    }

    companion object {
        private const val CHANNEL_ID = "music_channel"
        private const val NOTIFICATION_ID = 1
    }
}