package fe.linksheet.composable.page.settings.apps

import android.content.res.Resources
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import fe.android.compose.content.rememberOptionalContent
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.ComposableTextContent.Companion.content
import fe.android.compose.text.DefaultContent.Companion.text
import fe.composekit.component.list.column.shape.ClickableShapeListItem
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.component.list.item.ListItemFilledIconButton
import fe.linksheet.R
import fe.linksheet.composable.component.appinfo.AppInfoIcon
import fe.linksheet.module.app.DomainVerificationAppInfo
import fe.linksheet.module.app.LinkHandling

@Composable
fun VerifiedAppListItem(
    item: DomainVerificationAppInfo,
    padding: PaddingValues,
    shape: Shape,
    onClick: () -> Unit,
    onOtherClick: () -> Unit,
) {
    ClickableShapeListItem(
        padding = padding,
        shape = shape,
        position = ContentPosition.Leading,
        onClick = onClick,
        headlineContent = text(item.label),
        supportingContent = content {
            ItemContent(appInfo = item)
        },
        primaryContent = {
            AppInfoIcon(appInfo = item)
        },
        otherContent = rememberOptionalContent(item.linkHandling != LinkHandling.Unsupported) {
            ListItemFilledIconButton(
                iconPainter = Icons.Outlined.Settings.iconPainter,
                contentDescription = stringResource(id = R.string.settings),
                onClick = onOtherClick
            )
        }
    )
}

@Composable
private fun ItemContent(appInfo: DomainVerificationAppInfo) {
    Column {
        Text(text = appInfo.packageName, overflow = TextOverflow.Ellipsis, maxLines = 1)

        if (appInfo.linkHandling != LinkHandling.Unsupported) {
            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = buildHostStateText(
                    appInfo.hostSum,
                    stringVerified to appInfo.stateVerified,
                    stringSelected to appInfo.stateSelected,
                    stringNone to appInfo.stateNone,
                )
            )

            Text(
                text = stringResource(
                    id = if (appInfo.linkHandling == LinkHandling.Allowed) R.string.settings_verified_link_handlers__text_link_handling_allowed_true
                    else R.string.settings_verified_link_handlers__text_link_handling_allowed_false
                )
            )
        }
    }
}

private val stringVerified = DefaultAltStringRes(
    R.plurals.settings_verified_link_handlers__text_app_host_info_verified,
    R.plurals.settings_verified_link_handlers__text_app_host_info_verified_alt
)

private val stringSelected = DefaultAltStringRes(
    R.plurals.settings_verified_link_handlers__text_app_host_info_selected,
    R.plurals.settings_verified_link_handlers__text_app_host_info_selected_alt
)

private val stringNone = DefaultAltStringRes(
    R.plurals.settings_verified_link_handlers__text_app_host_info_none,
    R.plurals.settings_verified_link_handlers__text_app_host_info_none_alt
)


@Stable
data class DefaultAltStringRes(
    val default: Int,
    val alt: Int,
) {
    fun format(resources: Resources, single: Boolean, list: List<*>): String {
        val res = if (single) alt else default
        return resources.getQuantityString(res, list.size, list.size)
    }
}

@Composable
private fun buildHostStateText(sum: Int, vararg states: Pair<DefaultAltStringRes, List<String>>): String {
    val resources = LocalContext.current.resources

    var hasSingleState: Boolean
    val strings = states
        .filter { (_, hosts) -> hosts.isNotEmpty() }
        .also { hasSingleState = it.size == 1 }
        .map { (res, hosts) -> res.format(resources, hasSingleState, hosts) }

    if (hasSingleState) {
        return strings.single()
    }

    return pluralStringResource(
        id = R.plurals.settings_verified_link_handlers__text_app_host_info,
        count = sum,
        sum, strings.joinToString(separator = ", ")
    )
}

private class DomainVerificationAppInfoProvider() : PreviewParameterProvider<List<DomainVerificationAppInfo>> {
    override val values: Sequence<List<DomainVerificationAppInfo>> = sequenceOf(
        listOf(
            DomainVerificationAppInfo(
                packageName = "fe.linksheet",
                label = "LinkSheet",
                flags = 0,
                linkHandling = LinkHandling.Allowed,
                stateNone = mutableStateListOf(),
                stateSelected = mutableStateListOf(),
                stateVerified = mutableStateListOf(),
            ),
            DomainVerificationAppInfo(
                packageName = "fe.linksheet",
                label = "LinkSheet",
                flags = 0,
                linkHandling = LinkHandling.Allowed,
                stateNone = mutableStateListOf(),
                stateSelected = mutableStateListOf("google.com"),
                stateVerified = mutableStateListOf("google.com"),
            ),
            DomainVerificationAppInfo(
                packageName = "fe.linksheet",
                label = "LinkSheet",
                flags = 0,
                linkHandling = LinkHandling.Allowed,
                stateNone = mutableStateListOf("facebook.com"),
                stateSelected = mutableStateListOf("google.com"),
                stateVerified = mutableStateListOf("google.com"),
            ),
            DomainVerificationAppInfo(
                packageName = "fe.linksheet",
                label = "LinkSheet",
                flags = 0,
                linkHandling = LinkHandling.Allowed,
                stateNone = mutableStateListOf("facebook.com"),
                stateSelected = mutableStateListOf(),
                stateVerified = mutableStateListOf("google.com"),
            ),
            DomainVerificationAppInfo(
                packageName = "fe.linksheet",
                label = "LinkSheet",
                flags = 0,
                linkHandling = LinkHandling.Allowed,
                stateNone = mutableStateListOf("facebook.com"),
                stateSelected = mutableStateListOf(),
                stateVerified = mutableStateListOf(),
            ),
            DomainVerificationAppInfo(
                packageName = "fe.linksheet",
                label = "LinkSheet",
                flags = 0,
                linkHandling = LinkHandling.Disallowed,
                stateNone = mutableStateListOf(),
                stateSelected = mutableStateListOf("facebook.com"),
                stateVerified = mutableStateListOf(),
            )
        ),
        listOf(
            DomainVerificationAppInfo(
                packageName = "fe.linksheet",
                label = "LinkSheet",
                flags = 0,
                linkHandling = LinkHandling.Unsupported,
                stateNone = mutableStateListOf(),
                stateSelected = mutableStateListOf(),
                stateVerified = mutableStateListOf(),
            ),
            DomainVerificationAppInfo(
                packageName = "fe.linksheet",
                label = "LinkSheet",
                flags = 0,
                linkHandling = LinkHandling.Unsupported,
                stateNone = mutableStateListOf("google.com"),
                stateSelected = mutableStateListOf(),
                stateVerified = mutableStateListOf(),
            )
        )
    )
}

@Composable
@Preview
private fun VerifiedAppListItemPreview(
    @PreviewParameter(DomainVerificationAppInfoProvider::class) items: List<DomainVerificationAppInfo>,
) {
    Column {
        for (item in items) {
            VerifiedAppListItem(
                item = item,
                padding = PaddingValues(),
                shape = RoundedCornerShape(0),
                onClick = {},
                onOtherClick = {}
            )
        }
    }
}
