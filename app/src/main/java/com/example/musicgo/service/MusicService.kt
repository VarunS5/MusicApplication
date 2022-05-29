package com.example.musicgo.service

import android.app.Service
import android.content.*
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import com.example.musicgo.MusicServiceCallbacks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MusicService : Service(), MediaPlayer.OnPreparedListener {

    private val mediaPlayer = MediaPlayer()
    private lateinit var sharedPreferences : SharedPreferences
    private var isSameSong = false
    private val binder = MusicBinder()
    private lateinit var musicServiceCallbacks: MusicServiceCallbacks
    private val scope = CoroutineScope(Dispatchers.Main)

    inner class MusicBinder : Binder(){
        fun getService(): MusicService{
            return this@MusicService
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val songData = intent?.getStringExtra("songData")
        isSameSong = intent?.getBooleanExtra("sameSong",false) as Boolean
        val uri = Uri.parse(songData)
        sharedPreferences = applicationContext.getSharedPreferences("musicPrefs",Context.MODE_PRIVATE)
        try {
            mediaPlayer.setDataSource(applicationContext, uri)

            mediaPlayer.setOnPreparedListener(this)
            mediaPlayer.prepareAsync()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return START_STICKY
    }
    override fun onDestroy() {
        super.onDestroy()
        val sharedPreferences = applicationContext.getSharedPreferences("musicPrefs",Context.MODE_PRIVATE)
        val edit : SharedPreferences.Editor = sharedPreferences.edit()
        edit.putInt("LastPosition",mediaPlayer.currentPosition)
        edit.apply()
        mediaPlayer.pause()
    }

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    override fun onPrepared(player: MediaPlayer?) {
        player?.start()
        player?.isLooping = true
        val currentPosition: Int = if(isSameSong){
            sharedPreferences.getInt("LastPosition",0)
        } else{
            0
        }
        if(currentPosition == player?.duration){
            player.seekTo(0)
        }
        else {
            player?.seekTo(currentPosition)
        }
        musicServiceCallbacks.setDuration(player?.duration)
        scope.launch {
            while (player?.isPlaying == true) {
                musicServiceCallbacks.setProgress(player?.currentPosition)
                delay(1000)
            }
        }

    }

    fun setCallBacks (callBacks: MusicServiceCallbacks){
        musicServiceCallbacks = callBacks
    }
}