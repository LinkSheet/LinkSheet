package fe.linksheet.composable.settings.link

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import fe.android.preference.helper.BasePreference
import fe.android.preference.helper.Preference
import fe.android.preference.helper.compose.RepositoryState
import fe.linksheet.R
import fe.linksheet.amp2HtmlSettingsRoute
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.settings.link.downloader.downloaderPermissionState
import fe.linksheet.composable.settings.link.downloader.requestDownloadPermission
import fe.linksheet.composable.util.ColoredIcon
import fe.linksheet.composable.util.DividedSwitchRow
import fe.linksheet.composable.util.HeadlineText
import fe.linksheet.composable.util.LinkableTextView
import fe.linksheet.composable.util.RowInfoCard
import fe.linksheet.composable.util.SubtitleText
import fe.linksheet.composable.util.SwitchRow
import fe.linksheet.downloaderSettingsRoute
import fe.linksheet.followRedirectsSettingsRoute
import fe.linksheet.libRedirectSettingsRoute
import fe.linksheet.module.viewmodel.LinksSettingsViewModel
import org.koin.androidx.compose.koinViewModel


class Operation(
    @StringRes val id: Int,
    vararg val values: RepositoryState<Boolean, Boolean, Preference<Boolean>>
) {
    fun isEnabled() = values.all { it.value }
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalFoundationApi::class)
@Composable
fun LinksSettingsRoute(
    onBackPressed: () -> Unit,
    navController: NavController,
    viewModel: LinksSettingsViewModel = koinViewModel()
) {
    val writeExternalStoragePermissionState = downloaderPermissionState()

    val operations = listOf(
        Operation(R.string.fastforward, viewModel.useFastForwardRules),
        Operation(R.string.clear_urls, viewModel.useClearUrls),
        Operation(R.string.follow_redirects, viewModel.followRedirects),
        Operation(R.string.fastforward, viewModel.useFastForwardRules, viewModel.followRedirects),
        Operation(R.string.clear_urls, viewModel.useClearUrls, viewModel.followRedirects),
        Operation(R.string.amp2html, viewModel.enableAmp2Html),
        Operation(R.string.lib_redirect, viewModel.enableLibRedirect),
    )

    SettingsScaffold(R.string.links, onBackPressed = onBackPressed) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            stickyHeader(key = "header") {
                AnimatedVisibility(visible = operations.any { it.isEnabled() }) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(horizontal = 10.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 80.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Spacer(modifier = Modifier.width(10.dp))
                                ColoredIcon(
                                    icon = Icons.Default.Info,
                                    descriptionId = R.string.info
                                )

                                Column(modifier = Modifier.padding(10.dp)) {
                                    HeadlineText(headlineId = R.string.order_of_operation)

                                    operations.forEach { operation ->
                                        AnimatedVisibility(visible = operation.isEnabled()) {
                                            SubtitleText(subtitleId = operation.id)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }

            item(key = "clear_urls") {
                SwitchRow(
                    state = viewModel.useClearUrls,
                    viewModel = viewModel,
                    headline = stringResource(id = R.string.use_clear_urls),
                    subtitleBuilder = {
                        LinkableTextView(
                            id = R.string.use_clear_urls_explainer,
                            style = LocalTextStyle.current.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp
                            )
                        )
                    }
                )
            }

            item(key = "fastforward_rules") {
                SwitchRow(
                    state = viewModel.useFastForwardRules,
                    viewModel = viewModel,
                    headline = stringResource(id = R.string.fastfoward_rules),
                    subtitleBuilder = {
                        LinkableTextView(
                            id = R.string.fastfoward_rules_explainer,
                            style = LocalTextStyle.current.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp
                            )
                        )
                    }
                )
            }

            item(key = "libredirect") {
                DividedSwitchRow(
                    state = viewModel.enableLibRedirect,
                    viewModel = viewModel,
                    headline = stringResource(id = R.string.enable_libredirect),
                    subtitleBuilder = { _ ->
                        LinkableTextView(
                            id = R.string.enable_libredirect_explainer,
                            style = LocalTextStyle.current.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp,
                            ),
                            parentChecked = false,
                            parentClickListener = {
                                navController.navigate(libRedirectSettingsRoute)
                            }
                        )
                    }
                ) {
                    navController.navigate(libRedirectSettingsRoute)
                }
            }

            item(key = "follow_redirects") {
                DividedSwitchRow(
                    state = viewModel.followRedirects,
                    viewModel = viewModel,
                    headline = R.string.follow_redirects,
                    subtitle = R.string.follow_redirects_explainer,
                    onClick = {
                        navController.navigate(followRedirectsSettingsRoute)
                    }
                )
            }

            item(key = "amp2html") {
                DividedSwitchRow(
                    state = viewModel.enableAmp2Html,
                    viewModel = viewModel,
                    headline = R.string.enable_amp2html,
                    subtitle = R.string.enable_amp2html_explainer,
                    onClick = {
                        navController.navigate(amp2HtmlSettingsRoute)
                    }
                )
            }

            item(key = "enable_downloader") {
                DividedSwitchRow(
                    state = viewModel.enableDownloader,
                    viewModel = viewModel,
                    headline = R.string.enable_downloader,
                    subtitle = R.string.enable_downloader_explainer,
                    onChange = {
                        requestDownloadPermission(
                            writeExternalStoragePermissionState,
                            viewModel,
                            viewModel.enableDownloader,
                            it
                        )
                    },
                    onClick = {
                        navController.navigate(downloaderSettingsRoute)
                    }
                )
            }

            item(key = "resolve_embeds") {
                SwitchRow(
                    state = viewModel.resolveEmbeds,
                    viewModel = viewModel,
                    headline = stringResource(id = R.string.resolve_embeds),
                    subtitle = stringResource(id = R.string.resolve_embeds_explainer)
                )
            }
        }
    }
}
