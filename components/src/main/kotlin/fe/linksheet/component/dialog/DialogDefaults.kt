package fe.linksheet.component.dialog

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.linksheet.component.list.base.CustomListItemDefaults
import fe.linksheet.component.list.base.CustomListItemTextOptions
import fe.linksheet.component.list.base.ShapeListItemDefaults
import fe.linksheet.component.util.TextOptions

object DialogDefaults {
    val RadioButtonWidth = 48.dp
    val ContentPadding = 6.dp

    val ListItemInnerPadding = CustomListItemDefaults.padding(
        start = 0.dp,
        leadingContentEnd = 4.dp
    )

    val ListItemTextOptions: CustomListItemTextOptions
        @Composable
        get() = CustomListItemDefaults.textOptions(
            headline = TextOptions(style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp))
        )

    val ListItemColors: ListItemColors
        @Composable
        get() = ShapeListItemDefaults.colors(
            headlineColor = AlertDialogDefaults.textContentColor,
            supportingColor = AlertDialogDefaults.textContentColor,
            containerColor = AlertDialogDefaults.containerColor
        )

    val DialogPadding = PaddingValues(all = 24.dp)
    val IconPadding = PaddingValues(bottom = 16.dp)
    val TitlePadding = PaddingValues(bottom = 16.dp)
    val TextPadding = PaddingValues(bottom = 24.dp)
}
