package fr.esgi.deezerplayer.data.repository

import fr.esgi.deezerplayer.data.api.TrackAPI
import fr.esgi.deezerplayer.data.dto.mapper.AlbumsResponseMapper
import fr.esgi.deezerplayer.data.dto.mapper.TrackResponseMapper
import fr.esgi.deezerplayer.data.model.Album
import fr.esgi.deezerplayer.data.model.Track

class TrackRepository(
    private val api: TrackAPI
) : ListenerAPIRequest() {

    suspend fun getTracks(albumID: Int): List<Track> {
        val tracksResponseDTO = apiRequest { api.getTracks(albumID) }
        return TrackResponseMapper().map(tracksResponseDTO)
    }

    suspend fun getAlbum(id: Int): Album {
        val albumResponseDTO = apiRequest { api.getAlbum(id) }
        return AlbumsResponseMapper().extractAlbum(albumResponseDTO)
    }

}