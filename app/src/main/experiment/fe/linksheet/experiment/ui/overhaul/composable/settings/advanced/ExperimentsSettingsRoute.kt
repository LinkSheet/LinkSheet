package fe.linksheet.experiment.ui.overhaul.composable.settings.advanced

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fe.kotlin.extension.iterable.forEachWithInfo
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.experiment.ui.overhaul.ui.GoogleSansText
import fe.linksheet.extension.compose.clickable
import fe.linksheet.module.viewmodel.ExperimentsViewModel
import fe.linksheet.ui.HkGroteskFontFamily
import org.koin.androidx.compose.koinViewModel

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
            item(key = "header", contentType = "header") {
                ListItem(
                    modifier = Modifier.clip(SingleShape),
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        leadingIconColor = contentColorFor(MaterialTheme.colorScheme.primaryContainer),
                        headlineColor = contentColorFor(MaterialTheme.colorScheme.primaryContainer),
                        supportingColor = contentColorFor(MaterialTheme.colorScheme.primaryContainer)
                    ),
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = stringResource(id = R.string.warning),
                        )
                    },
                    headlineContent = {
                        Text(
                            text = stringResource(id = R.string.experiments_explainer_2),
                            fontFamily = HkGroteskFontFamily,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    supportingContent = {
                        Text(text = stringResource(id = R.string.experiments_explainer_3))
                    }
                )
            }

            viewModel.visibleExperiments.forEachWithInfo { experiment, _, _, experimentLast ->
                item(key = experiment.name, contentType = "exp-title") {
                    Text(
                        modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp),
                        text = "Experiment ${experiment.name}",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                experiment.preferences.forEachWithInfo { pref, index, first, last ->
                    item(key = pref.key, contentType = "pref-$first-$last") {
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
                                .clickable(onClick = { state(!state()) }),
                            colors = ListItemDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                headlineColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainerHigh),
                                supportingColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainerHigh)
                            ),
                            headlineContent = {
                                Text(
                                    text = pref.key.replace("experiment_", ""),
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1
                                )
                            },
                            trailingContent = {
                                Switch(checked = state(), onCheckedChange = { state(it) })
                            }
                        )
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

