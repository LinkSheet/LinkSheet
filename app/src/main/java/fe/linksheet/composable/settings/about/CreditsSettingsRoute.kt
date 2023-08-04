package fe.linksheet.composable.settings.about

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import fe.linksheet.R
import fe.linksheet.composable.util.SettingsItemRow
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.mastodonRedirectGithub
import fe.linksheet.openLinkWithGithub
import fe.linksheet.sealGithub


@Composable
fun CreditsSettingsRoute(
    onBackPressed: () -> Unit,
) {
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
                    headlineId = R.string.open_link_with,
                    subtitleId = R.string.license_apache_2,
                    onClick = {
                        uriHandler.openUri(openLinkWithGithub)
                    })
            }

            item("seal") {
                SettingsItemRow(
                    headlineId = R.string.seal,
                    subtitleId = R.string.license_gpl_3,
                    onClick = {
                        uriHandler.openUri(sealGithub)
                    }
                )
            }

            item("mastodon_redirect") {
                SettingsItemRow(
                    headlineId = R.string.mastodon_redirect,
                    subtitleId = R.string.license_mit,
                    onClick = {
                        uriHandler.openUri(mastodonRedirectGithub)
                    }
                )
            }
        }
    }
}

