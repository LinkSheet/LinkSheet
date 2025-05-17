package fe.linksheet.activity

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import fe.linksheet.activity.bottomsheet.compat.CompatSheetState
import kotlinx.coroutines.coroutineScope


fun Modifier.interceptTaps(state: CompatSheetState, interceptAccidentalTaps: Boolean): Modifier {
    if (!interceptAccidentalTaps) return this

    return pointerInput(Unit) {
        interceptTap { !state.isAnimationRunning() }
    }
}

// https://stackoverflow.com/a/76168038
private suspend fun PointerInputScope.interceptTap(
    pass: PointerEventPass = PointerEventPass.Initial,
    shouldCancel: (PointerEvent) -> Boolean,
) = coroutineScope {
    awaitEachGesture {
        val down = awaitFirstDown(pass = pass)
        val downTime = System.currentTimeMillis()
        val tapTimeout = viewConfiguration.longPressTimeoutMillis

        do {
            val event = awaitPointerEvent(pass)
            if (shouldCancel(event)) break

            val currentTime = System.currentTimeMillis()

            if (event.changes.size != 1) break // More than one event: not a tap
            if (currentTime - downTime >= tapTimeout) break // Too slow: not a tap

            val change = event.changes[0]

            if (change.id == down.id && !change.pressed) {
                change.consume()
            }
        } while (event.changes.any { it.id == down.id && it.pressed })
    }
}
