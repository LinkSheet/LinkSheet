package fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import fe.android.preference.helper.Preference
import fe.android.preference.helper.compose.MutablePreferenceState
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ClickableShapeListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ContentPosition
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ShapeListItemDefaults
import fe.linksheet.experiment.ui.overhaul.composable.util.OptionalContent
import fe.linksheet.experiment.ui.overhaul.composable.util.TextContent

object RadioButtonListItemDefaults {
    val Width = 24.dp
}

@Composable
fun <P : Preference<T, NT>, T : Any, NT> PreferenceRadioButtonListItem(
    enabled: Boolean = true,
    value: NT,
    preference: MutablePreferenceState<T, NT, P>,
    shape: Shape = ShapeListItemDefaults.SingleShape,
    padding: PaddingValues = ShapeListItemDefaults.EmptyPadding,
    position: ContentPosition,
    headlineContent: TextContent,
    overlineContent: TextContent? = null,
    supportingContent: TextContent? = null,
    otherContent: OptionalContent = null,
) {
    RadioButtonListItem(
        enabled = enabled,
        shape = shape,
        padding = padding,
        position = position,
        selected = preference() == value,
        onSelect = { preference(value) },
        overlineContent = overlineContent,
        headlineContent = headlineContent,
        supportingContent = supportingContent,
        otherContent = otherContent
    )
}

@Composable
fun RadioButtonListItem(
    enabled: Boolean = true,
    width: Dp = RadioButtonListItemDefaults.Width,
    selected: Boolean,
    onSelect: () -> Unit,
    shape: Shape = ShapeListItemDefaults.SingleShape,
    padding: PaddingValues = ShapeListItemDefaults.EmptyPadding,
    position: ContentPosition,
    headlineContent: TextContent,
    overlineContent: TextContent? = null,
    supportingContent: TextContent? = null,
    otherContent: OptionalContent,
) {
    ClickableShapeListItem(
        enabled = enabled,
        onClick = onSelect,
        role = Role.RadioButton,
        shape = shape,
        padding = padding,
        headlineContent = headlineContent,
        overlineContent = overlineContent,
        supportingContent = supportingContent,
        position = position,
        primaryContent = {
            DefaultListItemRadioButton(enabled = enabled, width = width, selected = selected, onSelect = onSelect)
        },
        otherContent = otherContent
    )
}

@Composable
private fun DefaultListItemRadioButton(
    enabled: Boolean = true,
    width: Dp = RadioButtonListItemDefaults.Width,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides width) {
        RadioButton(
            modifier = ShapeListItemDefaults.BaseContentModifier.width(width),
            enabled = enabled,
            selected = selected,
            onClick = onSelect,
        )
    }
}
