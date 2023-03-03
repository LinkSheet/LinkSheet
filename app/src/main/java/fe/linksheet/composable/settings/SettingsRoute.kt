package fe.linksheet.composable.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fe.linksheet.*
import fe.linksheet.R
import fe.linksheet.composable.SettingsItemRow
import fe.linksheet.extension.observeAsState
import fe.linksheet.ui.theme.HkGroteskFontFamily
import fe.linksheet.util.Results
import kotlinx.coroutines.delay

@Composable
fun SettingsRoute(
    navController: NavController,
    onBackPressed: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current

    val lifecycleState = LocalLifecycleOwner.current.lifecycle
        .observeAsState(ignoreFirst = Lifecycle.Event.ON_RESUME)

    LaunchedEffect(lifecycleState.first) {
        if (lifecycleState.first == Lifecycle.Event.ON_RESUME) {
            if (!viewModel.getUsageStatsAllowed(context)) {
                viewModel.onUsageStatsSorting(false)
            }

            if (viewModel.wasTogglingUsageStatsSorting) {
                viewModel.onUsageStatsSorting(true)
                viewModel.wasTogglingUsageStatsSorting = false
            }
        }
    }

    SettingsScaffold(R.string.settings, onBackPressed = onBackPressed) { padding ->
        LazyColumn(modifier = Modifier.padding(padding), contentPadding = PaddingValues(5.dp)) {
            item(key = "apps") {
                SettingsItemRow(
                    navController = navController,
                    navigateTo = appsSettingsRoute,
                    headline = R.string.apps,
                    subtitle = R.string.apps_explainer
                )
            }

            item(key = "bottom_sheet") {
                SettingsItemRow(
                    navController = navController,
                    navigateTo = bottomSheetSettingsRoute,
                    headline = R.string.bottom_sheet,
                    subtitle = R.string.bottom_sheet_explainer
                )
            }

            item(key = "links") {
                SettingsItemRow(
                    navController = navController,
                    navigateTo = linksSettingsRoute,
                    headline = R.string.links,
                    subtitle = R.string.links_explainer
                )
            }

            item(key = "about") {
                SettingsItemRow(
                    navController = navController,
                    navigateTo = aboutSettingsRoute,
                    headline = R.string.about,
                    subtitle = R.string.about_explainer
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


