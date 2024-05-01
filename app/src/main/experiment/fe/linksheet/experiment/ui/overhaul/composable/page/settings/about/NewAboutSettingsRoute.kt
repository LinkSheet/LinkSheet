package fe.linksheet.experiment.ui.overhaul.composable.page.settings.about

import ClearURLsMetadata
import LibRedirectMetadata
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import fe.fastforwardkt.FastForwardRules
import fe.kotlin.extension.primitive.unixMillisUtc
import fe.kotlin.time.ISO8601DateTimeFormatter
import fe.linksheet.BuildConfig
import fe.linksheet.R
import fe.linksheet.creditsSettingsRoute
import fe.linksheet.experiment.ui.overhaul.composable.ContentTypeDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.default.DefaultTwoLineIconClickableShapeListItem
import fe.linksheet.experiment.ui.overhaul.composable.component.page.ListItemData
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneScaffoldSettingsPage
import fe.linksheet.experiment.ui.overhaul.composable.component.page.layout.group
import fe.linksheet.experiment.ui.overhaul.composable.util.Annotated.Companion.buildAnnotatedTextContent
import fe.linksheet.experiment.ui.overhaul.composable.util.AnnotatedStringResource.Companion.annotated
import fe.linksheet.experiment.ui.overhaul.composable.util.ImageVectorIconType.Companion.vector
import fe.linksheet.experiment.ui.overhaul.composable.util.Resource.Companion.textContent
import fe.linksheet.experiment.ui.overhaul.composable.util.annotatedStringResource
import fe.linksheet.experiment.ui.overhaul.interaction.LocalHapticFeedbackInteraction
import fe.linksheet.linksheetGithub
import fe.linksheet.module.viewmodel.AboutSettingsViewModel
import fe.linksheet.ui.LocalActivity
import fe.linksheet.util.AppInfo
import fe.linksheet.util.AppSignature
import org.koin.androidx.compose.koinViewModel


object NewAboutSettingsRouteData {
    val externalVersions = arrayOf(
        ListItemData(
            vector(Icons.Outlined.ClearAll),
            textContent(R.string.clear_urls_version),
            additional = ClearURLsMetadata.fetchedAt
        ),
        ListItemData(
            vector(Icons.Outlined.Bolt),
            textContent(R.string.fastforward_version),
            additional = FastForwardRules.fetchedAt
        ),
        ListItemData(
            vector(Icons.Outlined.Security),
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
    val buildDate =
        BuildConfig.BUILT_AT.unixMillisUtc.format(ISO8601DateTimeFormatter.DefaultFormat)
    val buildType = AppSignature.checkSignature(activity)

    var devClicks by remember { mutableIntStateOf(0) }
    val interaction = LocalHapticFeedbackInteraction.current

    SaneScaffoldSettingsPage(headline = stringResource(id = R.string.about), onBackPressed = onBackPressed) {
        item(key = R.string.donate, contentType = ContentTypeDefaults.SingleGroupItem) {
            DefaultTwoLineIconClickableShapeListItem(
                headlineContent = textContent(R.string.donate),
                supportingContent = textContent(R.string.donate_subtitle),
                icon = vector(Icons.Outlined.AutoAwesome),
                onClick = { uriHandler.openUri(BuildConfig.LINK_BUY_ME_A_COFFEE) }
            )
        }


        divider(stringRes = R.string.links)

        group(size = 3) {
            item(key = R.string.credits) { padding, shape ->
                DefaultTwoLineIconClickableShapeListItem(
                    shape = shape,
                    padding = padding,
                    headlineContent = textContent(R.string.credits),
                    supportingContent = textContent(R.string.credits_explainer),
                    icon = vector(Icons.Outlined.Link),
                    onClick = { navigate(creditsSettingsRoute) }
                )
            }

            item(key = R.string.github) { padding, shape ->
                DefaultTwoLineIconClickableShapeListItem(
                    shape = shape,
                    padding = padding,
                    headlineContent = textContent(R.string.github),
                    supportingContent = textContent(R.string.github_explainer),
                    icon = vector(Icons.Outlined.Code),
                    onClick = { uriHandler.openUri(linksheetGithub) }
                )
            }

            item(key = R.string.discord) { padding, shape ->
                DefaultTwoLineIconClickableShapeListItem(
                    shape = shape,
                    padding = padding,
                    headlineContent = textContent(R.string.discord),
                    supportingContent = textContent(R.string.discord_explainer),
                    icon = vector(Icons.Outlined.Forum),
                    onClick = { uriHandler.openUri(BuildConfig.LINK_DISCORD) }
                )
            }
        }

        divider(stringRes = R.string.build_info)

        item(key = R.string.app_name, contentType = ContentTypeDefaults.SingleGroupItem) {
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
                icon = vector(Icons.Outlined.Build),
                onClick = {
                    interaction.copy(viewModel.getBuildInfo(), HapticFeedbackType.LongPress)
//                        if (devClicks == 7 && !viewModel.devModeEnabled()) {
//                            viewModel.devModeEnabled(true)
//                            activity.showToast(R.string.dev_mode_enabled, Toast.LENGTH_SHORT)
//                        }
//
//                        devClicks++
                }
            )
        }

        divider(stringRes = R.string.settings_about__divider_external_versions)

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
    newLine: Boolean = true
): AnnotatedString.Builder {
    val info = buildAnnotatedString {
        annotatedStringResource(id, parameter)
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
        supportingContent = annotated(R.string.last_updated, formatted),
        icon = data.icon,
        onClick = {
            interaction.copy(formatted, HapticFeedbackType.LongPress)
        }
    )
}
