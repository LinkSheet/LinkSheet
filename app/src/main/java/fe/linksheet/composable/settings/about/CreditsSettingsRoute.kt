package fe.linksheet.composable.settings.about

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fe.linksheet.R
import fe.linksheet.composable.util.SettingsItemRow
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.SubtitleText
import fe.linksheet.mastodonRedirectGithub
import fe.linksheet.openLinkWithGithub


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
                    headline = stringResource(id = R.string.open_link_with),
                    subtitle = stringResource(id = R.string.license_apache_2),
                    onClick = {
                        uriHandler.openUri(openLinkWithGithub)
                    },
                    content = {
                        SubtitleText(subtitle = stringResource(id = R.string.open_link_with_subtitle_2))
                    }
                )
            }

            item("mastodon_redirect") {
                SettingsItemRow(
                    headline = stringResource(id = R.string.mastodon_redirect),
                    subtitle = stringResource(id = R.string.license_mit),
                    onClick = {
                        uriHandler.openUri(mastodonRedirectGithub)
                    },
                    content = {
                        SubtitleText(subtitle = stringResource(id = R.string.mastodon_redirect_subtitle_2))
                    }
                )
            }
        }
    }
}

