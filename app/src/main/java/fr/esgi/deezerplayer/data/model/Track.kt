package fr.esgi.deezerplayer.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Track(
    val id: Int,
    val title: String,
    val duration: Int,
    val trackPosition: Int,
    val song: String
) : Parcelable