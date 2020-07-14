package fr.esgi.deezerplayer.view

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent

class AppWidgetHandle {
    companion object {

        fun updateAppWidget(context: Context) {
            // Setup update button to send an update request as a pending intent.
            val intentUpdate = Intent(context, AppWidget::class.java)

            // The intent action must be an app widget update.
            intentUpdate.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE

            val ids = AppWidgetManager.getInstance(context)
                    .getAppWidgetIds(
                        ComponentName(
                            context,
                            AppWidget::class.java
                        )
                    )
            intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            context.sendBroadcast(intentUpdate)
        }

    }
}