/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package relocated.androidx.compose.material

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTonalElevationEnabled
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.isContainer
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import relocated.androidx.compose.material3.ExperimentalMaterialApi

/**
 * <a href="https://material.io/design/environment/surfaces.html" class="external"
 * target="_blank">Material Design surface</a>.
 *
 * Material surface is the central metaphor in material design. Each surface exists at a given
 * elevation, which influences how that piece of surface visually relates to other surfaces and how
 * that surface casts shadows.
 *
 * See the other overloads for clickable, selectable, and toggleable surfaces.
 *
 * The Surface is responsible for:
 * 1) Clipping: Surface clips its children to the shape specified by [shape]
 * 2) Elevation: Surface draws a shadow to represent depth, where [elevation] represents the depth
 *    of this surface. If the passed [shape] is concave the shadow will not be drawn on Android
 *    versions less than 10.
 * 3) Borders: If [shape] has a border, then it will also be drawn.
 * 4) Background: Surface fills the shape specified by [shape] with the [color]. If [color] is
 *    [Colors.surface], the [ElevationOverlay] from [LocalElevationOverlay] will be used to apply an
 *    overlay - by default this will only occur in dark theme. The color of the overlay depends on
 *    the [elevation] of this Surface, and the [LocalAbsoluteTonalElevation] set by any parent surfaces.
 *    This ensures that a Surface never appears to have a lower elevation overlay than its
 *    ancestors, by summing the elevation of all previous Surfaces.
 * 5) Content color: Surface uses [contentColor] to specify a preferred color for the content of
 *    this surface - this is used by the [Text] and [Icon] components as a default color.
 * 6) Blocking touch propagation behind the surface.
 *
 * If no [contentColor] is set, this surface will try and match its background color to a color
 * defined in the theme [Colors], and return the corresponding content color. For example, if the
 * [color] of this surface is [Colors.surface], [contentColor] will be set to [Colors.onSurface]. If
 * [color] is not part of the theme palette, [contentColor] will keep the same value set above this
 * Surface.
 *
 * @sample androidx.compose.material.samples.SurfaceSample
 *
 * To modify these default style values used by text, use [ProvideTextStyle] or explicitly pass a
 * new [TextStyle] to your text.
 *
 * To manually retrieve the content color inside a surface, use [LocalContentColor].
 *
 * @param modifier Modifier to be applied to the layout corresponding to the surface
 * @param shape Defines the surface's shape as well its shadow. A shadow is only displayed if the
 *   [elevation] is greater than zero.
 * @param color The background color. Use [Color.Transparent] to have no color.
 * @param contentColor The preferred content color provided by this Surface to its children.
 *   Defaults to either the matching content color for [color], or if [color] is not a color from
 *   the theme, this will keep the same value set above this Surface.
 * @param border Optional border to draw on top of the surface
 * @param elevation The size of the shadow below the surface. Note that It will not affect z index
 *   of the Surface. If you want to change the drawing order you can use `Modifier.zIndex`.
 * @param content The content to be displayed on this Surface
 */
@Composable
fun Surface(
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    color: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(color),
    border: BorderStroke? = null,
    elevation: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    val absoluteElevation = LocalAbsoluteTonalElevation.current + elevation
    CompositionLocalProvider(
        LocalContentColor provides contentColor,
        LocalAbsoluteTonalElevation provides absoluteElevation
    ) {
        Box(
            modifier =
                modifier
                    .surface(
                        shape = shape,
                        backgroundColor =
                            surfaceColorAtElevation(
                                color = color,
                                elevation = absoluteElevation
                            ),
                        border = border,
                        elevation = elevation
                    )
                    .semantics(mergeDescendants = false) {
                        @Suppress("DEPRECATION")
                        isContainer = true
                    }
                    .pointerInput(Unit) {},
            propagateMinConstraints = true
        ) {
            content()
        }
    }
}

