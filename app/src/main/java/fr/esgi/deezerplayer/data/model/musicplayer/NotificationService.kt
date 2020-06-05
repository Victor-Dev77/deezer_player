package fr.esgi.deezerplayer.data.model.musicplayer

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import com.squareup.picasso.Picasso
import fr.esgi.deezerplayer.R
import fr.esgi.deezerplayer.data.model.Album
import fr.esgi.deezerplayer.data.model.Track
import fr.esgi.deezerplayer.view.MainActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async


class NotificationService : Service() {

    private var album: Album? = null
    private var track: Track? = null
    private val player = Player

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val extras = intent.extras
        album = extras?.get("album") as Album?
        track = extras?.get("track") as Track?

        if (intent.action == Constants.ACTION.STARTFOREGROUND_ACTION) {
            showNotification()
            Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show()
        } else if (intent.action == Constants.ACTION.PREV_ACTION) {
            player.previous()
            Toast.makeText(this, "Clicked Previous", Toast.LENGTH_SHORT).show()
            Log.i("toto", "Clicked Previous")
        } else if (intent.action == Constants.ACTION.PLAY_ACTION) {
            Toast.makeText(this, "Clicked Play", Toast.LENGTH_SHORT).show()
            Log.i("toto", "Clicked Play")
        } else if (intent.action == Constants.ACTION.NEXT_ACTION) {
            player.next()
            Toast.makeText(this, "Clicked Next", Toast.LENGTH_SHORT).show()
            Log.i("toto", "Clicked Next")
        } else if (intent.action ==
            Constants.ACTION.STOPFOREGROUND_ACTION
        ) {
            Log.i("toto", "Received Stop Foreground Intent")
            Toast.makeText(this, "Service Stoped", Toast.LENGTH_SHORT).show()
            player.release()
            stopForeground(true)
            stopSelf()
        }
        return START_STICKY
    }

    lateinit var status: Notification
    lateinit var notificationManager : NotificationManager
    lateinit var notificationChannel : NotificationChannel
    lateinit var builder : Notification.Builder
    private val channelId = "fr.esgi.deezerplayer"
    private val description = "Ceci est une description"
    private fun showNotification() = GlobalScope.async { // Using RemoteViews to bind custom layouts into Notification
        val views = RemoteViews(
            packageName,
            R.layout.status_bar_notification
        )
        val bigViews = RemoteViews(
            packageName,
            R.layout.status_bar_expanded_notification
        )

        // Load Image
        val bmp: Bitmap = Picasso.get().load(album?.cover).get()
        views.setImageViewBitmap(R.id.status_bar_album_art, bmp)

        bigViews.setImageViewBitmap(
            R.id.status_bar_album_art,
            bmp
        )


        val notificationIntent = Intent(this@NotificationService, MainActivity::class.java)
        notificationIntent.action = Constants.ACTION.MAIN_ACTION
        notificationIntent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK
                or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        val pendingIntent = PendingIntent.getActivity(
            this@NotificationService, 0,
            notificationIntent, 0
        )
        val previousIntent = Intent(this@NotificationService, NotificationService::class.java)
        previousIntent.action = Constants.ACTION.PREV_ACTION
        val ppreviousIntent = PendingIntent.getService(
            this@NotificationService, 0,
            previousIntent, 0
        )
        val playIntent = Intent(this@NotificationService, NotificationService::class.java)
        playIntent.action = Constants.ACTION.PLAY_ACTION
        val pplayIntent = PendingIntent.getService(
            this@NotificationService, 0,
            playIntent, 0
        )
        val nextIntent = Intent(this@NotificationService, NotificationService::class.java)
        nextIntent.action = Constants.ACTION.NEXT_ACTION
        val pnextIntent = PendingIntent.getService(
            this@NotificationService, 0,
            nextIntent, 0
        )
        val closeIntent = Intent(this@NotificationService, NotificationService::class.java)
        closeIntent.action = Constants.ACTION.STOPFOREGROUND_ACTION
        val pcloseIntent = PendingIntent.getService(
            this@NotificationService, 0,
            closeIntent, 0
        )
        views.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent)
        bigViews.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent)
        views.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent)
        bigViews.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent)
        views.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent)
        bigViews.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent)
        views.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent)
        bigViews.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent)
       /* views.setImageViewResource(
            R.id.status_bar_play,
            R.drawable.ic_launcher_foreground
        )
        bigViews.setImageViewResource(
            R.id.status_bar_play,
            R.drawable.ic_launcher_foreground
        )*/
        views.setTextViewText(R.id.status_bar_track_name, track?.title)
        bigViews.setTextViewText(R.id.status_bar_track_name, track?.title)
        views.setTextViewText(R.id.status_bar_artist_name, album?.artist?.name)
        bigViews.setTextViewText(R.id.status_bar_artist_name, album?.artist?.name)
        bigViews.setTextViewText(R.id.status_bar_album_name, album?.title)


        notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(
                channelId,description,NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(this@NotificationService,channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContent(views)
                .setCustomBigContentView(bigViews)
                .setLargeIcon(BitmapFactory.decodeResource(this@NotificationService.resources,
                    R.drawable.ic_launcher_background))
                .setContentIntent(pendingIntent)
        }else{

            builder = Notification.Builder(this@NotificationService)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContent(views)
                .setCustomBigContentView(bigViews)
                .setLargeIcon(BitmapFactory.decodeResource(this@NotificationService.resources,
                    R.mipmap.ic_launcher))
                .setContentIntent(pendingIntent)
        }
        notificationManager.notify(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, builder.build())

      /*  status = Notification.Builder(this)
            .setContent(views)
            .setCustomBigContentView(bigViews)
            .setContentTitle("Parking Meter")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            //.setOngoing(true)
            .build()
        NotificationManagerCompat.from(this).notify(1, status)*/
        /*status = Builder(this).build()
        status.contentView = views
        status.bigContentView = bigViews
        status.flags = Notification.FLAG_ONGOING_EVENT
        status.icon = R.drawable.ic_launcher_foreground
        status.contentIntent = pendingIntent*/
        startForeground(
            Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
            builder.build()
        )
    }

}

object Constants {

    interface ACTION {
        companion object {
            const val MAIN_ACTION = "fr.esgi.deezerplayer.action.main"
            const val INIT_ACTION = "fr.esgi.deezerplayer.action.init"
            const val PREV_ACTION = "fr.esgi.deezerplayer.action.prev"
            const val PLAY_ACTION = "fr.esgi.deezerplayer.action.play"
            const val NEXT_ACTION = "fr.esgi.deezerplayer.action.next"
            const val STARTFOREGROUND_ACTION =
                "fr.esgi.deezerplayer.action.startforeground"
            const val STOPFOREGROUND_ACTION =
                "fr.esgi.deezerplayer.action.stopforeground"
        }
    }

    interface NOTIFICATION_ID {
        companion object {
            const val FOREGROUND_SERVICE = 101
        }
    }
}