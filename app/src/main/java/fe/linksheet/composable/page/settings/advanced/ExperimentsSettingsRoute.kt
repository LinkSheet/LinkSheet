package fe.linksheet.composable.page.settings.advanced

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fe.android.compose.dialog.helper.dialogHelper
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.ComposableTextContent.Companion.content
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.ContentType
import fe.composekit.component.card.AlertCard
import fe.composekit.component.icon.IconOffset
import fe.linksheet.R
import app.linksheet.compose.list.item.PreferenceSwitchListItem
import app.linksheet.compose.page.SaneScaffoldSettingsPage
import fe.linksheet.module.viewmodel.ExperimentsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ExperimentsSettingsRoute(
    onBackPressed: () -> Unit,
    experiment: String?,
    viewModel: ExperimentsViewModel = koinViewModel(),
) {
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
        item(key = R.string.warning, contentType = ContentType.Alert) {
            ExperimentAlertCard()
        }

        for (group in viewModel.visibleExperiments) {
            divider(key = group.name, text = group.displayName)

            group(group.preferences.size) {
                for (experimentPreference in group.preferences) {
                    val key = experimentPreference.preference.key
                    val preference = viewModel.stateMap[key]!!

                    item(key = key) { padding, shape ->
                        PreferenceSwitchListItem(
                            shape = shape,
                            padding = padding,
                            statePreference = preference,
                            headlineContent = content {
                                Text(text = experimentPreference.displayName, overflow = TextOverflow.Ellipsis)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExperimentAlertCard() {
    AlertCard(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        icon = Icons.Rounded.WarningAmber.iconPainter,
        iconOffset = IconOffset(y = (-1).dp),
        iconContentDescription = stringResource(id = R.string.warning),
        headline = textContent(R.string.experiments_explainer_2),
        subtitle = textContent(R.string.experiments_explainer_3)
    )
}

@Preview
@Composable
private fun ExperimentAlertCardPreview() {
    ExperimentAlertCard()
}

