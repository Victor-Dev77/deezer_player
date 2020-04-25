package fr.esgi.deezerplayer.data.dto.mapper

import fr.esgi.deezerplayer.data.dto.AlbumResponseDTO
import fr.esgi.deezerplayer.data.model.Album

class AlbumsResponseMapper {

    // map AlbumResponseDTO en List<Album>
    fun map(albumsResponse: AlbumResponseDTO): List<Album> {
        val albumListDTO = albumsResponse.albumList // = "data" du JSON

        return albumListDTO.map { albumDto ->
            Album(
                albumDto.id, // get champs de AlbumDTO dans AlbumResponseDTO
                albumDto.title,
                albumDto.cover_medium,
                albumDto.nb_tracks,
                albumDto.release_date
            )
        }
    }
}