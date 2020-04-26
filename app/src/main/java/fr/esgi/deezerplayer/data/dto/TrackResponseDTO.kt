package fr.esgi.deezerplayer.data.dto

import com.google.gson.annotations.SerializedName

data class TrackResponseDTO(@SerializedName("data") val trackList: List<TrackDTO>)

data class TrackDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("duration") val duration: Int,
    @SerializedName("track_position") val track_position: Int,
    @SerializedName("preview") val preview: String
)