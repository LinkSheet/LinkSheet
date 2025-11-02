package fe.linksheet.composable.page.settings.about

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.android.compose.dialog.helper.confirm.ConfirmActionDialog
import fe.android.compose.dialog.helper.confirm.rememberConfirmActionDialog
import fe.android.compose.feedback.FeedbackType
import fe.android.compose.feedback.LocalHapticFeedbackInteraction
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.AnnotatedStringResourceContent.Companion.annotatedStringResource
import fe.android.compose.text.DefaultContent.Companion.text
import fe.android.span.helper.composable.fromStringRes
import fe.composekit.component.ContentType
import fe.composekit.component.card.AlertCard
import fe.composekit.component.list.column.group.ListItemData
import fe.composekit.component.list.item.default.DefaultTwoLineIconClickableShapeListItem
import fe.std.javatime.extension.unixMillisUtc
import fe.linksheet.BuildConfig
import fe.linksheet.R
import app.linksheet.compose.page.SaneScaffoldSettingsPage
import fe.linksheet.module.viewmodel.AboutSettingsViewModel
import app.linksheet.compose.theme.HkGroteskFontFamily
import fe.linksheet.util.buildconfig.LinkSheetInfo
import fe.std.javatime.time.ISO8601DateTimeFormatter
import org.koin.androidx.compose.koinViewModel


//object NewAboutSettingsRouteData {
//    val externalVersions = arrayOf(
//        ListItemData(
//            Icons.Outlined.ClearAll.iconPainter,
//            textContent(R.string.clear_urls_version),
//            additional = ClearURLsMetadata.fetchedAt
//        ),
//        ListItemData(
//            Icons.Outlined.Bolt.iconPainter,
//            textContent(R.string.fastforward_version),
//            additional = FastForwardRules.fetchedAt
//        ),
//        ListItemData(
//            Icons.Outlined.Security.iconPainter,
//            textContent(R.string.libredirect_version),
//            additional = LibRedirectMetadata.fetchedAt
//        )
//    )
//}

@Composable
fun VersionSettingsRoute(
    onBackPressed: () -> Unit,
    navigate: (String) -> Unit,
    viewModel: AboutSettingsViewModel = koinViewModel(),
) {
    val uriHandler = LocalUriHandler.current
    val buildDate = BuildConfig.BUILT_AT.unixMillisUtc.format(ISO8601DateTimeFormatter.FriendlyFormat)
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
        item {
//            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.app_linksheet),
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape),
                )

                Spacer(modifier = Modifier.height(10.dp))


                Text(
                    text = stringResource(id = R.string.app_name),
                    fontFamily = HkGroteskFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )

                Text(text = LinkSheetInfo.buildInfo.flavor)

                Text(text = LinkSheetInfo.buildInfo.versionName)
                Text(text = LinkSheetInfo.buildInfo.builtAt)
            }
//            }
        }

        item(key = R.string.app_name, contentType = ContentType.SingleGroupItem) {
            AlertCard(
                icon = Icons.Outlined.Bolt.iconPainter,
                iconContentDescription = null,
                headline = text("Build"),
                subtitle = text(LinkSheetInfo.buildInfo.versionName)
            )

//            DefaultTwoLineIconClickableShapeListItem(
//                headlineContent = textContent(R.string.version),
//                supportingContent = buildAnnotatedTextContent {
//                    appendBuildInfo(R.string.built_at, AppInfo.buildInfo.builtAt)
//                    appendBuildInfo(R.string.version_name, AppInfo.buildInfo.versionName)
//                    appendBuildInfo(
//                        R.string.flavor,
//                        AppInfo.buildInfo.flavor,
//                        AppInfo.buildInfo.workflowId != null
//                    )
//
//
//                    if (AppInfo.buildInfo.workflowId != null) {
//                        appendBuildInfo(id = R.string.github_workflow_run_id, AppInfo.buildInfo.workflowId, false)
//                    }
//                },
//                onClick = {
//                    state.open("")
//
//                    if (devClicks == 0) {
//                        interaction.copy(viewModel.getBuildInfo(), HapticFeedbackType.LongPress)
//                    }
//
//                    if (devClicks == 7 && !viewModel.devModeEnabled()) {
//                        viewModel.devModeEnabled(true)
//                        activity.showToast(R.string.dev_mode_enabled, Toast.LENGTH_SHORT)
//                    }
//
//                    devClicks++
//                }
//            )
        }

//        divider(id =  R.string.settings_about__divider_external_versions)
//
//        group(array = NewAboutSettingsRouteData.externalVersions) { data, padding, shape ->
//            ExternalVersionListItem(
//                shape = shape,
//                padding = padding,
//                data = data,
//                timestamp = data.additional!!
//            )
//        }
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
