package fr.esgi.deezerplayer.data.dto.mapper

import fr.esgi.deezerplayer.data.dto.TrackResponseDTO
import fr.esgi.deezerplayer.data.model.Track

class TrackResponseMapper {

    fun map(tracksResponse: TrackResponseDTO): List<Track> {
        val trackListDTO = tracksResponse.trackList // = "data" du JSON

        return trackListDTO.map { trackDto ->
            Track(
                trackDto.id,
                trackDto.title,
                trackDto.duration,
                trackDto.track_position,
                trackDto.preview
            )
        }
    }
}