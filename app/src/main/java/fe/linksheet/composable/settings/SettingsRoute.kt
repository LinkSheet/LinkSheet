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
import fe.linksheet.ui.theme.HkGroteskFontFamily

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
                    headline = R.string.apps,
                    subtitle = R.string.apps_explainer,
                    image = {
                        ColoredIcon(icon = Icons.Default.Apps, description = R.string.apps)
                    }
                )
            }

            item(key = "bottom_sheet") {
                SettingsItemRow(
                    navController = navController,
                    navigateTo = bottomSheetSettingsRoute,
                    headline = R.string.bottom_sheet,
                    subtitle = R.string.bottom_sheet_explainer,
                    image = {
                        ColoredIcon(icon = Icons.Default.ArrowUpward, description = R.string.bottom_sheet)
                    }
                )
            }

            item(key = "links") {
                SettingsItemRow(
                    navController = navController,
                    navigateTo = linksSettingsRoute,
                    headline = R.string.links,
                    subtitle = R.string.links_explainer,
                    image = {
                        ColoredIcon(icon = Icons.Default.Link, description = R.string.links)
                    }
                )
            }

            item(key = "theme") {
                SettingsItemRow(
                    navController = navController,
                    navigateTo = themeSettingsRoute,
                    headline = R.string.theme,
                    subtitle = R.string.theme_explainer,
                    image = {
                        ColoredIcon(icon = Icons.Default.DisplaySettings, description = R.string.theme)
                    }
                )
            }

            item(key = "about") {
                SettingsItemRow(
                    navController = navController,
                    navigateTo = aboutSettingsRoute,
                    headline = R.string.about,
                    subtitle = R.string.about_explainer,
                    image = {
                        ColoredIcon(icon = Icons.Default.Info, description = R.string.about)
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


