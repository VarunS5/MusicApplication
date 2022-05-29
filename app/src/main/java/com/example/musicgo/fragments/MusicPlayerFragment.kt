package com.example.musicgo.fragments

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.musicgo.MusicServiceCallbacks
import com.example.musicgo.service.MusicService
import com.example.musicgo.R
import com.example.musicgo.datasource.MusicSource


class MusicPlayerFragment : Fragment(), MusicServiceCallbacks {
    private val args: MusicPlayerFragmentArgs by navArgs()
    private lateinit var musicSource: MusicSource
    private lateinit var musicService: MusicService
    private lateinit var musicSeekBar: SeekBar
    private lateinit var completedTime : TextView
    private lateinit var remainingTime : TextView
    private var isPlaying = true
    private var bound = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_music_player, container, false)
        var songIndex = args.songIndex
        musicSource = MusicSource.getInstance(view)!!
        var song = musicSource.getSongsList()[songIndex]
        val songTitle: TextView = view.findViewById(R.id.song_title_detail)
        musicSeekBar = view.findViewById(R.id.music_progress_detail)
        completedTime = view.findViewById(R.id.time_elapsed)
        remainingTime = view.findViewById(R.id.time_remaining)
        val pauseOrPlayButton: Button = view.findViewById(R.id.music_play_pause)
        val previousButton: Button = view.findViewById(R.id.previous)
        val nextButton: Button = view.findViewById(R.id.next)
        val intent = Intent(view.context, MusicService::class.java)
        songTitle.isSelected = true
        songTitle.ellipsize = TextUtils.TruncateAt.MARQUEE
        intent.putExtra("songData", song?.songData)
        view.context.stopService(intent)
        songTitle.text = song?.songName
        view.context.startService(intent)
        view.context.bindService(intent, musicServiceConnection, Context.BIND_AUTO_CREATE)
        pauseOrPlayButton.setBackgroundResource(R.drawable.pause)
        previousButton.setOnClickListener {
            pauseOrPlayButton.setBackgroundResource(R.drawable.pause)
            isPlaying = true
            if (songIndex == 0) {
                Toast.makeText(view.context, "No Songs Before", Toast.LENGTH_SHORT).show()
            } else {
                if(bound) {
                    view.context.unbindService(musicServiceConnection)
                }
                view.context.stopService(intent)
                songIndex -= 1
                song = musicSource.getSongsList()[songIndex]
                intent.putExtra("songData", song?.songData)
                intent.putExtra("sameSong", false)
                songTitle.text = song?.songName
                view.context.startService(intent)
                view.context.bindService(intent, musicServiceConnection, Context.BIND_AUTO_CREATE)
            }
        }
        nextButton.setOnClickListener {
            pauseOrPlayButton.setBackgroundResource(R.drawable.pause)
            isPlaying = true
            if (songIndex == musicSource.getSongsList().size - 1) {
                Toast.makeText(view.context, "No Songs After", Toast.LENGTH_SHORT).show()
            } else {
                view.context.stopService(intent)
                if(bound) {
                    view.context.unbindService(musicServiceConnection)
                }
                songIndex += 1
                song = musicSource.getSongsList()[songIndex]
                intent.putExtra("songData", song?.songData)
                intent.putExtra("sameSong", false)
                songTitle.text = song?.songName
                view.context.startService(intent)
                view.context.bindService(intent, musicServiceConnection, Context.BIND_AUTO_CREATE)
            }
        }
        pauseOrPlayButton.setOnClickListener {
            intent.putExtra("sameSong", true)
            isPlaying = if (isPlaying) {
                pauseOrPlayButton.setBackgroundResource(R.drawable.play)
                view.context.stopService(intent)
                if(bound) {
                    view.context.unbindService(musicServiceConnection)
                    bound = false
                }
                false
            } else {
                pauseOrPlayButton.setBackgroundResource(R.drawable.pause)
                view.context.startService(intent)
                view.context.bindService(intent,musicServiceConnection, Context.BIND_AUTO_CREATE)
                bound = true
                true
            }
        }
        return view
    }

    private val musicServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
            val musicBinder = binder as MusicService.MusicBinder
            musicService = musicBinder.getService()
            bound = true
            musicService.setCallBacks(this@MusicPlayerFragment)
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            bound = false
        }

    }

    override fun onStop() {
        super.onStop()
        if(bound){
            view?.context?.unbindService(musicServiceConnection)
            bound = false
        }
    }

    override fun setProgress(progress : Int?) {
        musicSeekBar.progress = progress!!/1000
        val minutes : Int = progress!!/(1000*60)
        val seconds : Int = progress!!/1000 - minutes*60
        var secondsString = ""
        if(seconds < 10){
            secondsString = "0$seconds"
        }
        completedTime.text = "$minutes:$secondsString"
        remainingTime.text = (musicSeekBar.max - progress!!/1000).toString()
    }

    override fun setDuration(duration: Int?) {
        musicSeekBar.max = duration!!/1000
    }

}