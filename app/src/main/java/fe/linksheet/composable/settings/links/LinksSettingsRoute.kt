package fe.linksheet.composable.settings.links

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.linksheet.R
import fe.linksheet.composable.util.LinkedText
import fe.linksheet.composable.util.SwitchRow
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.settings.SettingsViewModel
import fe.linksheet.composable.util.withLink


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinksSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: SettingsViewModel
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
                val annotatedString = buildAnnotatedString {
                    append(stringResource(id = R.string.clear_urls_explainer))
                    append(" ")

                    withLink("https://github.com/ClearURLs") {
                        append("ClearURLs")
                    }

                    append(" ")
                    append(stringResource(id = R.string.clear_urls_explainer_2))
                }

                SwitchRow(
                    checked = viewModel.useClearUrls,
                    onChange = {
                        viewModel.onUseClearUrls(it)
                    },
                    headline = stringResource(id = R.string.clear_urls),
                    subtitleBuilder = {
                        LinkedText(
                            text = annotatedString,
                            fontSize = 16.sp,
                            onClick = { url -> uriHandler.openUri(url) }
                        )
                    }
                )
            }

            item(key = "fastforward_rules") {
                val annotatedString = buildAnnotatedString {
                    append(stringResource(id = R.string.fastfoward_rules_explainer))
                    append(" ")

                    withLink("https://github.com/FastForwardTeam/FastForward") {
                        append("FastForward")
                    }

                    append(" ")
                    append(stringResource(id = R.string.fastfoward_rules_explainer_2))
                }
                
                SwitchRow(
                    checked = viewModel.useFastForwardRules,
                    onChange = {
                        viewModel.onUseFastForwardRules(it)
                    },
                    headline = stringResource(id = R.string.fastfoward_rules),
                    subtitleBuilder = {
                        LinkedText(
                            text = annotatedString,
                            fontSize = 16.sp,
                            onClick = { url -> uriHandler.openUri(url) }
                        )
                    }
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

            if(viewModel.followRedirects){
                item(key = "follow_redirects_local_cache") {
                    SwitchRow(
                        checked = viewModel.followRedirectsLocalCache,
                        onChange = {
                            viewModel.onFollowRedirectsLocalCache(it)
                        },
                        headlineId = R.string.follow_redirects_local_cache,
                        subtitleId = R.string.follow_redirects_local_cache_explainer
                    )
                }
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

                    SwitchRow(checked = viewModel.followRedirectsExternalService, onChange = {
                        viewModel.onFollowRedirectsExternalService(it)
                    }, headline = stringResource(id = R.string.follow_redirects_external_service), subtitle = null, subtitleBuilder = {
                        LinkedText(
                            text = annotatedString,
                            fontSize = 16.sp,
                            onClick = { url -> uriHandler.openUri(url) }
                        )
                    })
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