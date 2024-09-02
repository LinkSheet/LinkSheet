package fe.linksheet.composable.page.main

import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.card.AlertCard
import fe.linksheet.module.viewmodel.MainViewModel


@Composable
fun BrowserCard(browserStatus: MainViewModel.BrowserStatus) {
    AlertCard(
        colors = CardDefaults.cardColors(containerColor = browserStatus.containerColor()),
        icon = browserStatus.icon.iconPainter,
        iconContentDescription = stringResource(id = browserStatus.iconDescription),
        headline = textContent(browserStatus.headline),
        subtitle = textContent(browserStatus.subtitle)
    )
}