/**
 * Material surface is the central metaphor in material design. Each surface exists at a given
 * elevation, which influences how that piece of surface visually relates to other surfaces and how
 * that surface casts shadows.
 *
 * This version of Surface is responsible for a click handling as well al everything else that a
 * regular Surface does:
 *
 * This clickable Surface is responsible for:
 * 1) Clipping: Surface clips its children to the shape specified by [shape]
 * 2) Elevation: Surface draws a shadow to represent depth, where [elevation] represents the depth
 *    of this surface. If the passed [shape] is convex the shadow will not be drawn on Android
 *    versions less than 10.
 * 3) Borders: If [shape] has a border, then it will also be drawn.
 * 4) Background: Surface fills the shape specified by [shape] with the [color]. If [color] is
 *    [Colors.surface], the [ElevationOverlay] from [LocalElevationOverlay] will be used to apply an
 *    overlay - by default this will only occur in dark theme. The color of the overlay depends on
 *    the [elevation] of this Surface, and the [LocalAbsoluteTonalElevation] set by any parent surfaces.
 *    This ensures that a Surface never appears to have a lower elevation overlay than its
 *    ancestors, by summing the elevation of all previous Surfaces.
 * 5) Content color: Surface uses [contentColor] to specify a preferred color for the content of
 *    this surface - this is used by the [Text] and [Icon] components as a default color. If no
 *    [contentColor] is set, this surface will try and match its background color to a color defined
 *    in the theme [Colors], and return the corresponding content color. For example, if the [color]
 *    of this surface is [Colors.surface], [contentColor] will be set to [Colors.onSurface]. If
 *    [color] is not part of the theme palette, [contentColor] will keep the same value set above
 *    this Surface.
 * 6) Click handling. This version of surface will react to the clicks, calling [onClick] lambda,
 *    updating the [interactionSource] when [PressInteraction] occurs, and showing ripple indication
 *    in response to press events. If you don't need click handling, consider using the Surface
 *    function that doesn't require [onClick] param.
 * 7) Semantics for clicks. Just like with [Modifier.clickable], clickable version of Surface will
 *    produce semantics to indicate that it is clicked. No semantic role is set by default, you may
 *    specify one by passing a desired [Role] with a [Modifier.semantics].
 *
 * @sample androidx.compose.material.samples.ClickableSurfaceSample
 *
 * To modify these default style values used by text, use [ProvideTextStyle] or explicitly pass a
 * new [TextStyle] to your text.
 *
 * To manually retrieve the content color inside a surface, use [LocalContentColor].
 *
 * @param onClick callback to be called when the surface is clicked
 * @param modifier Modifier to be applied to the layout corresponding to the surface
 * @param enabled Controls the enabled state of the surface. When `false`, this surface will not be
 *   clickable
 * @param shape Defines the surface's shape as well its shadow. A shadow is only displayed if the
 *   [elevation] is greater than zero.
 * @param color The background color. Use [Color.Transparent] to have no color.
 * @param contentColor The preferred content color provided by this Surface to its children.
 *   Defaults to either the matching content color for [color], or if [color] is not a color from
 *   the theme, this will keep the same value set above this Surface.
 * @param border Optional border to draw on top of the surface
 * @param elevation The size of the shadow below the surface. Note that It will not affect z index
 *   of the Surface. If you want to change the drawing order you can use `Modifier.zIndex`.
 * @param interactionSource an optional hoisted [MutableInteractionSource] for observing and
 *   emitting [Interaction]s for this surface. You can use this to change the surface's appearance
 *   or preview the surface in different states. Note that if `null` is provided, interactions will
 *   still happen internally.
 * @param content The content to be displayed on this Surface
 */
@ExperimentalMaterialApi
@Composable
fun Surface(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RectangleShape,
    color: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(color),
    border: BorderStroke? = null,
    elevation: Dp = 0.dp,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit
) {
    val absoluteElevation = LocalAbsoluteTonalElevation.current + elevation
    CompositionLocalProvider(
        LocalContentColor provides contentColor,
        LocalAbsoluteTonalElevation provides absoluteElevation
    ) {
        Box(
            modifier =
                modifier
                    .minimumInteractiveComponentSize()
                    .surface(
                        shape = shape,
                        backgroundColor =
                            surfaceColorAtElevation(
                                color = color,
                                elevation = absoluteElevation
                            ),
                        border = border,
                        elevation = elevation
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = ripple(),
                        enabled = enabled,
                        onClick = onClick
                    ),
            propagateMinConstraints = true
        ) {
            content()
        }
    }
}

