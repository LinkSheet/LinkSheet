package fe.linksheet.experiment.ui.overhaul.composable.page.settings.advanced

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import fe.linksheet.R
import fe.linksheet.experiment.ui.overhaul.composable.ContentTypeDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ShapeListItemDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.card.AlertCard
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.SwitchListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.module.viewmodel.ExperimentsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun NewExperimentsSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: ExperimentsViewModel = koinViewModel(),
) {
    SaneScaffoldSettingsPage(
        headline = stringResource(id = R.string.experiments),
        onBackPressed = onBackPressed,
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.resetAll() }) {
                Icon(
                    imageVector = Icons.Default.Restore,
                    contentDescription = stringResource(id = R.string.reset)
                )
            }
        }
    ) {
        item(key = R.string.warning, contentType = ContentTypeDefaults.Alert) {
            AlertCard(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                imageVector = Icons.Default.Warning,
                contentDescriptionId = R.string.warning,
                headlineId = R.string.experiments_explainer_2,
                subtitleId = R.string.experiments_explainer_3
            )
        }

        for (experiment in viewModel.visibleExperiments) {
            divider(key = experiment.name, text = "Experiment ${experiment.name}")

            group(experiment.preferences.size) {
                for (pref in experiment.preferences) {
                    val state = viewModel.stateMap[pref.key]!!
                    val title = pref.key.replace("experiment_", "")

                    item(key = pref.key) { padding, shape ->
                        SwitchListItem(
                            shape = shape,
                            padding = padding,
                            checked = state(),
                            onCheckedChange = { state(it) },
                            headlineContent = {
                                Text(text = title, overflow = TextOverflow.Ellipsis, maxLines = 1)
                            }
                        )
                    }
                }
            }
        }
    }
}

