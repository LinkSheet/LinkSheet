package fe.linksheet.experiment.ui.overhaul.composable.page.settings.about

import ClearURLsMetadata
import LibRedirectMetadata
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import fe.fastforwardkt.FastForwardRules
import fe.kotlin.extension.primitive.unixMillisUtc
import fe.kotlin.time.ISO8601DateTimeFormatter
import fe.linksheet.*
import fe.linksheet.R
import fe.linksheet.composable.util.*
import fe.linksheet.experiment.ui.overhaul.composable.ContentTypeDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.default.DefaultIconClickableShapeListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.experiment.ui.overhaul.composable.component.util.Annotated.Companion.buildAnnotatedTextContent
import fe.linksheet.experiment.ui.overhaul.composable.component.util.Resource.Companion.textContent
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
        item(key = R.string.donate, contentType = ContentTypeDefaults.SingleGroupItem) {
            DefaultIconClickableShapeListItem(
                headlineContent = textContent(R.string.donate),
                supportingContent = textContent(R.string.donate_subtitle),
                icon = Icons.Outlined.AutoAwesome,
                onClick = {
//                    uriHandler.openUri(BuildConfig.LINK_DISCORD)
                }
            )
        }


        divider(stringRes = R.string.links)

        group(size = 3) {
            item(key = R.string.credits) { padding, shape ->
                DefaultIconClickableShapeListItem(
                    shape = shape,
                    padding = padding,
                    headlineContent = textContent(R.string.credits),
                    supportingContent = textContent(R.string.credits_explainer),
                    icon = Icons.Outlined.Link,
                    onClick = { navigate(creditsSettingsRoute) }
                )
            }

            item(key = R.string.github) { padding, shape ->
                DefaultIconClickableShapeListItem(
                    shape = shape,
                    padding = padding,
                    headlineContent = textContent(R.string.github),
                    supportingContent = textContent(R.string.github_explainer),
                    icon = Icons.Outlined.Code,
                    onClick = { uriHandler.openUri(linksheetGithub) }
                )
            }

            item(key = R.string.discord) { padding, shape ->
                DefaultIconClickableShapeListItem(
                    shape = shape,
                    padding = padding,
                    headlineContent = textContent(R.string.discord),
                    supportingContent = textContent(R.string.discord_explainer),
                    icon = Icons.Outlined.Forum,
                    onClick = { uriHandler.openUri(BuildConfig.LINK_DISCORD) }
                )
            }
        }


        divider(stringRes = R.string.build_info)

        group(size = 1) {
//            item(key = R.string.built_by) { padding, shape ->
//                DefaultLeadingIconClickableShapeListItem(
//                    padding = padding,
//                    shape = shape,
//                    headlineId = R.string.built_by,
//                    supportingContent = buildType.stringRes,
//                    icon = Icons.AutoMirrored.Filled.Chat,
//                    onClick = { uriHandler.openUri(BuildConfig.LINK_DISCORD) }
//                )
//            }

            item(key = R.string.app_name) { padding, shape ->
                DefaultIconClickableShapeListItem(
                    padding = padding,
                    shape = shape,
                    headlineContent = textContent(R.string.version),
                    supportingContent = buildAnnotatedTextContent {
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

                        addNameValue(
                            stringResource(id = R.string.clear_urls_version),
                            ClearURLsMetadata.fetchedAt.unixMillisUtc.format(ISO8601DateTimeFormatter.DefaultFormat)
                        ).appendLine()

                        addNameValue(
                            stringResource(id = R.string.fastforward_version),
                            FastForwardRules.fetchedAt.unixMillisUtc.format(ISO8601DateTimeFormatter.DefaultFormat)
                        ).appendLine()

                        addNameValue(
                            stringResource(id = R.string.libredirect_version),
                            LibRedirectMetadata.fetchedAt.unixMillisUtc.format(ISO8601DateTimeFormatter.DefaultFormat)
                        )
                    },

                    icon = Icons.Outlined.Build,
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
    DefaultIconClickableShapeListItem(
        padding = padding,
        shape = shape,
        icon = icon,
        headlineContent = textContent(headline),
        supportingContent = buildAnnotatedTextContent {
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
    withStyle(style = SpanStyle(fontFamily = FontFamily.Monospace)) {
        append(" ", value)
    }

    return this
}