/**
 * Material surface is the central metaphor in material design. Each surface exists at a given
 * elevation, which influences how that piece of surface visually relates to other surfaces and how
 * that surface casts shadows.
 *
 * This version of Surface is responsible for a selection handling as well as everything else that a
 * regular Surface does:
 *
 * This selectable Surface is responsible for:
 * 1) Clipping: Surface clips its children to the shape specified by [shape]
 * 2) Elevation: Surface draws a shadow to represent depth, where [elevation] represents the depth
 *    of this surface. If the passed [shape] is convex the shadow will not be drawn on Android
 *    versions less than 10.
 * 3) Borders: If [shape] has a border, then it will also be drawn.
 * 4) Background: Surface fills the shape specified by [shape] with the [color]. If [color] is
 *    [Colors.surface], the [ElevationOverlay] from [LocalElevationOverlay] will be used to apply an
 *    overlay - by default this will only occur in dark theme. The color of the overlay depends on
 *    the [elevation] of this Surface, and the [LocalAbsoluteTonalElevation] set by any parent surfaces.
 *    This ensures that a Surface never appears to have a lower elevation overlay than its
 *    ancestors, by summing the elevation of all previous Surfaces.
 * 5) Content color: Surface uses [contentColor] to specify a preferred color for the content of
 *    this surface - this is used by the [Text] and [Icon] components as a default color. If no
 *    [contentColor] is set, this surface will try and match its background color to a color defined
 *    in the theme [Colors], and return the corresponding content color. For example, if the [color]
 *    of this surface is [Colors.surface], [contentColor] will be set to [Colors.onSurface]. If
 *    [color] is not part of the theme palette, [contentColor] will keep the same value set above
 *    this Surface.
 * 6) Click handling. This version of surface will react to the clicks, calling [onClick] lambda,
 *    updating the [interactionSource] when [PressInteraction] occurs, and showing ripple indication
 *    in response to press events. If you don't need click handling, consider using the Surface
 *    function that doesn't require [onClick] param.
 * 7) Semantics for selection. Just like with [Modifier.selectable], selectable version of Surface
 *    will produce semantics to indicate that it is selected. No semantic role is set by default,
 *    you may specify one by passing a desired [Role] with a [Modifier.semantics].
 *
 * @sample androidx.compose.material.samples.SelectableSurfaceSample
 *
 * To modify these default style values used by text, use [ProvideTextStyle] or explicitly pass a
 * new [TextStyle] to your text.
 *
 * To manually retrieve the content color inside a surface, use [LocalContentColor].
 *
 * @param selected whether this Surface is selected
 * @param onClick callback to be called when the surface is clicked
 * @param modifier Modifier to be applied to the layout corresponding to the surface
 * @param enabled Controls the enabled state of the surface. When `false`, this surface will not be
 *   selectable
 * @param shape Defines the surface's shape as well its shadow. A shadow is only displayed if the
 *   [elevation] is greater than zero.
 * @param color The background color. Use [Color.Transparent] to have no color.
 * @param contentColor The preferred content color provided by this Surface to its children.
 *   Defaults to either the matching content color for [color], or if [color] is not a color from
 *   the theme, this will keep the same value set above this Surface.
 * @param border Optional border to draw on top of the surface
 * @param elevation The size of the shadow below the surface. Note that It will not affect z index
 *   of the Surface. If you want to change the drawing order you can use `Modifier.zIndex`.
 * @param interactionSource an optional hoisted [MutableInteractionSource] for observing and
 *   emitting [Interaction]s for this surface. You can use this to change the surface's appearance
 *   or preview the surface in different states. Note that if `null` is provided, interactions will
 *   still happen internally.
 * @param content The content to be displayed on this Surface
 */
@ExperimentalMaterialApi
@Composable
fun Surface(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RectangleShape,
    color: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(color),
    border: BorderStroke? = null,
    elevation: Dp = 0.dp,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit
) {
    val absoluteElevation = LocalAbsoluteTonalElevation.current + elevation
    CompositionLocalProvider(
        LocalContentColor provides contentColor,
        LocalAbsoluteTonalElevation provides absoluteElevation
    ) {
        Box(
            modifier =
                modifier
                    .minimumInteractiveComponentSize()
                    .surface(
                        shape = shape,
                        backgroundColor =
                            surfaceColorAtElevation(
                                color = color,
                                elevation = absoluteElevation
                            ),
                        border = border,
                        elevation = elevation
                    )
                    .selectable(
                        selected = selected,
                        interactionSource = interactionSource,
                        indication = ripple(),
                        enabled = enabled,
                        onClick = onClick
                    ),
            propagateMinConstraints = true
        ) {
            content()
        }
    }
}

