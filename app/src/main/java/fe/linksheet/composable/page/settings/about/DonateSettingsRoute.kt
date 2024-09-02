package fe.linksheet.composable.page.settings.about

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import fe.linksheet.BuildConfig
import fe.linksheet.R
import fe.linksheet.composable.page.settings.SettingsScaffold
import fe.linksheet.composable.util.LinkableSubtitleText
import fe.linksheet.composable.util.rememberAnnotatedStringResource


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DonateSettingsRoute(
    onBackPressed: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
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
                                text = rememberAnnotatedStringResource(
                                    id = R.string.linksheet_donation_explainer_2
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(onClick = {
                                uriHandler.openUri(BuildConfig.LINK_BUY_ME_A_COFFEE)
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
                                uriHandler.openUri(BuildConfig.LINK_CRYPTO)
                            }) {
                                Text(
                                    text = stringResource(id = R.string.donate_crypto),
                                )
                            }
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
