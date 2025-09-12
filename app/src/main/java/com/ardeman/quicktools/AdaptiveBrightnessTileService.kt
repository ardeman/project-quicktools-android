package com.ardeman.quicktools

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class AdaptiveBrightnessTileService: TileService() {

    override fun onStartListening() {
        super.onStartListening()
        updateTileState()
    }

    override fun onClick() {
        super.onClick()

        // Check if we have permission to modify settings
        if (!Settings.System.canWrite(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)

            // Use PendingIntent for Android 14+ (API 34+), regular Intent for older versions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                val pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                startActivityAndCollapse(pendingIntent)
            } else {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                @Suppress("DEPRECATION")
                @SuppressLint("StartActivityAndCollapseDeprecated")
                startActivityAndCollapse(intent)
            }
            return
        }

        // Toggle adaptive brightness
        val currentState = isAdaptiveBrightnessEnabled()
        setAdaptiveBrightness(!currentState)
        updateTileState()
    }

    private fun updateTileState() {
        val tile = qsTile ?: return

        val isEnabled = isAdaptiveBrightnessEnabled()

        tile.state = if (isEnabled) {
            Tile.STATE_ACTIVE
        } else {
            Tile.STATE_INACTIVE
        }

        tile.label = "Adaptive brightness"
        tile.contentDescription = if (isEnabled) {
            "Adaptive brightness is on"
        } else {
            "Adaptive brightness is off"
        }

        // Use Material Design icon
        tile.icon = Icon.createWithResource(
            this,
            R.drawable.rounded_brightness_auto_24
        )

        tile.updateTile()
    }

    private fun isAdaptiveBrightnessEnabled(): Boolean {
        return try {
            val mode = Settings.System.getInt(
                contentResolver,
                Settings.System.SCREEN_BRIGHTNESS_MODE
            )
            mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
        } catch (_: Settings.SettingNotFoundException) {
            false
        }
    }

    private fun setAdaptiveBrightness(enable: Boolean) {
        Settings.System.putInt(
            contentResolver,
            Settings.System.SCREEN_BRIGHTNESS_MODE,
            if (enable) {
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
            } else {
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
            }
        )
    }

}