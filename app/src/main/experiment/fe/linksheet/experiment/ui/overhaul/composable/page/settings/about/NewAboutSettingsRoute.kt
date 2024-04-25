package fe.linksheet.experiment.ui.overhaul.composable.page.settings.about

import ClearURLsMetadata
import LibRedirectMetadata
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fe.fastforwardkt.FastForwardRules
import fe.kotlin.extension.primitive.unixMillisUtc
import fe.kotlin.time.ISO8601DateTimeFormatter
import fe.linksheet.*
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.*
import fe.linksheet.experiment.ui.overhaul.composable.ContentTypeDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.default.DefaultLeadingIconClickableShapeListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.experiment.ui.overhaul.composable.component.page.layout.group
import fe.linksheet.extension.android.showToast
import fe.linksheet.module.viewmodel.AboutSettingsViewModel
import fe.linksheet.ui.LocalActivity
import fe.linksheet.util.AppSignature
import org.koin.androidx.compose.koinViewModel


@Composable
fun NewAboutSettingsRoute(
    onBackPressed: () -> Unit,
    navigate: (String) -> Unit,
    viewModel: AboutSettingsViewModel = koinViewModel(),
) {
    val activity = LocalActivity.current
    val uriHandler = LocalUriHandler.current
    val clipboardManager = LocalClipboardManager.current
    val buildDate =
        BuildConfig.BUILT_AT.unixMillisUtc.format(ISO8601DateTimeFormatter.DefaultFormat)
    val buildType = AppSignature.checkSignature(activity)

    var devClicks by remember { mutableIntStateOf(0) }

    SaneScaffoldSettingsPage(headline = stringResource(id = R.string.about), onBackPressed = onBackPressed) {
        divider(stringRes = R.string.misc_settings)

        group(size = 3) {
            item(key = R.string.credits) { padding, shape ->
                DefaultLeadingIconClickableShapeListItem(
                    shape = shape,
                    padding = padding,
                    headlineId = R.string.credits,
                    subtitleId = R.string.credits_explainer,
                    icon = Icons.Default.Link,
                    onClick = { navigate(creditsSettingsRoute) }
                )
            }

            item(key = R.string.github) { padding, shape ->
                DefaultLeadingIconClickableShapeListItem(
                    shape = shape,
                    padding = padding,
                    headlineId = R.string.github,
                    subtitleId = R.string.github_explainer,
                    icon = Icons.Default.Home,
                    onClick = { uriHandler.openUri(linksheetGithub) }
                )
            }

            item(key = R.string.discord) { padding, shape ->
                DefaultLeadingIconClickableShapeListItem(
                    shape = shape,
                    padding = padding,
                    headlineId = R.string.discord,
                    subtitleId = R.string.discord_explainer,
                    icon = Icons.AutoMirrored.Filled.Chat,
                    onClick = { uriHandler.openUri(BuildConfig.LINK_DISCORD) }
                )
            }
        }

        divider(stringRes = R.string.donation)

        item(key = R.string.enable_libredirect, contentType = ContentTypeDefaults.SingleGroupItem) {
            DefaultLeadingIconClickableShapeListItem(
                headlineId = R.string.donate,
                subtitleId = R.string.donate_explainer,
                icon = Icons.AutoMirrored.Filled.Chat,
                onClick = { uriHandler.openUri(BuildConfig.LINK_DISCORD) }
            )
        }

        divider(stringRes = R.string.just_version)

        group(size = 5) {
            item(key = R.string.built_by) { padding, shape ->
                DefaultLeadingIconClickableShapeListItem(
                    padding = padding,
                    shape = shape,
                    headlineId = R.string.built_by,
                    subtitleId = buildType.stringRes,
                    icon = Icons.AutoMirrored.Filled.Chat,
                    onClick = { uriHandler.openUri(BuildConfig.LINK_DISCORD) }
                )
            }

            item(key = R.string.linksheet_version_info_header) { padding, shape ->
                DefaultLeadingIconClickableShapeListItem(
                    padding = padding,
                    shape = shape,
                    headlineText = AnnotatedString(stringResource(id = R.string.version)),
                    subtitleText = buildAnnotatedString {
                        addNameValue(stringResource(id = R.string.built_at), buildDate).appendLine()
                        addNameValue(
                            stringResource(id = R.string.commit),
                            BuildConfig.COMMIT.substring(0, 7)
                        ).appendLine()
                        addNameValue(stringResource(id = R.string.branch), BuildConfig.BRANCH).appendLine()
                        addNameValue(stringResource(id = R.string.version_name), BuildConfig.VERSION_NAME).appendLine()
                        addNameValue(stringResource(id = R.string.flavor), BuildConfig.FLAVOR).appendLine()
                        addNameValue(stringResource(id = R.string.type), BuildConfig.BUILD_TYPE)

                        if (BuildConfig.GITHUB_WORKFLOW_RUN_ID != null) {
                            appendLine()
                            addNameValue(
                                stringResource(id = R.string.github_workflow_run_id),
                                BuildConfig.GITHUB_WORKFLOW_RUN_ID
                            )
                        }
                    },
                    icon = Icons.Default.Build,
                    onClick = { }
                )


//                SettingsItemRow(
//                    headline = stringResource(id = R.string.version),
//                    subtitle = builtAt,
//                    onClick = {
//                        if (devClicks == 7 && !viewModel.devModeEnabled()) {
//                            viewModel.devModeEnabled(true)
//                            activity.showToast(R.string.dev_mode_enabled, Toast.LENGTH_SHORT)
//                        }
//
//                        devClicks++
//                    },
//                    image = {
//                        ColoredIcon(icon = Icons.Default.Link, descriptionId = R.string.version)
//                    },
//                    content = {
//                        SubtitleText(subtitle = flavor)
//                        SubtitleText(subtitle = type)
//                        SubtitleText(subtitle = commit)
//                        SubtitleText(subtitle = branch)
//                        SubtitleText(subtitle = fullVersionName)
//
//                        if (workflow != null) {
//                            SubtitleText(
//                                subtitle = workflow
//                            )
//                        }
//                    }
//                )
            }

            item(R.string.clear_urls_version) { padding, shape ->
                LibraryLastUpdatedRow(
                    padding = padding,
                    shape = shape,
                    headline = R.string.clear_urls_version,
                    fetchedAt = ClearURLsMetadata.fetchedAt,
                    icon = Icons.Default.ClearAll
                )
            }

            item(R.string.fastforward_version) { padding, shape ->
                LibraryLastUpdatedRow(
                    padding = padding,
                    shape = shape,
                    headline = R.string.fastforward_version,
                    fetchedAt = FastForwardRules.fetchedAt,
                    icon = Icons.Default.Bolt
                )
            }

            item(R.string.libredirect_version) { padding, shape ->
                LibraryLastUpdatedRow(
                    padding = padding,
                    shape = shape,
                    headline = R.string.libredirect_version,
                    fetchedAt = LibRedirectMetadata.fetchedAt,
                    icon = Icons.Default.Security
                )
            }
        }
    }

//    SettingsScaffold(R.string.about, onBackPressed = onBackPressed) { padding ->
//        LazyColumn(
//            modifier = Modifier
//                .padding(padding)
//                .fillMaxHeight(),
//            contentPadding = PaddingValues(horizontal = 5.dp)
//        ) {

//            item("linksheet_version") {

//
//                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
//                    TextButton(onClick = {
//                        clipboardManager.setText(buildAnnotatedString {
//                            append(
//                                activity.getText(R.string.linksheet_version_info_header),
//                                lineSeparator,
//                                builtAt,
//                                lineSeparator,
//                                flavor,
//                                lineSeparator,
//                                type,
//                                lineSeparator,
//                                commit,
//                                lineSeparator,
//                                branch,
//                                lineSeparator,
//                                fullVersionName
//                            )
//
//                            if (workflow != null) {
//                                append(lineSeparator, workflow)
//                            }
//                        })
//                    }) {
//                        Text(text = stringResource(id = R.string.copy_version_information))
//                    }
//                }
//            }
//
////            item("clearurls_version") {
////                LibraryLastUpdatedRow(
////                    R.string.clear_urls_version,
////                    ClearURLsMetadata.fetchedAt,
////                    Icons.Default.ClearAll
////                )
////            }
////
////            item("fastforward_version") {
////                LibraryLastUpdatedRow(
////                    R.string.fastforward_version,
////                    FastForwardRules.fetchedAt,
////                    Icons.Default.Bolt
////                )
////            }
////
////            item("libredirect") {
////                LibraryLastUpdatedRow(
////                    R.string.libredirect_version,
////                    LibRedirectMetadata.fetchedAt,
////                    Icons.Default.Security
////                )
////            }
//        }
}


@Composable
private fun LibraryLastUpdatedRow(
    padding: PaddingValues,
    shape: Shape,
    @StringRes headline: Int,
    fetchedAt: Long,
    icon: ImageVector,
) {
    DefaultLeadingIconClickableShapeListItem(
        padding = padding,
        shape = shape,
        icon = icon,
        headlineText = AnnotatedString(stringResource(id = headline)),
        subtitleText = buildAnnotatedString {
            addNameValue(
                stringResource(id = R.string.last_updated),
                fetchedAt.unixMillisUtc.format(ISO8601DateTimeFormatter.DefaultFormat)
            )
        },
        onClick = {}
    )
}

private fun AnnotatedString.Builder.addNameValue(name: String, value: String): AnnotatedString.Builder {
    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append(name) }
    append(" ", value)

    return this
}
