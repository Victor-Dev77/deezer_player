package fr.esgi.deezerplayer.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Artist(val id: Int, val name: String, val picture: String) : Parcelable