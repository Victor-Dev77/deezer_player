package fr.esgi.deezerplayer.data.dto

import com.google.gson.annotations.SerializedName

data class AlbumResponseDTO(@SerializedName("data") val albumList: List<AlbumDTO>)

data class AlbumDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("cover_medium") val cover_medium: String,
    @SerializedName("nb_tracks") val nb_tracks: Int,
    @SerializedName("release_date") val release_date: String//,
   // @SerializedName("artist") val artist: ArtistDTO
)

data class ArtistDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("picture_medium") val picture_medium: String
)