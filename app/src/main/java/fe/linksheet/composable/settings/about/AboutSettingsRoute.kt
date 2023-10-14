package fe.linksheet.composable.settings.about

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fe.kotlin.extension.unixMillisAtUtc
import fe.kotlin.util.ISO8601DateTimeFormatOption
import fe.linksheet.BuildConfig
import fe.linksheet.LinkSheetAppConfig
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.ColoredIcon
import fe.linksheet.composable.util.SettingsItemRow
import fe.linksheet.composable.util.SubtitleText
import fe.linksheet.composable.util.annotatedStringResource
import fe.linksheet.creditsSettingsRoute
import fe.linksheet.discordInvite
import fe.linksheet.donationBuyMeACoffee
import fe.linksheet.donationCrypto
import fe.linksheet.extension.compose.currentActivity
import fe.linksheet.lineSeparator
import fe.linksheet.linksheetGithub

@Composable
fun AboutSettingsRoute(
    navController: NavHostController,
    onBackPressed: () -> Unit
) {
    val activity = LocalContext.currentActivity()
    val uriHandler = LocalUriHandler.current
    val clipboardManager = LocalClipboardManager.current
    val buildDate =
        BuildConfig.BUILT_AT.unixMillisAtUtc.value.format(ISO8601DateTimeFormatOption.default)

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
                    headlineId = R.string.credits,
                    subtitleId = R.string.credits_explainer,
                    image = {
                        ColoredIcon(icon = Icons.Default.Link, descriptionId = R.string.credits)
                    }
                )
            }

            item("github") {
                SettingsItemRow(
                    headlineId = R.string.github,
                    subtitleId = R.string.github_explainer,
                    onClick = {
                        uriHandler.openUri(linksheetGithub)
                    },
                    image = {
                        ColoredIcon(icon = Icons.Default.Home, descriptionId = R.string.github)
                    }
                )
            }

            item("discord") {
                SettingsItemRow(
                    headlineId = R.string.discord,
                    subtitleId = R.string.discord_explainer,
                    onClick = {
                        uriHandler.openUri(discordInvite)
                    },
                    image = {
                        ColoredIcon(icon = Icons.Default.Chat, descriptionId = R.string.discord)
                    }
                )
            }

            if (LinkSheetAppConfig.showDonationBanner()) {
                item("donate") {
                    SettingsItemRow(
                        headline = stringResource(id = R.string.donate_crypto),
                        subtitle = annotatedStringResource(id = R.string.donate_explainer_crypto),
                        onClick = {
                            uriHandler.openUri(donationCrypto)
                        },
                        image = {
                            ColoredIcon(
                                icon = Icons.Default.CurrencyBitcoin,
                                descriptionId = R.string.donate_crypto
                            )
                        }
                    )
                }

                item("donate-1") {
                    SettingsItemRow(
                        headlineId = R.string.donate_card,
                        subtitleId = R.string.donate_explainer,
                        onClick = {
                            uriHandler.openUri(donationBuyMeACoffee)
                        },
                        image = {
                            ColoredIcon(
                                icon = Icons.Default.CurrencyExchange,
                                descriptionId = R.string.donate_crypto
                            )
                        }
                    )
                }
            }

            item("version") {
                val versionName = buildNameValueAnnotatedString(
                    stringResource(id = R.string.version_name),
                    BuildConfig.VERSION_NAME
                )

                val builtAt = buildNameValueAnnotatedString(
                    stringResource(id = R.string.built_at),
                    buildDate
                )

                val workflow = if (BuildConfig.GITHUB_WORKFLOW_RUN_ID != null) {
                    buildNameValueAnnotatedString(
                        stringResource(id = R.string.github_workflow_run_id),
                        BuildConfig.GITHUB_WORKFLOW_RUN_ID
                    )
                } else null

                SettingsItemRow(
                    headline = stringResource(id = R.string.version),
                    subtitle = versionName,
                    onClick = {
                        clipboardManager.setText(buildAnnotatedString {
                            append(
                                activity.getText(R.string.linksheet_version_info_header),
                                lineSeparator,
                                versionName,
                                lineSeparator,
                                builtAt
                            )

                            if (workflow != null) {
                                append(lineSeparator, workflow)
                            }
                        })
                    },
                    image = {
                        ColoredIcon(icon = Icons.Default.Info, descriptionId = R.string.version)
                    },
                    content = {
                        SubtitleText(subtitle = builtAt)

                        if (workflow != null) {
                            SubtitleText(
                                subtitle = workflow
                            )
                        }
                    }
                )
            }
        }
    }
}

private fun buildNameValueAnnotatedString(name: String, value: String): AnnotatedString {
    return buildAnnotatedString {
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append(name)
        }

        append(" ", value)
    }
}
