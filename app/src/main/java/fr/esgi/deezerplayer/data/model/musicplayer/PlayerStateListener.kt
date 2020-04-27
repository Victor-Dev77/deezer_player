package fr.esgi.deezerplayer.data.model.musicplayer

enum class PlayerState {
    INVALID,
    PLAYING,
    PAUSED,
    RESET,
    FINISH
}

interface PlayerStateListener {

    fun onStateChanged(state: PlayerState) {}

    fun onTrackFinished() {}
}