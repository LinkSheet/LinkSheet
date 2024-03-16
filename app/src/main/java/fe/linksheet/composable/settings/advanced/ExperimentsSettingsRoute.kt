package fe.linksheet.composable.settings.advanced

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.PreferenceSubtitle
import fe.linksheet.composable.util.SwitchRow
import fe.linksheet.module.preference.experiment.Experiments
import fe.linksheet.module.viewmodel.ExperimentsViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExperimentsSettingsRoute(
    navController: NavHostController,
    onBackPressed: () -> Unit,
    viewModel: ExperimentsViewModel = koinViewModel(),
) {
    SettingsScaffold(R.string.experiments, onBackPressed = onBackPressed, floatingActionButton = {
        FloatingActionButton(onClick = { viewModel.resetAll() }) {
            Icon(imageVector = Icons.Default.Restore, contentDescription = stringResource(id = R.string.reset))
        }
    }) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            stickyHeader(key = "header") {
                Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                    PreferenceSubtitle(
                        text = stringResource(id = R.string.experiments_explainer_2),
                        paddingHorizontal = 10.dp
                    )
                }
            }

            Experiments.experiments.forEach { experiment ->
                stickyHeader {
                    PreferenceSubtitle(text = "Experiment ${experiment.name}", paddingHorizontal = 10.dp)
                }

                experiment.preferences.forEach { pref ->
                    item {
                        SwitchRow(state = viewModel.states[pref.key]!!, headline = pref.key)
                    }
                }
            }
        }
    }
}
