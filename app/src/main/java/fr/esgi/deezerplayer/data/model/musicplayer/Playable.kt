package fr.esgi.deezerplayer.data.model.musicplayer

import fr.esgi.deezerplayer.data.model.Track

interface Playable {
    fun onTrackPrevious(): Track?
    fun onTrackPlay()
    fun onTrackPause()
    fun onTrackNext(): Track?
}