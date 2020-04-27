package fr.esgi.deezerplayer.data.model.musicplayer

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log


object Player : PlayerAdapter, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener,
    MediaPlayer.OnErrorListener {

    private val player = MediaPlayer().apply {
        setAudioAttributes(
            AudioAttributes
                .Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
        // this ref _player car dans apply -> @Player pour def this = objet et pas _player
        setOnErrorListener(this@Player)
        setOnPreparedListener(this@Player)
        setOnCompletionListener(this@Player)
    }

    private var stateListener: PlayerStateListener? = null

    fun setPlayerStateListener(listener: PlayerStateListener) {
        stateListener = listener
    }


    /*
        ####    LISTENER    ######
     */
    override fun onPrepared(mp: MediaPlayer?) {
        Log.d("toto", "MediaPlayer prepared")
        //TODO: click track afficher loader. une fois ici envoyer callback pour enlever loader et lancer play dans la vue
        player.start()
        stateListener?.onStateChanged(PlayerState.PLAYING)
    }

    override fun onCompletion(mp: MediaPlayer?) {
        Log.d("toto", "MediaPlayer completed")
        player.reset()
        stateListener?.onStateChanged(PlayerState.FINISH)
        stateListener?.onTrackFinished()
    }

    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        Log.d("toto", "ERROR MediaPlayer: what -> " + what + " / extra: " + extra)
        if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED)
            mp.reset()
        else if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN)
            mp.reset()
        return true
    }



    /*
        ####    CONTROLS    ######
     */
    override fun loadTrack(url: String) {
        /* try {
             player.setDataSource(url)
         } catch (e: Exception) {
             Log.d("toto", "ERROR Load: setdatasource -> " + e.stackTrace)
         }

         try {
             player.prepareAsync()
         } catch (e: Exception) {
             Log.d("toto", "ERROR Load: prepareAsync -> " + e.stackTrace)
         }*/
        if (player.isPlaying) {
            player.reset()
        }
        player.setDataSource(url)
        player.prepareAsync()
    }

    override fun play() {
        if (!player.isPlaying) {
            player.start()
            stateListener?.onStateChanged(PlayerState.PLAYING)
        }
    }

    override fun pause() {
        if (player.isPlaying) {
            player.pause()
            stateListener?.onStateChanged(PlayerState.PAUSED)
        }
    }

    override fun isPlaying() = player.isPlaying

    override fun reset() {
        player.reset()
        stateListener?.onStateChanged(PlayerState.RESET)
    }

    override fun release() {
        player.release()
    }

    override fun seekTo(position: Int) {
        player.seekTo(position)
    }

}
