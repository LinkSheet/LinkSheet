package fe.androidx.compose.material3

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import fe.androidx.compose.material3.tokens.ShapeKeyTokens

internal object ShapeDefaults {
    /** A non-rounded corner size */
    internal val CornerNone: CornerSize = CornerSize(0.dp)
}

internal fun CornerBasedShape.top(
    bottomSize: CornerSize = ShapeDefaults.CornerNone,
): CornerBasedShape {
    return copy(bottomStart = bottomSize, bottomEnd = bottomSize)
}

/**
 * Helper function for component shape tokens. Used to grab the bottom values of a shape parameter.
 */
internal fun CornerBasedShape.bottom(
    topSize: CornerSize = ShapeDefaults.CornerNone,
): CornerBasedShape {
    return copy(topStart = topSize, topEnd = topSize)
}

/**
 * Helper function for component shape tokens. Used to grab the start values of a shape parameter.
 */
internal fun CornerBasedShape.start(
    endSize: CornerSize = ShapeDefaults.CornerNone,
): CornerBasedShape {
    return copy(topEnd = endSize, bottomEnd = endSize)
}

/** Helper function for component shape tokens. Used to grab the end values of a shape parameter. */
internal fun CornerBasedShape.end(
    startSize: CornerSize = ShapeDefaults.CornerNone,
): CornerBasedShape {
    return copy(topStart = startSize, bottomStart = startSize)
}

internal fun Shapes.fromToken(value: ShapeKeyTokens): Shape {
    return when (value) {
        ShapeKeyTokens.CornerExtraLarge -> extraLarge
        ShapeKeyTokens.CornerExtraLargeTop -> extraLarge.top()
        ShapeKeyTokens.CornerExtraSmall -> extraSmall
        ShapeKeyTokens.CornerExtraSmallTop -> extraSmall.top()
        ShapeKeyTokens.CornerFull -> CircleShape
        ShapeKeyTokens.CornerLarge -> large
        ShapeKeyTokens.CornerLargeEnd -> large.end()
        ShapeKeyTokens.CornerLargeTop -> large.top()
        ShapeKeyTokens.CornerMedium -> medium
        ShapeKeyTokens.CornerNone -> RectangleShape
        ShapeKeyTokens.CornerSmall -> small
    }
}

/**
 * Converts a shape token key to the local shape provided by the theme The color is subscribed to
 * [LocalShapes] changes
 */
internal val ShapeKeyTokens.value: Shape
    @Composable @ReadOnlyComposable get() = MaterialTheme.shapes.fromToken(this)
