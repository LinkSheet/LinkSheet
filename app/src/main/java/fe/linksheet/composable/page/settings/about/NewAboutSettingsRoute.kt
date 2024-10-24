package fe.linksheet.composable.page.settings.about

import LibRedirectMetadata
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import fe.android.compose.dialog.helper.confirm.ConfirmActionDialog
import fe.android.compose.dialog.helper.confirm.rememberConfirmActionDialog
import fe.android.compose.feedback.FeedbackType
import fe.android.compose.feedback.LocalHapticFeedbackInteraction
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.AnnotatedStringContent.Companion.buildAnnotatedTextContent
import fe.android.compose.text.AnnotatedStringResourceContent.Companion.annotatedStringResource
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.android.span.helper.composable.fromStringRes
import fe.clearurlskt.ClearURLsMetadata
import fe.composekit.component.ContentType
import fe.composekit.component.list.column.group.ListItemData
import fe.composekit.component.list.item.default.DefaultTwoLineIconClickableShapeListItem
import fe.composekit.layout.column.group
import fe.fastforwardkt.FastForwardRules
import fe.linksheet.*
import fe.linksheet.R
import fe.linksheet.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.extension.android.showToast
import fe.linksheet.module.viewmodel.AboutSettingsViewModel
import fe.linksheet.composable.ui.LocalActivity
import fe.linksheet.util.AppInfo
import fe.linksheet.util.AppSignature
import fe.std.javatime.extension.unixMillisUtc
import fe.std.javatime.time.ISO8601DateTimeFormatter
import org.koin.androidx.compose.koinViewModel


object NewAboutSettingsRouteData {
    val externalVersions = arrayOf(
        ListItemData(
            Icons.Outlined.ClearAll.iconPainter,
            textContent(R.string.clear_urls_version),
            additional = ClearURLsMetadata.FETCHED_AT
        ),
        ListItemData(
            Icons.Outlined.Bolt.iconPainter,
            textContent(R.string.fastforward_version),
            additional = FastForwardRules.fetchedAt
        ),
        ListItemData(
            Icons.Outlined.Security.iconPainter,
            textContent(R.string.libredirect_version),
            additional = LibRedirectMetadata.fetchedAt
        )
    )
}

