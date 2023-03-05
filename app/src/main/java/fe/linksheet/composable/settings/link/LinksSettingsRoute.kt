package fe.linksheet.composable.settings.link

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.settings.SettingsViewModel
import fe.linksheet.composable.util.LinkableTextView
import fe.linksheet.composable.util.SwitchRow
import fe.linksheet.ui.theme.HkGroteskFontFamily


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
                SwitchRow(
                    checked = viewModel.useClearUrls,
                    onChange = {
                        viewModel.onUseClearUrls(it)
                    },
                    headline = stringResource(id = R.string.clear_urls),
                    subtitleBuilder = {
                        LinkableTextView(
                            id = R.string.clear_urls_explainer,
                            style = TextStyle.Default.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp
                            )
                        )
                    }
                )
            }

            item(key = "fastforward_rules") {
                SwitchRow(
                    checked = viewModel.useFastForwardRules,
                    onChange = {
                        viewModel.onUseFastForwardRules(it)
                    },
                    headline = stringResource(id = R.string.fastfoward_rules),
                    subtitleBuilder = {
                        LinkableTextView(
                            id = R.string.fastfoward_rules_explainer,
                            style = TextStyle.Default.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp
                            )
                        )
                    }
                )
            }

//            item(key = "libredirect") {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(end = 10.dp), verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth(0.8f)
//                            .clip(RoundedCornerShape(6.dp))
//                            .clickable {
//
//                            }
//                            .padding(start = 10.dp)
//
//                    ) {
//                        Text(
//                            text = stringResource(id = R.string.enable_libredirect),
//                            fontFamily = HkGroteskFontFamily,
//                            fontWeight = FontWeight.SemiBold,
//                            fontSize = 18.sp,
//                            color = MaterialTheme.colorScheme.onSurface
//                        )
//
//                        Text(
//                            text = stringResource(id = R.string.enable_libredirect_explainer),
//                            fontSize = 16.sp,
//                            color = MaterialTheme.colorScheme.onSurface
//                        )
//                    }
//
//                    Divider(
//                        modifier = Modifier
//                            .height(32.dp)
//                            .padding(horizontal = 8.dp)
//                            .width(1f.dp)
//                            .align(Alignment.CenterVertically),
//                        color = MaterialTheme.colorScheme.tertiary
//                    )
//
//                    Row(
//                        modifier = Modifier.fillMaxWidth(),
//                        horizontalArrangement = Arrangement.End
//                    ) {
//                        Switch(checked = false, onCheckedChange = {
//
//                        })
//                    }
//                }
//            }

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

            if (viewModel.followRedirects) {
                item(key = "follow_redirects_external_service") {
                    SwitchRow(
                        checked = viewModel.followRedirectsExternalService,
                        onChange = {
                            viewModel.onFollowRedirectsExternalService(it)
                        },
                        headline = stringResource(id = R.string.follow_redirects_external_service),
                        subtitle = null,
                        subtitleBuilder = {
                            LinkableTextView(
                                id = R.string.follow_redirects_external_service_explainer,
                                style = TextStyle.Default.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 16.sp
                                )
                            )
                        })
                }
            }
        }
    }
}