package fe.linksheet.composable.settings.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.experiment.ui.overhaul.composable.settings.advanced.BottomShape
import fe.linksheet.experiment.ui.overhaul.composable.settings.advanced.MiddleShape
import fe.linksheet.experiment.ui.overhaul.composable.settings.advanced.SingleShape
import fe.linksheet.experiment.ui.overhaul.composable.settings.advanced.TopShape
import fe.linksheet.extension.compose.enabled
import fe.linksheet.module.viewmodel.ThemeSettingsViewModel
import fe.linksheet.ui.Theme
import fe.linksheet.util.AndroidVersion
import org.koin.androidx.compose.koinViewModel


@Composable
fun ThemeSettingsRoute(navController: NavController, viewModel: ThemeSettingsViewModel = koinViewModel()) {
    SettingsScaffold(R.string.theme, onBackPressed = { navController.popBackStack() }) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp)
        ) {
            if (AndroidVersion.AT_LEAST_API_31_S) {
                item(key = R.string.theme_enable_material_you, contentType = "switch") {
                    ListItem(
                        modifier = Modifier
                            .clip(SingleShape)
                            .clickable(onClick = { viewModel.themeMaterialYou(!viewModel.themeMaterialYou()) })
                            .height(IntrinsicSize.Min),
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            headlineColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainerHigh),
                            supportingColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainerHigh)
                        ),
                        headlineContent = {
                            Text(text = stringResource(id = R.string.theme_enable_material_you))
                        },
                        supportingContent = {
                            Text(text = stringResource(id = R.string.theme_enable_material_you_explainer))
                        },
                        trailingContent = {
                            Switch(
                                modifier = Modifier.fillMaxHeight(),
                                checked = viewModel.themeMaterialYou(),
                                onCheckedChange = { viewModel.themeMaterialYou(it) }
                            )
                        }
                    )
                }
            }

            item(key = "mode", contentType = "mode") {
                Text(
                    modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp),
                    text = stringResource(id = R.string.theme_mode),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleSmall
                )
            }

            item(key = R.string.light, contentType = "checkbox") {
                ListItem(
                    modifier = Modifier
                        .clip(TopShape)
                        .padding(bottom = 1.dp)
                        .clickable(onClick = { viewModel.theme(Theme.Light) }),
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        headlineColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainerHigh),
                        supportingColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainerHigh)
                    ),
                    headlineContent = {
                        Text(text = stringResource(id = R.string.light))
                    },
                    trailingContent = {
                        RadioButton(
                            selected = viewModel.theme() == Theme.Light,
                            onClick = { viewModel.theme(Theme.Light) },
                            modifier = Modifier
                        )
                    }
                )
            }

            item(key = R.string.dark, contentType = "checkbox") {
                ListItem(
                    modifier = Modifier
                        .clip(MiddleShape)
                        .padding(vertical = 1.dp)
                        .clickable(onClick = { viewModel.theme(Theme.Dark) }),
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        headlineColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainerHigh),
                        supportingColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainerHigh)
                    ),
                    headlineContent = {
                        Text(text = stringResource(id = R.string.dark))
                    },
                    trailingContent = {
                        RadioButton(
                            selected = viewModel.theme() == Theme.Dark,
                            onClick = { viewModel.theme(Theme.Dark) },
                        )
                    }
                )
            }

            item(key = R.string.system, contentType = "checkbox") {
                ListItem(
                    modifier = Modifier
                        .clip(MiddleShape)
                        .padding(vertical = 1.dp)
                        .clickable(onClick = { viewModel.theme(Theme.System) }),
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        headlineColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainerHigh),
                        supportingColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainerHigh)
                    ),
                    headlineContent = {
                        Text(text = stringResource(id = R.string.system))
                    },
                    trailingContent = {
                        RadioButton(
                            selected = viewModel.theme() == Theme.System,
                            onClick = { viewModel.theme(Theme.System) },
                        )
                    }
                )
            }

            item(key = R.string.theme_enable_amoled, contentType = "switch") {
                val enabled = viewModel.theme() == Theme.System || viewModel.theme() == Theme.Dark

                ListItem(
                    modifier = Modifier
                        .clip(BottomShape)
                        .padding(top = 1.dp)
                        .clickable(enabled) { viewModel.themeAmoled(!viewModel.themeAmoled()) }
                        .enabled(enabled)
                        .height(IntrinsicSize.Min),
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        headlineColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainerHigh),
                        supportingColor = contentColorFor(MaterialTheme.colorScheme.surfaceContainerHigh)
                    ),
                    headlineContent = {
                        Text(text = stringResource(id = R.string.theme_enable_amoled))
                    },
                    supportingContent = {
                        Text(text = stringResource(id = R.string.theme_enable_amoled_explainer))
                    },
                    trailingContent = {
                        Switch(
                            modifier = Modifier.fillMaxHeight(),
                            enabled = enabled,
                            checked = viewModel.themeAmoled(),
                            onCheckedChange = { viewModel.themeAmoled(it) }
                        )
                    }
                )
            }
        }
    }
}
