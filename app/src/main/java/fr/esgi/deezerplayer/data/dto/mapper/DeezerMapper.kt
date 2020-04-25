package fr.esgi.deezerplayer.data.dto.mapper

import fr.esgi.deezerplayer.data.dto.AlbumResponseDTO
import fr.esgi.deezerplayer.data.model.Album
import fr.esgi.deezerplayer.data.model.Artist

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
                albumDto.release_date,
                Artist(
                    albumDto.artist.id,
                    albumDto.artist.name,
                    albumDto.artist.picture_medium
                )
            )
        }
    }
}