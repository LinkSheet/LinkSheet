package fe.linksheet.composable.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fe.linksheet.*
import fe.linksheet.R
import fe.linksheet.composable.util.ColoredIcon
import fe.linksheet.composable.util.SettingsItemRow
import fe.linksheet.ui.HkGroteskFontFamily

@Composable
fun SettingsRoute(
    navController: NavController,
    onBackPressed: () -> Unit
) {
    SettingsScaffold(R.string.settings, onBackPressed = onBackPressed) { padding ->
        LazyColumn(modifier = Modifier.padding(padding), contentPadding = PaddingValues(5.dp)) {
            item(key = "apps") {
                SettingsItemRow(
                    navController = navController,
                    navigateTo = appsSettingsRoute,
                    headlineId = R.string.apps,
                    subtitleId = R.string.apps_explainer,
                    image = {
                        ColoredIcon(icon = Icons.Default.Apps, descriptionId = R.string.apps)
                    }
                )
            }

            item(key = "browser") {
                SettingsItemRow(
                    navController = navController,
                    navigateTo = browserSettingsRoute,
                    headlineId = R.string.browser,
                    subtitleId = R.string.browser_explainer,
                    image = {
                        ColoredIcon(
                            icon = Icons.Default.OpenInBrowser,
                            descriptionId = R.string.browser
                        )
                    }
                )
            }

            item(key = "bottom_sheet") {
                SettingsItemRow(
                    navController = navController,
                    navigateTo = bottomSheetSettingsRoute,
                    headlineId = R.string.bottom_sheet,
                    subtitleId = R.string.bottom_sheet_explainer,
                    image = {
                        ColoredIcon(
                            icon = Icons.Default.ArrowUpward,
                            descriptionId = R.string.bottom_sheet
                        )
                    }
                )
            }

            item(key = "links") {
                SettingsItemRow(
                    navController = navController,
                    navigateTo = linksSettingsRoute,
                    headlineId = R.string.links,
                    subtitleId = R.string.links_explainer,
                    image = {
                        ColoredIcon(icon = Icons.Default.Link, descriptionId = R.string.links)
                    }
                )
            }

            item(key = "theme") {
                SettingsItemRow(
                    navController = navController,
                    navigateTo = themeSettingsRoute,
                    headlineId = R.string.theme,
                    subtitleId = R.string.theme_explainer,
                    image = {
                        ColoredIcon(
                            icon = Icons.Default.DisplaySettings,
                            descriptionId = R.string.theme
                        )
                    }
                )
            }

//            item(key = "advanced") {
//                SettingsItemRow(
//                    navController = navController,
//                    navigateTo = advancedSettingsRoute,
//                    headlineId = R.string.advanced,
//                    subtitleId = R.string.advanced_explainer,
//                    image = {
//                        ColoredIcon(icon = Icons.Default.Adb, descriptionId = R.string.advanced)
//                    }
//                )
//            }

            item(key = "debug") {
                SettingsItemRow(
                    navController = navController,
                    navigateTo = debugSettingsRoute,
                    headlineId = R.string.debug,
                    subtitleId = R.string.debug_explainer,
                    image = {
                        ColoredIcon(icon = Icons.Default.BugReport, descriptionId = R.string.debug)
                    }
                )
            }

            item(key = "about") {
                SettingsItemRow(
                    navController = navController,
                    navigateTo = aboutSettingsRoute,
                    headlineId = R.string.about,
                    subtitleId = R.string.about_explainer,
                    image = {
                        ColoredIcon(icon = Icons.Default.Info, descriptionId = R.string.about)
                    }
                )
            }
        }
    }
}

@Composable
fun ItemDivider(@StringRes id: Int) {
    Spacer(modifier = Modifier.height(10.dp))

    Text(
        modifier = Modifier.padding(horizontal = 10.dp),
        text = stringResource(id = id),
        fontFamily = HkGroteskFontFamily,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
    )

    Spacer(modifier = Modifier.height(5.dp))
}


