package com.example.musicgo.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Song(
    val musicId: String,
    val songName: String,
    val songArtist: String,
    val songData: String,
    val songDuration: String
) : Parcelable {
}