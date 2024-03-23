package fe.linksheet.experiment.ui.overhaul.composable.page.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import fe.linksheet.R
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ShapeListItemDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.AlertListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.ClickableAlertListItem
import fe.linksheet.experimentSettingsRoute

@Composable
fun UnofficialBuild() {
    AlertListItem(
        colors = ShapeListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.errorContainer),
        imageVector = Icons.Default.Warning,
        contentDescriptionTextId = R.string.warning,
        headlineContentTextId = R.string.running_unofficial_build,
        supportingContentTextId = R.string.built_by_error
    )
}
