package com.example.musicgo

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs


class MusicPlayerFragment : Fragment() {
    private val args: MusicPlayerFragmentArgs by navArgs()
    private lateinit var musicSource: MusicSource
    private var isPlaying = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_music_player, container, false)
        var songIndex = args.songIndex
        musicSource = MusicSource.getInstance(view)!!
        var song = musicSource.getSongsList()[songIndex]
        val songTitle: TextView = view.findViewById(R.id.song_title_detail)
        val pauseOrPlayButton: Button = view.findViewById(R.id.music_play_pause)
        val previousButton: Button = view.findViewById(R.id.previous)
        val nextButton: Button = view.findViewById(R.id.next)
        val intent = Intent(view.context, MusicService::class.java)
        intent.putExtra("songData", song?.songData)
        view.context.stopService(intent)
        songTitle.text = song?.songName
        view.context.startService(intent)
        pauseOrPlayButton.setBackgroundResource(R.drawable.pause)
        previousButton.setOnClickListener {

            if (songIndex == 0) {
                Toast.makeText(view.context, "No Songs Before", Toast.LENGTH_SHORT).show()
            } else {

                view.context.stopService(intent)
                songIndex -= 1
                song = musicSource.getSongsList()[songIndex]
                intent.putExtra("songData", song?.songData)
                intent.putExtra("sameSong", false)
                songTitle.text = song?.songName
                view.context.startService(intent)
            }
        }
        nextButton.setOnClickListener {

            if (songIndex == musicSource.getSongsList().size - 1) {
                Toast.makeText(view.context, "No Songs After", Toast.LENGTH_SHORT).show()
            } else {
                view.context.stopService(intent)
                songIndex += 1
                song = musicSource.getSongsList()[songIndex]
                intent.putExtra("songData", song?.songData)
                intent.putExtra("sameSong", false)
                songTitle.text = song?.songName
                view.context.startService(intent)
            }
        }
        pauseOrPlayButton.setOnClickListener {
            intent.putExtra("sameSong", true)
            isPlaying = if (isPlaying) {
                pauseOrPlayButton.setBackgroundResource(R.drawable.play)
                view.context.stopService(intent)
                false
            } else {
                pauseOrPlayButton.setBackgroundResource(R.drawable.pause)
                view.context.startService(intent)
                true
            }
        }
        return view
    }

}