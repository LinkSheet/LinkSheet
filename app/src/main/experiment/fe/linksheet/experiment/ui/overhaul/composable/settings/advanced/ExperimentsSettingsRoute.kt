package fe.linksheet.experiment.ui.overhaul.composable.settings.advanced

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fe.android.compose.dialog.helper.dialogHelper
import fe.android.preference.helper.compose.StatePreference
import fe.kotlin.extension.iterable.forEachWithInfo
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.*
import fe.linksheet.extension.compose.clickable
import fe.linksheet.extension.kotlin.collectOnIO
import fe.linksheet.module.viewmodel.ExperimentsViewModel
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDateTime
import kotlin.math.exp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NewExperimentsSettingsRoute(
    navController: NavHostController,
    onBackPressed: () -> Unit,
    viewModel: ExperimentsViewModel = koinViewModel(),
) {
    val listState = rememberLazyListState()


    SettingsScaffold(R.string.experiments,
        onBackPressed = onBackPressed,
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.resetAll() }) {
                Icon(
                    imageVector = Icons.Default.Restore,
                    contentDescription = stringResource(id = R.string.reset)
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
//            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp)
        ) {
//            stickyHeader(key = "header") {
//                Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
//                    PreferenceSubtitle(
//                        text = stringResource(id = R.string.experiments_explainer_2),
//                        paddingHorizontal = 10.dp
//                    )
//                }
//            }

            viewModel.visibleExperiments.forEachWithInfo { experiment, _, _, experimentLast ->
//                stickyHeader {
//                    PreferenceSubtitle(text = "Experiment ${experiment.name}", paddingHorizontal = 10.dp)
//                }
                item {
                    Text(
                        modifier = Modifier.padding(start = 16.dp, bottom = 12.dp),
                        text = "Experiment ${experiment.name}",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                experiment.preferences.forEachWithInfo { pref, index, first, last ->
                    item {
                        val state = viewModel.stateMap[pref.key]!!
                        val shape = if (first && last) SingleShape
                        else if (first) TopShape
                        else if (last) BottomShape
                        else MiddleShape

                        val itemPadding = if (first) PaddingValues(bottom = 1.dp)
                        else if (last) PaddingValues(top = 1.dp)
                        else PaddingValues(vertical = 2.dp)

                        ListItem(
                            modifier = Modifier
                                .clip(shape)
                                .padding(itemPadding)
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
                                )
                                .clickable(onClick = {
                                    state(!state())
                                }
                                ),
                            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
                            headlineContent = {
                                Text(text = pref.key.replace("experiment_", ""))
                            },
                            trailingContent = {
                                Switch(checked = state(), onCheckedChange = { state(it) })
                            }
                        )
//
//                        SwitchRow(
//                            state = viewModel.stateMap[pref.key]!!,
//                            headline = pref.key.replace("experiment_", "")
//                        )
                    }
                }

                if (!experimentLast) {
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

val SingleShape = RoundedCornerShape(24.dp)

val TopShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 4.dp, bottomEnd = 4.dp)
val MiddleShape = RoundedCornerShape(4.dp)
val BottomShape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp, bottomEnd = 24.dp, bottomStart = 24.dp)

