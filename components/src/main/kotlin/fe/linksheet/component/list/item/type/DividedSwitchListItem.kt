package fe.linksheet.component.list.item.type

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fe.linksheet.component.list.base.ClickableShapeListItem
import fe.linksheet.component.list.base.ContentPosition
import fe.linksheet.component.list.base.ShapeListItemDefaults
import fe.linksheet.component.util.Default.Companion.text
import fe.linksheet.component.util.OptionalContent
import fe.linksheet.component.util.TextContent
import fe.linksheet.component.util.rememberOptionalContent


@Composable
fun DividedSwitchListItem(
    enabled: Boolean = true,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onContentClick: () -> Unit,
    shape: Shape = ShapeListItemDefaults.SingleShape,
    padding: PaddingValues = ShapeListItemDefaults.EmptyPadding,
    position: ContentPosition,
    headlineContent: TextContent,
    overlineContent: TextContent? = null,
    supportingContent: TextContent? = null,
    otherContent: OptionalContent = null,
) {
    ClickableShapeListItem(
        enabled = enabled,
        onClick = onContentClick,
        role = Role.Button,
        shape = shape,
        padding = padding,
        position = position,
        headlineContent = headlineContent,
        overlineContent = overlineContent,
        supportingContent = supportingContent,
        primaryContent = {
            DefaultDividedListItemSwitch(
                enabled = enabled,
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        },
        otherContent = otherContent
    )
}

@Composable
private fun DefaultDividedListItemSwitch(
    enabled: Boolean = true,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val thumbContent = rememberOptionalContent(checked) {
        Icon(
            modifier = Modifier.size(SwitchDefaults.IconSize),
            imageVector = Icons.Filled.Check,
            contentDescription = null,
        )
    }

    Box(modifier = ShapeListItemDefaults.BaseContentModifier) {
        VerticalDivider(
            modifier = Modifier
                .height(32.dp)
                .align(Alignment.Center)
        )
    }

    Switch(
        modifier = ShapeListItemDefaults.BaseContentModifier.padding(start = 12.dp),
        enabled = enabled,
        checked = checked,
        thumbContent = thumbContent,
        onCheckedChange = onCheckedChange
    )
}


@Preview
@Composable
fun DividedClickableShapeListItemPreview() {
    var checked by remember { mutableStateOf(true) }
    DividedSwitchListItem(
        checked = checked,
        onContentClick = {

        },
        onCheckedChange = { checked = !checked },
        position = ContentPosition.Trailing,
        headlineContent = text("Preview"),
        supportingContent = text(
            "Subtitle 123\ntest\n" +
                    "test\n" +
                    "test\n" +
                    "test\n" +
                    "test"
        ),
    )
}
