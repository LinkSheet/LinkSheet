package fe.linksheet.extension.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

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


/**
 * Yes, I absolutely stole this from Stackoverflow (https://stackoverflow.com/a/68233130) and
 * have _zero_ clue how any of this works, even though my degree says I probably should, lol.
 *
 * Thank you Mr machfour, who ever you are.
 */
fun Modifier.angledGradientBackground(colors: List<Color>, degrees: Float) = this.then(
    drawBehind {
        /*
        Have to compute length of gradient vector so that it lies within
        the visible rectangle.
        --------------------------------------------
        | length of gradient ^  /                  |
        |             --->  /  /                   |
        |                  /  / <- rotation angle  |
        |                 /  o --------------------|  y
        |                /  /                      |
        |               /  /                       |
        |              v  /                        |
        --------------------------------------------
                             x

                   diagonal angle = atan2(y, x)
                 (it's hard to draw the diagonal)

        Simply rotating the diagonal around the centre of the rectangle
        will lead to points outside the rectangle area. Further, just
        truncating the coordinate to be at the nearest edge of the
        rectangle to the rotated point will distort the angle.
        Let α be the desired gradient angle (in radians) and γ be the
        angle of the diagonal of the rectangle.
        The correct for the length of the gradient is given by:
        x/|cos(α)|  if -γ <= α <= γ,   or   π - γ <= α <= π + γ
        y/|sin(α)|  if  γ <= α <= π - γ, or π + γ <= α <= 2π - γ
        where γ ∈ (0, π/2) is the angle that the diagonal makes with
        the base of the rectangle.

        */

        val (x, y) = size
        val gamma = atan2(y, x)

        if (gamma == 0f || gamma == (PI / 2).toFloat()) {
            // degenerate rectangle
            return@drawBehind
        }

        val degreesNormalised = (degrees % 360).let { if (it < 0) it + 360 else it }

        val alpha = (degreesNormalised * PI / 180).toFloat()

        val gradientLength = when (alpha) {
            // ray from centre cuts the right edge of the rectangle
            in 0f..gamma, in (2 * PI - gamma)..2 * PI -> {
                x / cos(alpha)
            }
            // ray from centre cuts the top edge of the rectangle
            in gamma..(PI - gamma).toFloat() -> {
                y / sin(alpha)
            }
            // ray from centre cuts the left edge of the rectangle
            in (PI - gamma)..(PI + gamma) -> {
                x / -cos(alpha)
            }
            // ray from centre cuts the bottom edge of the rectangle
            in (PI + gamma)..(2 * PI - gamma) -> {
                y / -sin(alpha)
            }
            // default case (which shouldn't really happen)
            else -> hypot(x, y)
        }

        val centerOffsetX = cos(alpha) * gradientLength / 2
        val centerOffsetY = sin(alpha) * gradientLength / 2

        drawRect(
            Brush.linearGradient(
                colors = colors,
                start = Offset(center.x - centerOffsetX, center.y - centerOffsetY),
                end = Offset(center.x + centerOffsetX, center.y + centerOffsetY)
            ), size = size
        )
    }
)