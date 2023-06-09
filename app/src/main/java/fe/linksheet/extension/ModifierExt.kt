package fe.linksheet.extension

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha

typealias ChangeModifier = (Modifier) -> Modifier

inline fun Modifier.runIf(condition: Boolean, run: ChangeModifier) =
    if (condition) this.let(run) else this

inline fun Modifier.runIf(
    condition: Boolean,
    runIf: ChangeModifier,
    runElse: ChangeModifier
) = if (condition) this.let(runIf) else this.let(runElse)

fun Modifier.nullClickable(): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    clickable(interactionSource = interactionSource, indication = null, onClick = {})
}

fun Modifier.clickable(
    onClick: (() -> Unit)? = null
) = this.runIf(onClick != null) { it.clickable(onClick = onClick!!) }

fun Modifier.enabled(enabled: Boolean) = runIf(!enabled) { it.alpha(0.3f) }