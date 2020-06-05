package fr.esgi.deezerplayer.data.model.musicplayer

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log

class BackgroundSoundService() : Service() {

    private var player = Player

    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    constructor(player: Player) : this() {
        Log.d("toto", "constructor service: " + player)
        this.player = player
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("toto","on create service")
     /*   val afd = applicationContext.assets.openFd("backgroundsound1.wav") as AssetFileDescriptor
        val player = MediaPlayer()
        player.setDataSource(afd.fileDescriptor)
        player.isLooping = true // Set looping
        player.setVolume(100f, 100f)*/

    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        player.play()
        Log.d("toto", "start in startCommand func")
        return START_STICKY
    }

    override fun onStart(intent: Intent, startId: Int) {
        // TO DO
    }

    fun onUnBind(arg0: Intent): IBinder? {
        // TO DO Auto-generated method
        return null
    }

    fun onStop() {

    }

    fun onPause() {

    }

    override fun onDestroy() {
        player.release()
    }

    override fun onLowMemory() {

    }
}