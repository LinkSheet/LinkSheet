package fe.linksheet.experiment.ui.overhaul.composable.page.main

import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ShapeListItemDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.AlertListItem
import fe.linksheet.module.viewmodel.MainViewModel


@Composable
fun BrowserCard(browserStatus: MainViewModel.BrowserStatus) {
    AlertListItem(
        colors = ShapeListItemDefaults.colors(containerColor = browserStatus.containerColor()),
        imageVector = browserStatus.icon,
        contentDescriptionTextId = browserStatus.iconDescription,
        headlineContentTextId = browserStatus.headline,
        supportingContentTextId = browserStatus.subtitle
    )
}
