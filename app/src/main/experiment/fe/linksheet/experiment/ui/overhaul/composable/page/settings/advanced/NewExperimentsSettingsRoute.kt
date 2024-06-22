package fe.linksheet.experiment.ui.overhaul.composable.page.settings.advanced

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import fe.android.compose.dialog.helper.dialogHelper
import fe.linksheet.R
import fe.linksheet.composable.settings.advanced.ExperimentDialog
import fe.linksheet.component.ContentTypeDefaults
import fe.linksheet.component.card.ClickableAlertCard2
import fe.linksheet.component.list.base.ContentPosition
import fe.linksheet.component.list.item.type.SwitchListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.component.util.ComposableTextContent.Companion.content
import fe.linksheet.component.util.Resource.Companion.textContent
import fe.linksheet.extension.kotlin.collectOnIO
import fe.linksheet.module.viewmodel.ExperimentsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun NewExperimentsSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: ExperimentsViewModel = koinViewModel(),
) {
    val experiment by viewModel.experiment.collectOnIO()
    val enableExperimentDialog = dialogHelper<String, String, Unit>(
        fetch = { it },
        awaitFetchBeforeOpen = true,
        dynamicHeight = true
    ) { state, close ->

        ExperimentDialog(state!!)
    }

    LaunchedEffect(experiment) {
        val pref = viewModel.stateMap.keys.indexOf(experiment)
        if (pref != -1) {
//            listState.animateScrollToItem(pref)
            enableExperimentDialog.open(experiment!!)
        }
    }

    SaneScaffoldSettingsPage(
        headline = stringResource(id = R.string.experiments),
        onBackPressed = onBackPressed,
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(paddingValues = WindowInsets.navigationBars.asPaddingValues()),
                onClick = { viewModel.resetAll() }
            ) {
                Icon(
                    imageVector = Icons.Default.Restore,
                    contentDescription = stringResource(id = R.string.reset)
                )
            }
        }
    ) {
        item(key = R.string.warning, contentType = ContentTypeDefaults.Alert) {
            ClickableAlertCard2(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                imageVector = Icons.Default.Warning,
                contentDescription = stringResource(id = R.string.warning),
                headline = textContent(R.string.experiments_explainer_2),
                subtitle = textContent(R.string.experiments_explainer_3)
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
                            position = ContentPosition.Trailing,
                            headlineContent = content {
                                Text(text = title, overflow = TextOverflow.Ellipsis, maxLines = 1)
                            }
                        )
                    }
                }
            }
        }
    }
}

