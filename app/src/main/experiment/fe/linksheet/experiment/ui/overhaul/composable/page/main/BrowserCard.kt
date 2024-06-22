package fe.linksheet.experiment.ui.overhaul.composable.page.main

import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import fe.linksheet.component.card.AlertCard
import fe.linksheet.module.viewmodel.MainViewModel


@Composable
fun BrowserCard(browserStatus: MainViewModel.BrowserStatus) {
    AlertCard(
        colors = CardDefaults.cardColors(containerColor = browserStatus.containerColor()),
        imageVector = browserStatus.icon,
        contentDescriptionId = browserStatus.iconDescription,
        headlineId = browserStatus.headline,
        subtitleId = browserStatus.subtitle
    )
}
