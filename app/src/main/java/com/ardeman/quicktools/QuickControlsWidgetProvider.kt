package com.ardeman.quicktools

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class QuickControlsWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_BRIGHTNESS = "com.ardeman.quicktools.ACTION_BRIGHTNESS"
        const val ACTION_VOLUME = "com.ardeman.quicktools.ACTION_VOLUME"
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Update all instances of this widget
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            ACTION_BRIGHTNESS -> {
                // Launch brightness control activity
                val brightnessIntent = Intent(context, BrightnessControlActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
                context.startActivity(brightnessIntent)
            }
            ACTION_VOLUME -> {
                // Launch volume control activity
                val volumeIntent = Intent(context, VolumeControlActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
                }
                context.startActivity(volumeIntent)
            }
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        // Create RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.widget_quick_controls)

        // Set up brightness button click
        val brightnessPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            Intent(context, QuickControlsWidgetProvider::class.java).apply {
                action = ACTION_BRIGHTNESS
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.button_brightness, brightnessPendingIntent)

        // Set up volume button click
        val volumePendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            Intent(context, QuickControlsWidgetProvider::class.java).apply {
                action = ACTION_VOLUME
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.button_volume, volumePendingIntent)

        // Update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}