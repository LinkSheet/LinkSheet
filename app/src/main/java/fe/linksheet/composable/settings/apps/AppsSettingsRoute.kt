package fe.linksheet.composable.settings.apps

import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fe.linksheet.R
import fe.linksheet.appsWhichCanOpenLinksSettingsRoute
import fe.linksheet.composable.SettingsItemRow
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.preferredAppsSettingsRoute
import fe.linksheet.preferredBrowserSettingsRoute


@Composable
fun AppsSettingsRoute(
    navController: NavHostController,
    onBackPressed: () -> Unit,
) {
    SettingsScaffold(R.string.apps, onBackPressed = onBackPressed) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxHeight(), contentPadding = PaddingValues(horizontal = 5.dp)) {
            item(key = preferredBrowserSettingsRoute) {
                SettingsItemRow(
                    navController = navController,
                    navigateTo = preferredBrowserSettingsRoute,
                    headline = R.string.preferred_browser,
                    subtitle = R.string.preferred_browser_explainer
                )
            }

            item(key = preferredAppsSettingsRoute) {
                SettingsItemRow(
                    navController = navController,
                    navigateTo = preferredAppsSettingsRoute,
                    headline = R.string.preferred_apps,
                    subtitle = R.string.preferred_apps_settings
                )
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                item(key = appsWhichCanOpenLinksSettingsRoute) {
                    SettingsItemRow(
                        navController = navController,
                        navigateTo = appsWhichCanOpenLinksSettingsRoute,
                        headline = R.string.apps_which_can_open_links,
                        subtitle = R.string.apps_which_can_open_links_explainer_2
                    )
                }
            }
        }
    }
}