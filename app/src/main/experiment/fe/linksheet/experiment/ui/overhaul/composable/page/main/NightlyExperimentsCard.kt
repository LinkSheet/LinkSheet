package fe.linksheet.experiment.ui.overhaul.composable.page.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import fe.linksheet.R
import fe.linksheet.experiment.ui.overhaul.composable.ContentTypeDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.AlertListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.ClickableAlertListItem
import fe.linksheet.experimentSettingsRoute
import fe.linksheet.ui.PreviewTheme

@Composable
fun NightlyExperimentsCard(navigate: (String) -> Unit) {
    ClickableAlertListItem(
        onClick = { navigate(experimentSettingsRoute.route) },
        imageVector = Icons.Filled.NewReleases,
        contentDescriptionTextId = R.string.nightly_experiments_card,
        headlineContentTextId = R.string.nightly_experiments_card,
        supportingContentTextId = R.string.nightly_experiments_card_explainer
    )
}

@Composable
@Preview
fun NightlyExperimentsCardPreview() {
    PreviewTheme {
        NightlyExperimentsCard {

        }
    }
}
