package fr.esgi.deezerplayer.data.dto

import com.google.gson.annotations.SerializedName

// GSON avec @SerializedName recup champs du JSON
// recup "data" qui est le tableau contenant les Albums
data class AlbumResponseDTO(@SerializedName("data") val albumList: List<AlbumDTO>)

// un item dans le tableau "data"
// recup seulement qlq champs
data class AlbumDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("cover_medium") val cover_medium: String,
    @SerializedName("nb_tracks") val nb_tracks: Int,
    @SerializedName("release_date") val release_date: String
    // si on veut recup l'artiste, ajouter cette ligne
   // @SerializedName("artist") val artist: ArtistDTO
)

// champs contenu dans l'objet "artist" dans le JSON
data class ArtistDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("picture_medium") val picture_medium: String
)