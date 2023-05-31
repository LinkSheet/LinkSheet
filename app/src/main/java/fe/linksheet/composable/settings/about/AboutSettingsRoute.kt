package fe.linksheet.composable.settings.about

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fe.linksheet.BuildConfig
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.ColoredIcon
import fe.linksheet.composable.util.SettingsItemRow
import fe.linksheet.composable.util.SubtitleText
import fe.linksheet.creditsSettingsRoute


@Composable
fun AboutSettingsRoute(
    navController: NavHostController,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    SettingsScaffold(R.string.about, onBackPressed = onBackPressed) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            item(creditsSettingsRoute) {
                SettingsItemRow(
                    navController = navController,
                    navigateTo = creditsSettingsRoute,
                    headline = R.string.credits,
                    subtitle = R.string.credits_explainer,
                    image = {
                        ColoredIcon(icon = Icons.Default.Link, description = R.string.credits)
                    }
                )
            }

            item("github") {
                SettingsItemRow(
                    headline = R.string.github,
                    subtitle = R.string.github_explainer,
                    onClick = {
                        uriHandler.openUri("https://github.com/1fexd/LinkSheet")
                    },
                    image = {
                        ColoredIcon(icon = Icons.Default.Home, description = R.string.github)
                    }
                )
            }

            item("donate") {
                SettingsItemRow(
                    headline = R.string.donate,
                    subtitle = R.string.donate_explainer,
                    onClick = {
                        uriHandler.openUri("https://coindrop.to/fexd")
                    },
                    image = {
                        ColoredIcon(
                            icon = Icons.Default.CurrencyBitcoin,
                            description = R.string.donate
                        )
                    }
                )
            }

            item("version") {
                SettingsItemRow(
                    headline = stringResource(id = R.string.version),
                    subtitle = BuildConfig.VERSION_NAME,
                    onClick = {},
                    image = {
                        ColoredIcon(icon = Icons.Default.Info, description = R.string.version)
                    },
                    content = {
                        SubtitleText(subtitle = BuildConfig.VERSION_CODE.toString())
                    }
                )
            }
        }
    }
}
