package com.example.musicgo.datasource

import android.database.Cursor
import android.provider.MediaStore
import android.view.View
import com.example.musicgo.model.Song


class MusicSource private constructor(view: View) {
    private val songsList: ArrayList<Song?> = ArrayList()
    private var cursor: Cursor? = null

    init {
        cursor = view.context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA
            ), null, null, null
        )
        if (cursor != null) {
            if (cursor!!.moveToFirst()) {
                do {
                    val displayName =
                        cursor!!.getString(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME))
                    val songId =
                        cursor!!.getString(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                    val songArtist =
                        cursor!!.getString(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST))
                    val songData =
                        cursor!!.getString(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                    val songDuration =
                        cursor!!.getString(cursor!!.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                    if (songDuration != null) {
                        val song = Song(songId, displayName, songArtist, songData, songDuration)
                        songsList.add(song)
                    }
                } while (cursor!!.moveToNext())
            }
        }
    }
    fun getSongsList(): ArrayList<Song?> {
        return songsList
    }
    companion object {
        private var instance: MusicSource? = null
        fun getInstance(view: View): MusicSource? {
            if (instance == null){
                instance = MusicSource(view)
            }
            return instance
        }
    }
}