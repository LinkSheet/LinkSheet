package fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import fe.android.preference.helper.Preference
import fe.android.preference.helper.compose.MutablePreferenceState
import fe.linksheet.component.list.base.*
import fe.linksheet.component.list.item.type.RadioButtonListItem
import fe.linksheet.component.util.OptionalContent
import fe.linksheet.component.util.TextContent

@Composable
fun <P : Preference<T, NT>, T : Any, NT> PreferenceRadioButtonListItem(
    enabled: Boolean = true,
    value: NT,
    preference: MutablePreferenceState<T, NT, P>,
    shape: Shape = ShapeListItemDefaults.SingleShape,
    padding: PaddingValues = ShapeListItemDefaults.EmptyPadding,
    colors: ListItemColors = ShapeListItemDefaults.colors(),
    containerHeight: CustomListItemContainerHeight = CustomListItemDefaults.containerHeight(),
    innerPadding: CustomListItemPadding = CustomListItemDefaults.padding(),
    textOptions: CustomListItemTextOptions = CustomListItemDefaults.textOptions(),
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
        colors = colors,
        position = position,
        selected = preference() == value,
        onSelect = { preference(value) },
        overlineContent = overlineContent,
        headlineContent = headlineContent,
        supportingContent = supportingContent,
        otherContent = otherContent,
        containerHeight = containerHeight,
        innerPadding = innerPadding,
        textOptions = textOptions
    )
}
