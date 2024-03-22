package ui_overhaul.fe.linksheet.composable.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import fe.linksheet.R
import fe.linksheet.experimentSettingsRoute
import fe.linksheet.ui.PreviewTheme

@Composable
fun NightlyExperimentsCard(navigate: (String) -> Unit) {
    MainCard(onClick = { navigate(experimentSettingsRoute.route) }) {
        MainCardContent(
            icon = Icons.Filled.NewReleases,
            iconDescription = R.string.nightly_experiments_card,
            title = R.string.nightly_experiments_card,
            content = R.string.nightly_experiments_card_explainer
        )
    }
}

@Composable
@Preview
fun NightlyExperimentsCardPreview() {
    PreviewTheme {
        NightlyExperimentsCard {

        }
    }
}
