package fr.esgi.deezerplayer.data.dto.mapper

import fr.esgi.deezerplayer.data.dto.AlbumDTO
import fr.esgi.deezerplayer.data.dto.AlbumResponseDTO
import fr.esgi.deezerplayer.data.model.Album
import fr.esgi.deezerplayer.data.model.Artist

class AlbumsResponseMapper {

    fun map(albumsResponse: AlbumResponseDTO): List<Album> {
        val albumListDTO = albumsResponse.albumList
        return albumListDTO.map { albumDto -> extractAlbum(albumDto) }
    }

    fun extractAlbum(albumDto: AlbumDTO): Album {
        val id: Int = albumDto.alternative?.id ?: albumDto.id
        val trackList: String = albumDto.alternative?.track_list ?: albumDto.track_list
        return Album(
            id,
            albumDto.title,
            albumDto.cover_medium,
            albumDto.nb_tracks,
            albumDto.release_date,
            trackList,
            Artist(
                albumDto.artist.id,
                albumDto.artist.name,
                albumDto.artist.picture_medium
            )
        )
    }
}