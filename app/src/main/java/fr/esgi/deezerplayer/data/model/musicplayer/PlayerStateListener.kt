package fr.esgi.deezerplayer.data.model.musicplayer

enum class PlayerState {
    INIT,
    PLAYING,
    PAUSED,
    RESET,
    FINISH,
    RELEASED
}

interface PlayerStateListener {

    fun onStateChanged(state: PlayerState) {}

    fun onTrackFinished() {}
}