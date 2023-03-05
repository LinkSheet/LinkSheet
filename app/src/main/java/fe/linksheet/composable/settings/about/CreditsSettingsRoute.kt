package fe.linksheet.composable.settings.about

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import fe.linksheet.R
import fe.linksheet.composable.util.SettingsItemRow
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.settings.SettingsViewModel
import fe.linksheet.openLinkWithGithub
import fe.linksheet.sealGithub


@Composable
fun CreditsSettingsRoute(
    onBackPressed: () -> Unit,
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    SettingsScaffold(R.string.credits, onBackPressed = onBackPressed) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            item("openlinkwith") {
                SettingsItemRow(
                    headline = R.string.open_link_with,
                    subtitle = R.string.license_apache_2,
                    onClick = {
                        uriHandler.openUri(openLinkWithGithub)
                    })
            }

            item("seal") {
                SettingsItemRow(
                    headline = R.string.seal,
                    subtitle = R.string.license_gpl_3,
                    onClick = {
                        uriHandler.openUri(sealGithub)
                    }
                )
            }
        }
    }
}

