package com.ardeman.quicktools

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
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

class BrightnessControlActivity : ComponentActivity() {

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
                BrightnessControlPopup(
                    onDismiss = { finish() }
                )
            }
        }
    }
}

@Composable
fun BrightnessControlPopup(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var brightnessValue by remember {
        mutableStateOf(getCurrentBrightness(context))
    }
    var canWriteSettings by remember {
        mutableStateOf(Settings.System.canWrite(context))
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.rounded_brightness_6_24),
                    contentDescription = "Brightness",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Brightness",
                    style = MaterialTheme.typography.headlineSmall
                )

                if (!canWriteSettings) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Permission Required",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )

                            Button(
                                onClick = {
                                    context.startActivity(
                                        Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                                    )
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text("Grant Permission")
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.rounded_brightness_5_24),
                            contentDescription = "Low brightness",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Slider(
                            value = brightnessValue,
                            onValueChange = { newValue ->
                                brightnessValue = newValue
                                setBrightness(context, newValue)
                            },
                            valueRange = 0f..1f,
                            modifier = Modifier.weight(1f)
                        )

                        Icon(
                            painter = painterResource(id = R.drawable.rounded_brightness_7_24),
                            contentDescription = "High brightness",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Text(
                        text = "${(brightnessValue * 100).roundToInt()}%",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

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

private fun getCurrentBrightness(context: Context): Float {
    return try {
        val brightness = Settings.System.getInt(
            context.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS
        )
        brightness / 255f
    } catch (e: Settings.SettingNotFoundException) {
        0.5f
    }
}

private fun setBrightness(context: Context, value: Float) {
    if (Settings.System.canWrite(context)) {
        Settings.System.putInt(
            context.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS_MODE,
            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
        )
        Settings.System.putInt(
            context.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS,
            (value * 255).roundToInt()
        )
    }
}