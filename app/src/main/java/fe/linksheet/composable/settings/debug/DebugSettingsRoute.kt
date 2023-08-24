package fe.linksheet.composable.settings.debug

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.ColoredIcon
import fe.linksheet.composable.util.SettingsItemRow
import fe.linksheet.loadDumpedPreferences
import fe.linksheet.logViewerSettingsRoute


@Composable
fun DebugSettingsRoute(
    navController: NavHostController,
    onBackPressed: () -> Unit,
) {
    SettingsScaffold(R.string.debug, onBackPressed = onBackPressed) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            item(key = "logs") {
                SettingsItemRow(
                    navController = navController,
                    navigateTo = logViewerSettingsRoute,
                    headlineId = R.string.logs,
                    subtitleId = R.string.logs_explainer,
                    image = {
                        ColoredIcon(icon = Icons.Default.List, descriptionId = R.string.logs)
                    }
                )
            }

            item(key = "load_dumped_preferences") {
                SettingsItemRow(
                    navController = navController,
                    navigateTo = loadDumpedPreferences,
                    headlineId = R.string.import_dumped_preference,
                    subtitleId = R.string.import_dumped_preference_explainer,
                    image = {
                        ColoredIcon(icon = Icons.Default.List, descriptionId = R.string.logs)
                    }
                )
            }
        }
    }
}