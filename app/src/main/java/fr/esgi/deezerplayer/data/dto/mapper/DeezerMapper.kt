package fr.esgi.deezerplayer.data.dto.mapper

import android.util.Log
import fr.esgi.deezerplayer.data.dto.AlbumResponseDTO
import fr.esgi.deezerplayer.model.Album

class AlbumsResponseMapper {

    fun map(albumsResponse: AlbumResponseDTO): List<Album> {
        val albumListDTO = albumsResponse.albumList

        return albumListDTO.map { albumDto ->
            Album(albumDto.id, albumDto.title, albumDto.cover_medium, albumDto.nb_tracks, albumDto.release_date)
        }
    }
}