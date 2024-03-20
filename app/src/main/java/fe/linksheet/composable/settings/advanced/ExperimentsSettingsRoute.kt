package fe.linksheet.composable.settings.advanced

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fe.android.compose.dialog.helper.dialogHelper
import fe.android.preference.helper.compose.StatePreference
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.*
import fe.linksheet.extension.kotlin.collectOnIO
import fe.linksheet.module.viewmodel.ExperimentsViewModel
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDateTime
import kotlin.math.exp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExperimentsSettingsRoute(
    navController: NavHostController,
    onBackPressed: () -> Unit,
    viewModel: ExperimentsViewModel = koinViewModel(),
) {
    val listState = rememberLazyListState()

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
            listState.animateScrollToItem(pref)
            enableExperimentDialog.open(experiment!!)
        }
    }


    SettingsScaffold(R.string.experiments, onBackPressed = onBackPressed, floatingActionButton = {
        FloatingActionButton(onClick = { viewModel.resetAll() }) {
            Icon(imageVector = Icons.Default.Restore, contentDescription = stringResource(id = R.string.reset))
        }
    }) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            state = listState,
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

            viewModel.visibleExperiments.forEach { experiment ->
                stickyHeader {
                    PreferenceSubtitle(text = "Experiment ${experiment.name}", paddingHorizontal = 10.dp)
                }

                experiment.preferences.forEach { pref ->
                    item {
                        SwitchRow(state = viewModel.stateMap[pref.key]!!, headline = pref.key)
                    }
                }
            }
        }
    }
}
