package fe.linksheet.composable.page.settings.apps

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fe.linksheet.R
import fe.linksheet.composable.page.settings.SettingsScaffold
import fe.linksheet.composable.util.ColoredIcon
import fe.linksheet.composable.util.SettingsItemRow
import fe.linksheet.navigation.appsWhichCanOpenLinksSettingsRoute


@Composable
fun AppsSettingsRoute(
    navController: NavHostController,
    onBackPressed: () -> Unit,
) {
    SettingsScaffold(R.string.app_browsers, onBackPressed = onBackPressed) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(), contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
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
        }
    }
}
