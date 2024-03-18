package fe.linksheet.composable.settings.advanced

import android.content.Intent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fe.linksheet.R
import fe.linksheet.activity.onboarding.OnboardingActivity
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.ColoredIcon
import fe.linksheet.composable.util.SettingsItemRow
import fe.linksheet.experimentSettingsRoute
import fe.linksheet.exportImportSettingsRoute
import fe.linksheet.featureFlagSettingsRoute

@Composable
fun AdvancedSettingsRoute(
    navController: NavHostController,
    onBackPressed: () -> Unit,
) {
    val context = LocalContext.current

    SettingsScaffold(R.string.advanced, onBackPressed = onBackPressed) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
//            item(key = "shizuku") {
//                SettingsItemRow(
//                    navController = navController,
//                    navigateTo = shizukuSettingsRoute,
//                    headlineId = R.string.shizuku,
//                    subtitleId = R.string.shizuku_explainer,
//                    image = {
//                        ColoredIcon(icon = Icons.Default.Cable, descriptionId = R.string.advanced)
//                    }
//                )
//            }

            item(key = "feature_flags") {
                SettingsItemRow(
                    navController = navController,
                    navigateTo = featureFlagSettingsRoute,
                    headlineId = R.string.feature_flags,
                    subtitleId = R.string.feature_flags_explainer,
                    image = {
                        ColoredIcon(
                            icon = Icons.Default.Flag,
                            descriptionId = R.string.feature_flags
                        )
                    }
                )
            }

            item(key = "experiments") {
                SettingsItemRow(
                    navController = navController,
                    navigateTo = experimentSettingsRoute.route,
                    headlineId = R.string.experiments,
                    subtitleId = R.string.experiments_explainer,
                    image = {
                        ColoredIcon(
                            icon = Icons.Default.Android,
                            descriptionId = R.string.experiments
                        )
                    }
                )
            }

            item(key = "export_import_settings"){
                SettingsItemRow(
                    navController = navController,
                    navigateTo = exportImportSettingsRoute,
                    headlineId = R.string.export_import_settings,
                    subtitleId = R.string.export_import_settings_explainer,
                    image = {
                        ColoredIcon(
                            icon = Icons.Default.ImportExport,
                            descriptionId = R.string.export_import_settings
                        )
                    }
                )
            }
        }
    }
}
