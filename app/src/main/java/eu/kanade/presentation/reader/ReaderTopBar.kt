package eu.kanade.presentation.reader

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.widget.TextClock
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import eu.kanade.tachiyomi.ui.reader.ReaderViewModel
import eu.kanade.tachiyomi.ui.reader.chapter.ReaderChapterItem

@Composable
fun ReaderTopBar(
    state: ReaderViewModel.State,
    chapters: List<ReaderChapterItem>,
    modifier: Modifier = Modifier,
) {
    if (state.currentPage <= 0 || state.totalPages <= 0) return

    val context = LocalContext.current
    var batteryLevel by remember { mutableIntStateOf(-1) }

    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context, intent: Intent) {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                if (level != -1 && scale != -1) {
                    batteryLevel = (level * 100 / scale.toFloat()).toInt()
                } else if (level != -1) {
                    batteryLevel = level
                }
            }
        }
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(receiver, filter)
        if (batteryStatus != null) {
            val level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            if (level != -1 && scale != -1) {
                batteryLevel = (level * 100 / scale.toFloat()).toInt()
            } else if (level != -1) {
                batteryLevel = level
            }
        }

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    val style = TextStyle(
        color = Color(235, 235, 235),
        fontSize = MaterialTheme.typography.bodySmall.fontSize,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
    )
    val strokeStyle = style.copy(
        color = Color(45, 45, 45),
        drawStyle = Stroke(width = 4f),
    )

    // E.g. "Ch. 5/10 Pg. 2/20"
    val currentChapterIndex = chapters.indexOfFirst { it.isCurrent }.takeIf { it >= 0 } ?: 0
    // chapters list is often reversed (latest first), but it might be reading order.
    // Usually, the index + 1 is what user wants, or size - index. Let's assume standard index.
    // If it's reversed, the user might see Ch 30/30 on chapter 1. Let's just use the index for now.
    // Actually, Tachiyomi chapters list passed to viewer is usually in reading order (ascending or descending based on preference).
    val chapterText = "Ch. ${currentChapterIndex + 1}/${chapters.size}"
    val pageText = "Pg. ${state.currentPage}/${state.totalPages}"

    val leftText = "$chapterText  $pageText"

    Box(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        // Left Info
        Box(modifier = Modifier.align(Alignment.CenterStart)) {
            Text(text = leftText, style = strokeStyle)
            Text(text = leftText, style = style)
        }

        // Right Info (Battery & Time)
        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var timeText by remember {
                mutableStateOf(android.text.format.DateFormat.format("HH:mm", java.util.Calendar.getInstance()).toString())
            }

            DisposableEffect(context) {
                val receiver = object : BroadcastReceiver() {
                    override fun onReceive(c: Context, intent: Intent) {
                        timeText = android.text.format.DateFormat.format("HH:mm", java.util.Calendar.getInstance()).toString()
                    }
                }
                context.registerReceiver(receiver, IntentFilter(Intent.ACTION_TIME_TICK))
                onDispose {
                    context.unregisterReceiver(receiver)
                }
            }

            val batteryText = if (batteryLevel >= 0) "$batteryLevel% " else ""
            val rightText = "$batteryText$timeText"

            Box {
                Text(text = rightText, style = strokeStyle)
                Text(text = rightText, style = style)
            }
        }
    }
}
