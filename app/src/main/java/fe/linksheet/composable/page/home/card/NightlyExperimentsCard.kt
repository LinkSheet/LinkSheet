package fe.linksheet.composable.page.home.card

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Science
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.card.AlertCard
import fe.linksheet.R
import fe.linksheet.navigation.ExperimentRoute
import fe.composekit.route.Route

@Composable
fun NightlyExperimentsCard(navigate: (Route) -> Unit) {
    AlertCard(
        onClick = { navigate(ExperimentRoute.Empty) },
        icon = Icons.Outlined.Science.iconPainter,
        iconContentDescription = stringResource(id = R.string.nightly_experiments_card),
        headline = textContent(R.string.nightly_experiments_card),
        subtitle = textContent(R.string.nightly_experiments_card_explainer)
    )
}


@Preview
@Composable
private fun NightlyExperimentsCardPreview() {
    NightlyExperimentsCard(navigate = {})
}
