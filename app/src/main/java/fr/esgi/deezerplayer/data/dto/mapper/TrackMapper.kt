package fr.esgi.deezerplayer.data.dto.mapper

import fr.esgi.deezerplayer.data.dto.TrackDTO
import fr.esgi.deezerplayer.data.dto.TrackResponseDTO
import fr.esgi.deezerplayer.data.model.Track

class TrackResponseMapper {

    fun map(tracksResponse: TrackResponseDTO): List<Track> {
        val trackListDTO = tracksResponse.trackList
        if (trackListDTO == null) // Si lien tracklist a renvoyé exception ou pas de données, quelques albums dans ce cas
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