package fr.esgi.deezerplayer.data.model.musicplayer

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


object Player : PlayerAdapter, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener,
    MediaPlayer.OnErrorListener {

    private const val PLAYBACK_POSITION_REFRESH_INTERVAL_MS = 1000 // update UI chaque sec (seekbar)
    private var mExecutor: ScheduledExecutorService? = null
    private var mSeekBarPositionUpdateTask: Runnable? = null

    private var player = initMediaPlayer()

    private var stateListener: PlayerStateListener? = null

    fun setPlayerStateListener(listener: PlayerStateListener) {
        stateListener = listener
    }

    private lateinit var currentState: PlayerState
    val currentPosition get() = player.currentPosition


    /*
        ####    LISTENER    ######
     */
    override fun onPrepared(mp: MediaPlayer?) {
        Log.d("toto", "MediaPlayer prepared")
        //TODO: click track afficher loader. une fois ici envoyer callback pour enlever loader et lancer play dans la vue
        initializeProgressCallback()
        play()
    }

    override fun onCompletion(mp: MediaPlayer?) {
        Log.d("toto", "MediaPlayer completed")
        seekTo(0)
        stopUpdatingCallbackWithPosition(true)
        currentState = PlayerState.FINISH
        stateListener?.onStateChanged(PlayerState.FINISH)
        stateListener?.onTrackFinished()
    }

    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        Log.d("toto", "ERROR MediaPlayer: what -> " + what + " / extra: " + extra)
        if (what == MediaPlayer.MEDIA_ERROR_SERVER_DIED)
            mp.reset()
        else if (what == MediaPlayer.MEDIA_ERROR_UNKNOWN)
            mp.reset()
        currentState = PlayerState.RESET
        return true
    }



    /*
        ####    CONTROLS    ######
     */
    private fun initMediaPlayer() = MediaPlayer().apply {
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
        currentState = PlayerState.INIT
    }

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
        if (currentState == PlayerState.RELEASED) {
            player = initMediaPlayer()
        }
        player.reset()
        player.setDataSource(url)
        player.prepareAsync()
    }

    override fun play() {
        if (!player.isPlaying) {
            player.start()
            currentState = PlayerState.PLAYING
            stateListener?.onStateChanged(PlayerState.PLAYING)
            startUpdatingCallbackWithPosition()
        }
    }

    override fun pause() {
        if (player.isPlaying) {
            player.pause()
            currentState = PlayerState.PAUSED
            stateListener?.onStateChanged(PlayerState.PAUSED)
        }
    }

    override fun isPlaying() = player.isPlaying

    override fun reset() {
        player.reset()
        stopUpdatingCallbackWithPosition(true)
        currentState = PlayerState.RESET
        stateListener?.onStateChanged(PlayerState.RESET)
    }

    override fun release() {
        player.release()
        currentState = PlayerState.RELEASED
    }

    override fun seekTo(position: Int) {
        player.seekTo(position)
    }


    /*
        ####    SYNCHRO PLAYER POSITION WITH PROGRESS CALLBACK    ######
     */
    // TODO: refaire ces 2 func avec Coroutine si possible
    private fun startUpdatingCallbackWithPosition() {
        if (mExecutor == null) {
            mExecutor = Executors.newSingleThreadScheduledExecutor()
        }
        if (mSeekBarPositionUpdateTask == null) {
            mSeekBarPositionUpdateTask = Runnable { updateProgressCallbackTask() }
        }
        mExecutor!!.scheduleAtFixedRate(
            mSeekBarPositionUpdateTask!!,
            0,
            PLAYBACK_POSITION_REFRESH_INTERVAL_MS.toLong(),
            TimeUnit.MILLISECONDS
        )
    }

    // Signal position lecture au callback
    private fun stopUpdatingCallbackWithPosition(resetUIPlaybackPosition: Boolean) {
        if (mExecutor != null) {
            mExecutor!!.shutdownNow()
            mExecutor = null
            mSeekBarPositionUpdateTask = null
            if (resetUIPlaybackPosition && stateListener != null) {
                stateListener!!.onPositionChanged(0)
            }
        }
    }

    private fun updateProgressCallbackTask() {
        if (isPlaying()) {
            val currentPosition: Int = player.currentPosition
            stateListener?.onPositionChanged(currentPosition)
        }
    }

    override fun initializeProgressCallback() {
        val duration: Int = player.duration
        stateListener?.onDurationChanged(duration)
        stateListener?.onPositionChanged(0)
    }

}
