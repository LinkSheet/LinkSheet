package fe.linksheet.composable.settings.apps

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PublishedWithChanges
import androidx.compose.material.icons.filled.Verified
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fe.linksheet.R
import fe.linksheet.appsWhichCanOpenLinksSettingsRoute
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.ColoredIcon
import fe.linksheet.composable.util.SettingsItemRow
import fe.linksheet.preferredAppsSettingsRoute
import fe.linksheet.pretendToBeApp
import fe.linksheet.util.AndroidVersion


@Composable
fun AppsSettingsRoute(
    navController: NavHostController,
    onBackPressed: () -> Unit,
) {
    SettingsScaffold(R.string.apps, onBackPressed = onBackPressed) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(), contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            item(key = preferredAppsSettingsRoute) {
                SettingsItemRow(
                    navController = navController,
                    navigateTo = preferredAppsSettingsRoute,
                    headlineId = R.string.preferred_apps,
                    subtitleId = R.string.preferred_apps_settings,
                    image = {
                        ColoredIcon(
                            icon = Icons.Default.OpenInNew,
                            descriptionId = R.string.preferred_apps
                        )
                    }
                )
            }

            if (AndroidVersion.AT_LEAST_API_31_S) {
                item(key = appsWhichCanOpenLinksSettingsRoute) {
                    SettingsItemRow(
                        navController = navController,
                        navigateTo = appsWhichCanOpenLinksSettingsRoute,
                        headlineId = R.string.apps_which_can_open_links,
                        subtitleId = R.string.apps_which_can_open_links_explainer_2,
                        image = {
                            ColoredIcon(
                                icon = Icons.Default.Verified,
                                descriptionId = R.string.apps_which_can_open_links
                            )
                        }
                    )
                }

                item(key = pretendToBeApp) {
                    SettingsItemRow(
                        navController = navController,
                        navigateTo = pretendToBeApp,
                        headlineId = R.string.pretend_to_be_app,
                        subtitleId = R.string.pretend_to_be_app_explainer,
                        image = {
                            ColoredIcon(
                                icon = Icons.Default.PublishedWithChanges,
                                descriptionId = R.string.pretend_to_be_app
                            )
                        }
                    )
                }
            }
        }
    }
}