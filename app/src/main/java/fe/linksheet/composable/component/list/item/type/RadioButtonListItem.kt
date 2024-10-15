package fe.linksheet.composable.component.list.item.type

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ListItemColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import fe.android.compose.content.OptionalContent
import fe.android.compose.text.TextContent
import fe.android.preference.helper.Preference
import fe.android.preference.helper.compose.MutablePreferenceState
import fe.composekit.component.CommonDefaults
import fe.composekit.component.list.column.CustomListItemContainerHeight
import fe.composekit.component.list.column.CustomListItemDefaults
import fe.composekit.component.list.column.CustomListItemPadding
import fe.composekit.component.list.column.CustomListItemTextOptions
import fe.composekit.component.list.column.shape.ShapeListItemDefaults
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.component.list.item.EnabledContent
import fe.composekit.component.list.item.EnabledContentSet
import fe.composekit.component.list.item.type.RadioButtonListItem
import fe.composekit.component.shape.CustomShapeDefaults

@Composable
fun <P : Preference<T, NT>, T : Any, NT> PreferenceRadioButtonListItem(
    enabled: EnabledContentSet = EnabledContent.all,
    value: NT,
    preference: MutablePreferenceState<T, NT, P>,
    shape: Shape = CustomShapeDefaults.SingleShape,
    padding: PaddingValues = CommonDefaults.EmptyPadding,
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
