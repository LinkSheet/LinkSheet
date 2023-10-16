package fe.linksheet.composable.settings.about

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.pluralStringResource
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
import fe.linksheet.aboutSettingsRoute
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.ColoredIcon
import fe.linksheet.composable.util.LinkableSubtitleText
import fe.linksheet.composable.util.SettingsItemRow
import fe.linksheet.composable.util.SubtitleText
import fe.linksheet.composable.util.annotatedStringResource
import fe.linksheet.creditsSettingsRoute
import fe.linksheet.developmentTimeHours
import fe.linksheet.developmentTimeMonths
import fe.linksheet.discordInvite
import fe.linksheet.donateSettingsRoute
import fe.linksheet.donationBuyMeACoffee
import fe.linksheet.donationCrypto
import fe.linksheet.extension.androidx.navigate
import fe.linksheet.extension.compose.currentActivity
import fe.linksheet.lineSeparator
import fe.linksheet.linksheetGithub
import fe.linksheet.mainRoute
import fe.linksheet.settingsRoute

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DonateSettingsRoute(
    onBackPressed: () -> Unit
) {
    val uriHandler = LocalUriHandler.current

    val developmentHours =
        pluralStringResource(id = R.plurals.hours, developmentTimeHours, developmentTimeHours)
    val developmentMonths =
        pluralStringResource(id = R.plurals.months, developmentTimeMonths, developmentTimeMonths)

    SettingsScaffold(R.string.donate, onBackPressed = onBackPressed) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            stickyHeader {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 80.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = annotatedStringResource(
                                    id = R.string.linksheet_donation_explainer,
                                    developmentHours,
                                    developmentMonths
                                )
                            )

                            Text(
                                text = annotatedStringResource(
                                    id = R.string.linksheet_donation_explainer_2
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(onClick = {
                                uriHandler.openUri(donationBuyMeACoffee)
                            }) {
                                Text(
                                    text = stringResource(id = R.string.donate_card),
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            LinkableSubtitleText(
                                id = R.string.donate_explainer_crypto,
                                enabled = true
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(onClick = {
                                uriHandler.openUri(donationCrypto)
                            }) {
                                Text(
                                    text = stringResource(id = R.string.donate_crypto),
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            LinkableSubtitleText(
                                id = R.string.donate_other,
                                enabled = true
                            )
                        }

                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(10.dp)) {

                }
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
