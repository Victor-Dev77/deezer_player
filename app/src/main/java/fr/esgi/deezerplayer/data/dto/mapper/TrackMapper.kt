package fr.esgi.deezerplayer.data.dto.mapper

import fr.esgi.deezerplayer.data.dto.TrackDTO
import fr.esgi.deezerplayer.data.dto.TrackResponseDTO
import fr.esgi.deezerplayer.data.model.Track

class TrackResponseMapper {

    fun map(tracksResponse: TrackResponseDTO): List<Track> {
        val trackListDTO = tracksResponse.trackList // = "data" du JSON
        if (trackListDTO == null)
            return ArrayList()
        return trackListDTO.map { trackDto ->
            val track: TrackDTO = trackDto.alternative ?: trackDto
            Track(
                track.id,
                track.title,
                track.duration,
                track.track_position,
                track.preview
            )
        }
    }
}