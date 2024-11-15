package fe.androidx.compose.material3.internal

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline

/**
 * Replacement for Modifier.background which takes color lazily to avoid recomposition while
 * animating.
 */
internal fun Modifier.textFieldBackground(
    color: ColorProducer,
    shape: Shape,
): Modifier =
    this.drawWithCache {
        val outline = shape.createOutline(size, layoutDirection, this)
        onDrawBehind { drawOutline(outline, color = color()) }
    }
