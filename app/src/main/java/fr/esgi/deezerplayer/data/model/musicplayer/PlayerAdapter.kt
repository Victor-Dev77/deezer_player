package fr.esgi.deezerplayer.data.model.musicplayer

import fr.esgi.deezerplayer.data.model.Track

interface PlayerAdapter {

    fun loadTrack(url: String)

    fun release()

    fun isPlaying(): Boolean

    fun play()

    fun reset()

    fun pause()

    fun initializeProgressCallback()

    fun seekTo(position: Int)

    fun next(): Track?

    fun previous(): Track?
}