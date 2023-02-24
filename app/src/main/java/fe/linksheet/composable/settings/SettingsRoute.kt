package fe.linksheet.composable.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fe.linksheet.R
import fe.linksheet.appsWhichCanOpenLinks
import fe.linksheet.composable.ClickableRow
import fe.linksheet.extension.observeAsState
import fe.linksheet.preferredSettingsRoute
import fe.linksheet.ui.theme.HkGroteskFontFamily

@Composable
fun SettingsRoute(navController: NavController, viewModel: SettingsViewModel = viewModel()) {
    val context = LocalContext.current
    var enabled by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        enabled = !viewModel.checkDefaultBrowser(context)
    }

    val lifecycleState = LocalLifecycleOwner.current.lifecycle.observeAsState()
    LaunchedEffect(lifecycleState.value) {
        if (lifecycleState.value == Lifecycle.Event.ON_RESUME) {
            enabled = !viewModel.checkDefaultBrowser(context)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 5.dp)
    ) {
        item {
            ClickableRow(
                padding = 10.dp,
                onClick = { viewModel.openDefaultBrowserSettings(context) },
                enabled = enabled
            ) {
                Column {
                    Text(
                        text = stringResource(id = R.string.set_as_browser),
                        fontFamily = HkGroteskFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(id = R.string.set_as_browser_explainer),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        item {
            ClickableRow(
                padding = 10.dp,
                onClick = { navController.navigate(preferredSettingsRoute) }) {
                Column {
                    Text(
                        text = stringResource(id = R.string.preferred_apps),
                        fontFamily = HkGroteskFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(id = R.string.preferred_apps_settings),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        item {
            ClickableRow(
                padding = 10.dp,
                onClick = { navController.navigate(appsWhichCanOpenLinks) }) {
                Column {
                    Text(
                        text = stringResource(id = R.string.apps_which_can_open_links),
                        fontFamily = HkGroteskFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(id = R.string.apps_which_can_open_links_explainer_2),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        item {
            SwitchRow(
                checked = viewModel.enableCopyButton,
                onChange = {
                    viewModel.onEnableCopyButton(it)
                },
                headlineId = R.string.enable_copy_button,
                subtitleId = R.string.enable_copy_button_explainer
            )
        }

        item {
            SwitchRow(checked = viewModel.singleTap, onChange = {
                viewModel.onSingleTap(it)
            }, headlineId = R.string.single_tap, subtitleId = R.string.single_tap_explainer)
        }
    }
}



