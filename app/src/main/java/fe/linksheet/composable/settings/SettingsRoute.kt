package fe.linksheet.composable.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
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
    val state = lifecycleState.value

    if (state == Lifecycle.Event.ON_RESUME) {
        enabled = !viewModel.checkDefaultBrowser(context)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 5.dp)) {
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

        ClickableRow(padding = 10.dp, onClick = { navController.navigate(appsWhichCanOpenLinks) }) {
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
}