@Composable
fun NewAboutSettingsRoute(
    onBackPressed: () -> Unit,
    navigate: (String) -> Unit,
    viewModel: AboutSettingsViewModel = koinViewModel(),
) {
    val activity = LocalActivity.current
    val uriHandler = LocalUriHandler.current
    val buildDate = BuildConfig.BUILT_AT.unixMillisUtc.format(ISO8601DateTimeFormatter.DefaultFormat)
    val buildType = AppSignature.checkSignature(activity)

    var devClicks by remember { mutableIntStateOf(0) }

//    LaunchedEffect(Unit) {
//        while (true) {
//            delay(5000)
//            devClicks = 0
//        }
//    }

    val interaction = LocalHapticFeedbackInteraction.current

    val state = rememberConfirmActionDialog<String>()

    ConfirmActionDialog(
        state = state,
        onConfirm = { input -> },
        onDismiss = { input -> }
    ) { input ->
        VersionDialog(dismiss = state::dismiss, confirm = state::confirm)
    }

    SaneScaffoldSettingsPage(headline = stringResource(id = R.string.about), onBackPressed = onBackPressed) {
        if (LinkSheetAppConfig.showDonationBanner()) {
            item(key = R.string.donate, contentType = ContentType.SingleGroupItem) {
                DefaultTwoLineIconClickableShapeListItem(
                    headlineContent = textContent(R.string.donate),
                    supportingContent = annotatedStringResource(R.string.donate_subtitle),
                    icon = Icons.Outlined.AutoAwesome.iconPainter,
                    onClick = { uriHandler.openUri(BuildConfig.LINK_BUY_ME_A_COFFEE) }
                )
            }
        }

        divider(id = R.string.links)

        group(size = 3) {
            item(key = R.string.credits) { padding, shape ->
                DefaultTwoLineIconClickableShapeListItem(
                    shape = shape,
                    padding = padding,
                    headlineContent = textContent(R.string.credits),
                    supportingContent = textContent(R.string.credits_explainer),
                    icon = Icons.Outlined.Link.iconPainter,
                    onClick = { navigate(creditsSettingsRoute) }
                )
            }

            item(key = R.string.github) { padding, shape ->
                DefaultTwoLineIconClickableShapeListItem(
                    shape = shape,
                    padding = padding,
                    headlineContent = textContent(R.string.github),
                    supportingContent = textContent(R.string.github_explainer),
                    icon = Icons.Outlined.Code.iconPainter,
                    onClick = { uriHandler.openUri(linksheetGithub) }
                )
            }

            item(key = R.string.discord) { padding, shape ->
                DefaultTwoLineIconClickableShapeListItem(
                    shape = shape,
                    padding = padding,
                    headlineContent = textContent(R.string.discord),
                    supportingContent = textContent(R.string.discord_explainer),
                    icon = Icons.Outlined.Forum.iconPainter,
                    onClick = { uriHandler.openUri(BuildConfig.LINK_DISCORD) }
                )
            }
        }

        divider(id = R.string.build_info)

        item(key = R.string.app_name, contentType = ContentType.SingleGroupItem) {
            DefaultTwoLineIconClickableShapeListItem(
                headlineContent = textContent(R.string.version),
                supportingContent = buildAnnotatedTextContent {
                    appendBuildInfo(R.string.built_at, AppInfo.buildInfo.builtAt)
                    appendBuildInfo(R.string.version_name, AppInfo.buildInfo.versionName)
                    appendBuildInfo(
                        R.string.flavor,
                        AppInfo.buildInfo.flavor,
                        AppInfo.buildInfo.workflowId != null
                    )

                    if (AppInfo.buildInfo.workflowId != null) {
                        appendBuildInfo(id = R.string.github_workflow_run_id, AppInfo.buildInfo.workflowId, false)
                    }
                },
                icon = Icons.Outlined.Build.iconPainter,
                onClick = {
//                    state.open("")

                    if (devClicks == 0) {
                        interaction.copy(viewModel.getBuildInfo(), FeedbackType.LongPress)
                    }

                    if (devClicks == 7 && !viewModel.devModeEnabled()) {
                        viewModel.devModeEnabled(true)
                        activity.showToast(R.string.dev_mode_enabled, Toast.LENGTH_SHORT)
                    }

                    devClicks++
                }
            )
        }

        divider(id = R.string.settings_about__divider_external_versions)

        group(array = NewAboutSettingsRouteData.externalVersions) { data, padding, shape ->
            ExternalVersionListItem(
                shape = shape,
                padding = padding,
                data = data,
                timestamp = data.additional!!
            )
        }
    }
}

@Composable
private fun AnnotatedString.Builder.appendBuildInfo(
    @StringRes id: Int,
    parameter: String,
    newLine: Boolean = true,
): AnnotatedString.Builder {
    val info = buildAnnotatedString {
        fromStringRes(id, parameter)
        if (newLine) appendLine()
    }

    append(info)
    return this
}

@Composable
private fun ExternalVersionListItem(shape: Shape, padding: PaddingValues, data: ListItemData<Long>, timestamp: Long) {
    val interaction = LocalHapticFeedbackInteraction.current

    val formatted = remember(timestamp) {
        timestamp.unixMillisUtc.format(ISO8601DateTimeFormatter.DefaultFormat)
    }

    DefaultTwoLineIconClickableShapeListItem(
        shape = shape,
        padding = padding,
        headlineContent = data.headlineContent,
        supportingContent = annotatedStringResource(R.string.last_updated, formatted),
        icon = data.icon,
        onClick = {
            interaction.copy(formatted, FeedbackType.LongPress)
        }
    )
}