/**
 * Material surface is the central metaphor in material design. Each surface exists at a given
 * elevation, which influences how that piece of surface visually relates to other surfaces and how
 * that surface casts shadows.
 *
 * This version of Surface is responsible for a toggling its checked state as well as everything
 * else that a regular Surface does:
 *
 * This toggleable Surface is responsible for:
 * 1) Clipping: Surface clips its children to the shape specified by [shape]
 * 2) Elevation: Surface draws a shadow to represent depth, where [elevation] represents the depth
 *    of this surface. If the passed [shape] is convex the shadow will not be drawn on Android
 *    versions less than 10.
 * 3) Borders: If [shape] has a border, then it will also be drawn.
 * 4) Background: Surface fills the shape specified by [shape] with the [color]. If [color] is
 *    [Colors.surface], the [ElevationOverlay] from [LocalElevationOverlay] will be used to apply an
 *    overlay - by default this will only occur in dark theme. The color of the overlay depends on
 *    the [elevation] of this Surface, and the [LocalAbsoluteTonalElevation] set by any parent surfaces.
 *    This ensures that a Surface never appears to have a lower elevation overlay than its
 *    ancestors, by summing the elevation of all previous Surfaces.
 * 5) Content color: Surface uses [contentColor] to specify a preferred color for the content of
 *    this surface - this is used by the [Text] and [Icon] components as a default color. If no
 *    [contentColor] is set, this surface will try and match its background color to a color defined
 *    in the theme [Colors], and return the corresponding content color. For example, if the [color]
 *    of this surface is [Colors.surface], [contentColor] will be set to [Colors.onSurface]. If
 *    [color] is not part of the theme palette, [contentColor] will keep the same value set above
 *    this Surface.
 * 6) Click handling. This version of surface will react to the check toggles, calling
 *    [onCheckedChange] lambda, updating the [interactionSource] when [PressInteraction] occurs, and
 *    showing ripple indication in response to press events. If you don't need check handling,
 *    consider using a Surface function that doesn't require [onCheckedChange] param.
 * 7) Semantics for toggle. Just like with [Modifier.toggleable], toggleable version of Surface will
 *    produce semantics to indicate that it is checked. No semantic role is set by default, you may
 *    specify one by passing a desired [Role] with a [Modifier.semantics].
 *
 * @sample androidx.compose.material.samples.ToggleableSurfaceSample
 *
 * To modify these default style values used by text, use [ProvideTextStyle] or explicitly pass a
 * new [TextStyle] to your text.
 *
 * To manually retrieve the content color inside a surface, use [LocalContentColor].
 *
 * @param checked whether or not this Surface is toggled on or off
 * @param onCheckedChange callback to be invoked when the toggleable Surface is clicked
 * @param modifier Modifier to be applied to the layout corresponding to the surface
 * @param enabled Controls the enabled state of the surface. When `false`, this surface will not be
 *   selectable
 * @param shape Defines the surface's shape as well its shadow. A shadow is only displayed if the
 *   [elevation] is greater than zero.
 * @param color The background color. Use [Color.Transparent] to have no color.
 * @param contentColor The preferred content color provided by this Surface to its children.
 *   Defaults to either the matching content color for [color], or if [color] is not a color from
 *   the theme, this will keep the same value set above this Surface.
 * @param border Optional border to draw on top of the surface
 * @param elevation The size of the shadow below the surface. Note that It will not affect z index
 *   of the Surface. If you want to change the drawing order you can use `Modifier.zIndex`.
 * @param interactionSource an optional hoisted [MutableInteractionSource] for observing and
 *   emitting [Interaction]s for this surface. You can use this to change the surface's appearance
 *   or preview the surface in different states. Note that if `null` is provided, interactions will
 *   still happen internally.
 * @param content The content to be displayed on this Surface
 */
@ExperimentalMaterialApi
@Composable
fun Surface(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = RectangleShape,
    color: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(color),
    border: BorderStroke? = null,
    elevation: Dp = 0.dp,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit
) {
    val absoluteElevation = LocalAbsoluteTonalElevation.current + elevation
    CompositionLocalProvider(
        LocalContentColor provides contentColor,
        LocalAbsoluteTonalElevation provides absoluteElevation
    ) {
        Box(
            modifier =
                modifier
                    .minimumInteractiveComponentSize()
                    .surface(
                        shape = shape,
                        backgroundColor =
                            surfaceColorAtElevation(
                                color = color,
                                elevation = absoluteElevation
                            ),
                        border = border,
                        elevation = elevation
                    )
                    .toggleable(
                        value = checked,
                        interactionSource = interactionSource,
                        indication = ripple(),
                        enabled = enabled,
                        onValueChange = onCheckedChange
                    ),
            propagateMinConstraints = true
        ) {
            content()
        }
    }
}

private fun Modifier.surface(
    shape: Shape,
    backgroundColor: Color,
    border: BorderStroke?,
    elevation: Dp
) =
    this.shadow(elevation, shape, clip = false)
        .then(if (border != null) Modifier.border(border, shape) else Modifier)
        .background(color = backgroundColor, shape = shape)
        .clip(shape)

@Composable
private fun surfaceColorAtElevation(color: Color, elevation: Dp): Color =
    MaterialTheme.colorScheme.applyTonalElevation(color, elevation)

@Composable
@ReadOnlyComposable
internal fun ColorScheme.applyTonalElevation(backgroundColor: Color, elevation: Dp): Color {
    val tonalElevationEnabled = LocalTonalElevationEnabled.current
    return if (backgroundColor == surface && tonalElevationEnabled) {
        surfaceColorAtElevation(elevation)
    } else {
        backgroundColor
    }
}

val LocalAbsoluteTonalElevation = compositionLocalOf { 0.dp }
