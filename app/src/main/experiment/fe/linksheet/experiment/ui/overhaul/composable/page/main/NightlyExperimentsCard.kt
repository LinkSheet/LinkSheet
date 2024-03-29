package fe.linksheet.experiment.ui.overhaul.composable.page.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.runtime.Composable
import fe.linksheet.R
import fe.linksheet.experiment.ui.overhaul.composable.component.card.ClickableAlertCard
import fe.linksheet.experimentSettingsRoute

@Composable
fun NightlyExperimentsCard(navigate: (String) -> Unit) {
    ClickableAlertCard(
        onClick = { navigate(experimentSettingsRoute.route) },
        imageVector = Icons.Filled.NewReleases,
        contentDescriptionId = R.string.nightly_experiments_card,
        headlineId = R.string.nightly_experiments_card,
        subtitleId = R.string.nightly_experiments_card_explainer
    )
}
