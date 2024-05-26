package fe.linksheet.experiment.ui.overhaul.composable.component.dialog

import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.linksheet.experiment.ui.overhaul.composable.component.list.base.CustomListItemDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.list.base.CustomListItemTextOptions
import fe.linksheet.experiment.ui.overhaul.composable.component.list.base.ShapeListItemDefaults
import fe.linksheet.experiment.ui.overhaul.composable.util.TextOptions

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
}
