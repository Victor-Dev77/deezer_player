package fr.esgi.deezerplayer.view


import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
import fr.esgi.deezerplayer.R
import fr.esgi.deezerplayer.data.model.musicplayer.ConstantActionBtnPlayer
import fr.esgi.deezerplayer.data.model.musicplayer.Player
import fr.esgi.deezerplayer.services.NotificationActionService

class AppWidget : AppWidgetProvider() {

    /**
     * Update a single app widget.  This is a helper method for the standard
     * onUpdate() callback that handles one widget update at a time.
     *
     * @param context          The application context.
     * @param appWidgetManager The app widget manager.
     * @param appWidgetId      The current app widget id.
     */
    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {

        // Construct the RemoteViews object.
        val views = RemoteViews(context.packageName, R.layout.widget_demo)
        views.setTextViewText(R.id.appwidget_update_title_track, Player.getCurrentTrack()?.title ?: context.resources.getString(R.string.widget_title_track))
        views.setTextViewText(
            R.id.appwidget_update_artist_name, Player.getAlbum()?.artist?.name ?: context.resources.getString(
                R.string.widget_artist_name
            )
        )
        // Jouer sur la visibility car ne peut pas changer drawable d'une imageButton ici
        val isPlaying = Player.isPlaying()
        if (isPlaying) {
            views.setViewVisibility(R.id.widget_btn_play, View.GONE)
            views.setViewVisibility(R.id.widget_btn_pause, View.VISIBLE)
        } else {
            views.setViewVisibility(R.id.widget_btn_play, View.VISIBLE)
            views.setViewVisibility(R.id.widget_btn_pause, View.GONE)
        }


        // Previous Btn
        setPlayerAction(
            context,
            ConstantActionBtnPlayer.ACTION_PREVIOUS,
            views,
            R.id.widget_btn_previous,
            appWidgetId
        )

        // Play Btn
        setPlayerAction(
            context,
            ConstantActionBtnPlayer.ACTION_PLAY,
            views,
            R.id.widget_btn_play,
            appWidgetId
        )

        // Pause Btn
        setPlayerAction(
            context,
            ConstantActionBtnPlayer.ACTION_PLAY,
            views,
            R.id.widget_btn_pause,
            appWidgetId
        )

        // Next Btn
        setPlayerAction(
            context,
            ConstantActionBtnPlayer.ACTION_NEXT,
            views,
            R.id.widget_btn_next,
            appWidgetId
        )

        // Instruct the widget manager to update the widget.
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun setPlayerAction(context: Context, action: String, views: RemoteViews, btnView: Int, appWidgetId: Int) {
        val intentAction = Intent(context, NotificationActionService::class.java)
        intentAction.action = action //AppWidgetManager.ACTION_APPWIDGET_UPDATE
        // Include the widget ID to be updated as an intent extra.
        val idArray = intArrayOf(appWidgetId)
        intentAction.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray)
        val pendingAction = PendingIntent.getBroadcast(
            context,
            appWidgetId, intentAction, PendingIntent.FLAG_UPDATE_CURRENT
        )
        views.setOnClickPendingIntent(btnView, pendingAction)
    }


    /**
     * Override for onUpdate() method, to handle all widget update requests.
     *
     * @param context          The application context.
     * @param appWidgetManager The app widget manager.
     * @param appWidgetIds     An array of the app widget IDs.
     */
    override fun onUpdate(
        context: Context, appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them.
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
}