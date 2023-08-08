package fe.linksheet.composable.settings.advanced

import android.content.Intent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Flag
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
import fe.linksheet.featureFlagSettingsRoute
import fe.linksheet.shizukuSettingsRoute

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

            item(key = "launch_onboarding") {
                SettingsItemRow(
                    headlineId = R.string.launch_onboaring,
                    subtitleId = R.string.launch_onboaring_explainer,
                    image = {
                        ColoredIcon(
                            icon = Icons.Default.DeveloperMode,
                            descriptionId = R.string.launch_onboaring
                        )
                    },
                    onClick = {
                        context.startActivity(Intent(context, OnboardingActivity::class.java))
                    }
                )
            }
        }
    }
}