package fr.esgi.deezerplayer.data.model.musicplayer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.squareup.picasso.Picasso
import fr.esgi.deezerplayer.R
import fr.esgi.deezerplayer.data.model.Album
import fr.esgi.deezerplayer.data.model.Track
import fr.esgi.deezerplayer.services.NotificationActionService
import fr.esgi.deezerplayer.services.OnClearFromRecentService
import fr.esgi.deezerplayer.util.Coroutines


class CreateNotification {
    companion object {
        private const val CHANNEL_ID = "channel1"
        private const val ACTION_PREVIOUS = ConstantActionBtnPlayer.ACTION_PREVIOUS
        private const val ACTION_PLAY = ConstantActionBtnPlayer.ACTION_PLAY
        private const val ACTION_NEXT = ConstantActionBtnPlayer.ACTION_NEXT
        private const val ACTION_CLOSE = ConstantActionBtnPlayer.ACTION_CLOSE
        private lateinit var notification: Notification
        private lateinit var notificationManager: NotificationManager

        private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.extras?.getString("actionname")) {
                    ACTION_PREVIOUS -> Player.onTrackPrevious()
                    ACTION_PLAY -> {
                        if (Player.isPlaying())
                            Player.onTrackPause()
                        else
                            Player.onTrackPlay()
                    }
                    ACTION_NEXT -> Player.onTrackNext()
                    ACTION_CLOSE -> {
                        Player.pause()
                        Player.reset()
                        notificationManager.cancelAll()
                    }
                }
            }
        }

        fun registerReceiver(context: Context) {
            context.registerReceiver(broadcastReceiver, IntentFilter("TRACKS_TRACKS"))
            context.startService(Intent(context, OnClearFromRecentService::class.java))
        }

        fun unregisterReceiver(context: Context) {
            context.unregisterReceiver(broadcastReceiver)
        }

        fun createNotification(
            context: Context,
            album: Album?,
            track: Track?,
            playButton: Int,
            position: Int,
            size: Int
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d("toto", "create notif")
                val notificationManagerCompat = NotificationManagerCompat.from(context)
                val mediaSessionCompat = MediaSessionCompat(context, "tag")

                val pendingIntentPrevious: PendingIntent?
                val drawPrevious: Int
                if (position == 0) {
                    pendingIntentPrevious = null
                    drawPrevious = 0
                } else {
                    val intentPrevious = Intent(context, NotificationActionService::class.java)
                        .setAction(ACTION_PREVIOUS)
                    pendingIntentPrevious = PendingIntent.getBroadcast(
                        context,
                        0,
                        intentPrevious,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    drawPrevious = R.drawable.ic_rewind
                }

                val intentPlay = Intent(context, NotificationActionService::class.java)
                    .setAction(ACTION_PLAY)
                val pendingIntentPlay = PendingIntent.getBroadcast(
                    context,
                    0,
                    intentPlay,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                val pendingIntentNext: PendingIntent?
                val drawNext: Int
                if (position == size) {
                    pendingIntentNext = null
                    drawNext = 0
                } else {
                    val intentNext = Intent(context, NotificationActionService::class.java)
                        .setAction(ACTION_NEXT)
                    pendingIntentNext = PendingIntent.getBroadcast(
                        context,
                        0,
                        intentNext,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    drawNext = R.drawable.ic_forward
                }

                val pendingIntentClose: PendingIntent?
                val intentClose = Intent(context, NotificationActionService::class.java)
                    .setAction(ACTION_CLOSE)
                pendingIntentClose = PendingIntent.getBroadcast(
                    context,
                    0,
                    intentClose,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
                val drawClose: Int = R.drawable.ic_close

                var bmp: Bitmap
                Coroutines.ioThenMain(
                    { Picasso.get().load(album?.cover).get() },
                    {
                        bmp = it ?: BitmapFactory.decodeResource(context.resources, R.drawable.ic_music_note)

                        notification = NotificationCompat.Builder(context, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_music_note)
                            .setContentTitle(track?.title)
                            .setContentText(album?.artist?.name)
                            .setLargeIcon(bmp)
                            .setOnlyAlertOnce(true)
                            .setShowWhen(false)
                            .setOngoing(true)
                            .addAction(drawPrevious, "Previous", pendingIntentPrevious)
                            .addAction(playButton, "Play", pendingIntentPlay)
                            .addAction(drawNext, "Next", pendingIntentNext)
                            .addAction(drawClose, "Close", pendingIntentClose)
                            .setStyle(androidx.media.app.NotificationCompat.MediaStyle()
                                .setShowActionsInCompactView(0, 1, 2)
                                .setMediaSession(mediaSessionCompat.sessionToken)
                            )

                            .setPriority(NotificationCompat.PRIORITY_LOW)
                            .build()

                        notificationManagerCompat.notify(1, notification)

                    } // callback
                )

            }
        }

        fun createChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d("toto", "create channel")
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_LOW
                )
                notificationManager = context.getSystemService(NotificationManager::class.java)
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(channel)
                }
            }
        }


        fun destroy() {
            notificationManager.cancelAll()
        }

    }
}

