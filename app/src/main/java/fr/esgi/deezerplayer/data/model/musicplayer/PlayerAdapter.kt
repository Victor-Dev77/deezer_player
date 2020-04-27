package fr.esgi.deezerplayer.data.model.musicplayer

interface PlayerAdapter {

    fun loadTrack(url: String)

    fun release()

    fun isPlaying(): Boolean

    fun play()

    fun reset()

    fun pause()

    fun seekTo(position: Int)
}