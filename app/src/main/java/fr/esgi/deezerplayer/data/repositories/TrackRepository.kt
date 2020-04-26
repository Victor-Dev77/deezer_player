package fr.esgi.deezerplayer.data.repositories

import fr.esgi.deezerplayer.data.api.TrackAPI
import fr.esgi.deezerplayer.data.dto.mapper.TrackResponseMapper
import fr.esgi.deezerplayer.data.model.Track

class TrackRepository (
    private val api: TrackAPI
) : ListenerAPIRequest() {

    suspend fun getTracks(albumID: Int): List<Track> {
        val tracksResponseDTO = apiRequest { api.getTracks(albumID) }
        return TrackResponseMapper().map(tracksResponseDTO)
    }

}