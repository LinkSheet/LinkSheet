package fe.linksheet.composable.page.settings.apps.verifiedlinkhandlers

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.linksheet.compose.page.SaneScaffoldSettingsPage
import app.linksheet.compose.preview.PreviewContainer
import app.linksheet.feature.app.core.DomainVerificationAppInfo
import app.linksheet.feature.app.core.LinkHandling
import app.linksheet.testing.fake.PackageInfoFakes
import app.linksheet.testing.fake.toDomainVerificationAppInfo
import fe.android.compose.icon.BitmapIconPainter
import fe.android.compose.icon.IconPainter
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.DefaultContent.Companion.text
import fe.android.compose.text.ProvideContentColorOptionsStyleText
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.android.compose.text.TextContent
import fe.composekit.component.ContentType
import fe.composekit.component.dialog.DialogDefaults
import fe.composekit.component.list.column.SaneLazyColumnDefaults
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.component.list.item.type.CheckboxListItem
import fe.composekit.component.shape.CustomShapeDefaults
import fe.composekit.layout.column.group
import fe.kotlin.extension.iterable.mapToSet
import fe.linksheet.R
import app.linksheet.feature.app.ui.AppInfoIcon
import fe.linksheet.composable.dialog.DomainVerificationDialogData
import fe.linksheet.composable.dialog.createState
import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.module.viewmodel.VerifiedLinkHandlerViewModel
import app.linksheet.compose.util.drawBitmap
import fe.linksheet.util.extension.android.tryStartActivity
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun VlhAppRoute(
    packageName: String,
    onBackPressed: () -> Unit,
    viewModel: VerifiedLinkHandlerViewModel = koinViewModel(parameters = { parametersOf(packageName) }),
) {
    val appInfo = viewModel.get(packageName)
    val preferredApps by viewModel.getPreferredAppsFlow(packageName).collectAsStateWithLifecycle(emptyList())

    val activity = LocalActivity.current
    if (appInfo != null) {
        VlhAppRouteInternal(
            onBackPressed = onBackPressed,
            appInfo = appInfo,
            preferredApps = preferredApps,
            openSettings = {
                activity?.tryStartActivity(viewModel.openSettings())
            }
        )
    }
}

@Composable
private fun VlhAppRouteInternal(
    onBackPressed: () -> Unit,
    appInfo: DomainVerificationAppInfo,
    preferredApps: List<PreferredApp>,
    openSettings: (String) -> Unit,
) {
    val data = DomainVerificationDialogData(appInfo, preferredApps.mapToSet { it.host })
    val mutableStates = remember(data) { data.createState() }
    val states = remember(data) { mutableStates.toMap() }

    SaneScaffoldSettingsPage(
        headline = stringResource(id = R.string.settings_verified_link_handler__title_details),
        onBackPressed = onBackPressed
    ) {
        item(key = 1, contentType = ContentType.SingleGroupItem) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppInfoIcon(appInfo = appInfo)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = appInfo.label)
                    Text(text = appInfo.packageName, overflow = TextOverflow.Ellipsis, maxLines = 1)
                }
            }
        }

        item(key = 2, contentType = ContentType.Divider) {
            Spacer(modifier = Modifier.height(SaneLazyColumnDefaults.VerticalSpacing))
        }

        item(key = 3, contentType = ContentType.SingleGroupItem) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CustomShapeDefaults.SingleShape)
            ) {
                VlhButton(
                    textContent = textContent(R.string.generic__button_text_settings),
                    iconPainter = Icons.Rounded.Settings.iconPainter,
                    weight = 1 / 3f,
                    shape = RoundedCornerShape(25, 0, 0, 50),
                    onClick = {}
                )
                VlhButton(
                    textContent = textContent(R.string.generic__button_text_settings),
                    iconPainter = Icons.Rounded.Settings.iconPainter,
                    weight = 1 / 3f,
                    shape = RoundedCornerShape(0),
                    onClick = {}
                )
                VlhButton(
                    textContent = textContent(R.string.generic__button_text_settings),
                    iconPainter = Icons.Rounded.Settings.iconPainter,
                    weight = 1 / 3f,
                    shape = RoundedCornerShape(0, 50, 25, 0),
                    onClick = { openSettings(appInfo.packageName) }
                )
            }
        }

        divider(id = R.string.settings_verified_link_handler__text_hosts)

        group(list = states.keys.toList(), key = { it }) { host, padding, shape ->
//            val padding = DialogDefaults.ListItemInnerPadding.copy(
//                vertical = 4.dp
//            )

            CheckboxListItem(
                padding = padding,
                shape = shape,
                checked = mutableStates[host]!!,
                onCheckedChange = {
                    mutableStates[host] = it
                },
                position = ContentPosition.Leading,
                headlineContent = text(host),
                otherContent = null,
                innerPadding = DialogDefaults.ListItemInnerPadding.copy(
                    vertical = 4.dp
                ),
                textOptions = DialogDefaults.ListItemTextOptions,
                colors = DialogDefaults.ListItemColors
            )
        }
    }
}

@Composable
private fun RowScope.VlhButton(
    textContent: TextContent,
    iconPainter: IconPainter,
    weight: Float,
    shape: Shape,
    onClick: (() -> Unit)? = null,
) {
    FilledTonalButton(
        modifier = Modifier.weight(weight),
        shape = shape,
        onClick = onClick ?: {}
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                painter = iconPainter.rememberPainter(),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            ProvideContentColorOptionsStyleText(
                contentColor = MaterialTheme.colorScheme.primary,
//                textOptions = TextOptions(style = MaterialTheme.typography.bodyMedium),
                content = textContent.content
            )
        }
    }
}


@Preview
@Composable
private fun VlhAppRouteInternalPreview() {
    val icon = drawBitmap(Size(24f, 24f)) { drawCircle(Color.Red) }
    VlhAppRouteInternalPreviewBase(
        app = PackageInfoFakes.Youtube.toDomainVerificationAppInfo(
            linkHandling = LinkHandling.Allowed,
            stateNone = mutableListOf(),
            stateSelected = mutableListOf(),
            stateVerified = mutableListOf(),
            icon = BitmapIconPainter.bitmap(icon)
        )
    )
}

@Composable
private fun VlhAppRouteInternalPreviewBase(app: DomainVerificationAppInfo) {
    PreviewContainer {
        VlhAppRouteInternal(
            onBackPressed = {},
            appInfo = app,
            preferredApps = emptyList(),
            openSettings = {

            },
        )
    }
}
