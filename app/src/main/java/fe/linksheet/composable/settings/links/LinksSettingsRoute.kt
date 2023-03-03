package fe.linksheet.composable.settings.links

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import fe.linksheet.R
import fe.linksheet.composable.ClickableRow
import fe.linksheet.composable.LinkedText
import fe.linksheet.composable.SwitchRow
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.settings.SettingsViewModel
import fe.linksheet.composable.withLink
import fe.linksheet.ui.theme.HkGroteskFontFamily


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinksSettingsRoute(
    navController: NavHostController,
    onBackPressed: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

    SettingsScaffold(R.string.links, onBackPressed = onBackPressed) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            item(key = "clear_urls") {
                SwitchRow(
                    checked = viewModel.useClearUrls,
                    onChange = {
                        viewModel.onUseClearUrls(it)
                    },
                    headlineId = R.string.clear_urls,
                    subtitleId = R.string.clear_urls_explainer
                )
            }

            item(key = "follow_redirects") {
                SwitchRow(
                    checked = viewModel.followRedirects,
                    onChange = {
                        viewModel.onFollowRedirects(it)
                    },
                    headlineId = R.string.follow_redirects,
                    subtitleId = R.string.follow_redirects_explainer
                )
            }

            if (viewModel.followRedirects) {
                item(key = "follow_redirects_external_service") {
                    val annotatedString = buildAnnotatedString {
                        append(stringResource(id = R.string.follow_redirects_external_service_explainer))
                        val privacyPolicy = stringResource(
                            id =
                            R.string.follow_redirects_external_service_explainer_privacy_policy
                        )

                        append(" ")

                        withLink("https://unshorten.me/privacy-policy") {
                            append(privacyPolicy)
                        }
                    }


                    ClickableRow(
                        modifier = Modifier,
                        padding = 10.dp,
                        verticalAlignment = Alignment.CenterVertically,
                        onClick = {
                            viewModel.onFollowRedirectsExternalService(!viewModel.followRedirectsExternalService)
                        }) {
                        Column(modifier = Modifier.fillMaxWidth(0.8f)) {
                            Text(
                                text = stringResource(id = R.string.follow_redirects_external_service),
                                fontFamily = HkGroteskFontFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            LinkedText(
                                text = annotatedString,
                                fontSize = 16.sp,
                                onClick = { url -> uriHandler.openUri(url) }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Switch(
                                checked = viewModel.followRedirectsExternalService,
                                onCheckedChange = { viewModel.onFollowRedirectsExternalService(it) }
                            )
                        }
                    }
                }
            }

            if (viewModel.followRedirects) {
                item(key = "follow_only_known_trackers") {
                    SwitchRow(
                        checked = viewModel.followOnlyKnownTrackers,
                        onChange = {
                            viewModel.onFollowOnlyKnownTrackers(it)
                        },
                        headlineId = R.string.follow_only_known_trackers,
                        subtitleId = R.string.follow_only_known_trackers_explainer
                    )
                }
            }
        }
    }
}