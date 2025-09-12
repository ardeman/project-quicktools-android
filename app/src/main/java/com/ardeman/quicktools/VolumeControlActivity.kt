package com.ardeman.quicktools

import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ardeman.quicktools.ui.theme.QuickToolsTheme
import kotlin.math.roundToInt

class VolumeControlActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make this activity appear as a dialog/popup
        window.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            attributes.dimAmount = 0.6f
        }

        setContent {
            QuickToolsTheme {
                VolumeControlPopup(
                    onDismiss = { finish() }
                )
            }
        }
    }
}

@Composable
fun VolumeControlPopup(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val audioManager = remember {
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    var mediaVolume by remember {
        mutableStateOf(getCurrentVolume(audioManager, AudioManager.STREAM_MUSIC))
    }
    var ringVolume by remember {
        mutableStateOf(getCurrentVolume(audioManager, AudioManager.STREAM_RING))
    }
    var alarmVolume by remember {
        mutableStateOf(getCurrentVolume(audioManager, AudioManager.STREAM_ALARM))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.01f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { /* Prevent card clicks from dismissing */ },
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.rounded_volume_up_24),
                    contentDescription = "Volume",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Volume Controls",
                    style = MaterialTheme.typography.headlineSmall
                )

                // Media Volume
                VolumeSlider(
                    label = "Media",
                    icon = R.drawable.rounded_music_note_24,
                    value = mediaVolume,
                    onValueChange = { newValue ->
                        mediaVolume = newValue
                        setVolume(audioManager, AudioManager.STREAM_MUSIC, newValue)
                    }
                )

                // Ring Volume
                VolumeSlider(
                    label = "Ring",
                    icon = R.drawable.rounded_ring_volume_24,
                    value = ringVolume,
                    onValueChange = { newValue ->
                        ringVolume = newValue
                        setVolume(audioManager, AudioManager.STREAM_RING, newValue)
                    }
                )

                // Alarm Volume
                VolumeSlider(
                    label = "Alarm",
                    icon = R.drawable.rounded_alarm_24,
                    value = alarmVolume,
                    onValueChange = { newValue ->
                        alarmVolume = newValue
                        setVolume(audioManager, AudioManager.STREAM_ALARM, newValue)
                    }
                )

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Done")
                }
            }
        }
    }
}

@Composable
fun VolumeSlider(
    label: String,
    icon: Int,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = label,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = "${(value * 100).roundToInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.rounded_volume_mute_24),
                contentDescription = "Mute",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )

            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = 0f..1f,
                modifier = Modifier.weight(1f)
            )

            Icon(
                painter = painterResource(id = R.drawable.rounded_volume_up_24),
                contentDescription = "Max volume",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

private fun getCurrentVolume(audioManager: AudioManager, streamType: Int): Float {
    val current = audioManager.getStreamVolume(streamType)
    val max = audioManager.getStreamMaxVolume(streamType)
    return if (max > 0) current.toFloat() / max else 0f
}

private fun setVolume(audioManager: AudioManager, streamType: Int, value: Float) {
    val max = audioManager.getStreamMaxVolume(streamType)
    val volume = (value * max).roundToInt()
    audioManager.setStreamVolume(streamType, volume, AudioManager.FLAG_SHOW_UI)
}