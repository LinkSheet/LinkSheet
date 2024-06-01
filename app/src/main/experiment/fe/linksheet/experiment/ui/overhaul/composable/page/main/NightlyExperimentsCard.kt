package fe.linksheet.experiment.ui.overhaul.composable.page.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Science
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import fe.linksheet.R
import fe.linksheet.experiment.ui.overhaul.composable.component.card.ClickableAlertCard2
import fe.linksheet.experiment.ui.overhaul.composable.util.Resource.Companion.textContent
import fe.linksheet.experimentSettingsRoute

@Composable
fun NightlyExperimentsCard(navigate: (String) -> Unit) {
    ClickableAlertCard2(
        onClick = { navigate(experimentSettingsRoute.route) },
        imageVector = Icons.Outlined.Science,
        contentDescription = stringResource(id = R.string.nightly_experiments_card),
        headline = textContent(R.string.nightly_experiments_card),
        subtitle = textContent(R.string.nightly_experiments_card_explainer)
    )
}


@Preview
@Composable
fun NightlyExperimentsCardPreview(){
    NightlyExperimentsCard(navigate = {})
}
