package fr.esgi.deezerplayer.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Album(
    val id: Int,
    val title: String,
    val cover: String,
    val NBTracks: Int,
    val releaseDate: String,
    val trackList: String,
    val artist: Artist
) : Parcelable