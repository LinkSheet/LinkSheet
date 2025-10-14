package app.linksheet.feature.shizuku.ui

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.InstallMobile
import androidx.compose.material.icons.rounded.NotStarted
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.linksheet.compose.page.SaneScaffoldSettingsPage
import app.linksheet.compose.preview.PreviewTheme
import app.linksheet.feature.shizuku.R
import app.linksheet.feature.shizuku.ShizukuDownload
import app.linksheet.feature.shizuku.viewmodel.ShizukuSettingsViewModel
import app.linksheet.feature.shizuku.viewmodel.ShizukuStatus
import fe.android.compose.icon.iconPainter
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.android.compose.text.TextContent
import fe.android.compose.text.TextOptions
import fe.composekit.component.ContentType
import fe.composekit.component.PreviewThemeNew
import fe.composekit.component.card.AlertCard
import fe.composekit.component.icon.IconOffset
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.component.list.item.type.SwitchListItem
import fe.composekit.layout.column.SaneLazyListScope
import fe.composekit.lifecycle.collectRefreshableAsStateWithLifecycle
import fe.composekit.preference.collectAsStateWithLifecycle
import fe.std.coroutines.BaseRefreshableFlow
import org.koin.androidx.compose.koinViewModel
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Composable
public fun <T> BaseRefreshableFlow<T>.collectRefreshableAsStateWithLifecycle2(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext,
): State<T> {
    val initialValue = remember { value }
    return collectRefreshableAsStateWithLifecycle(
        initialValue = initialValue,
        lifecycle = lifecycleOwner.lifecycle,
        minActiveState = minActiveState,
        context = context
    )
}
@Composable
fun ShizukuRoute(
    onBackPressed: () -> Unit,
    viewModel: ShizukuSettingsViewModel = koinViewModel(),
) {
    val activity = LocalActivity.current

    val status by viewModel.status.collectRefreshableAsStateWithLifecycle2(
        minActiveState = Lifecycle.State.RESUMED,
    )

    val enableShizuku by viewModel.enableShizuku.collectAsStateWithLifecycle()
    ShizukuRouteInternal(
        status = status,
        openManager = {
            viewModel.startManager(activity)
        },
        requestPermission = viewModel::requestPermission,
        onBackPressed = onBackPressed,
        enabled = enableShizuku,
        onEnabledChange = viewModel.enableShizuku
    )
}

@Composable
private fun ShizukuRouteInternal(
    status: ShizukuStatus,
    openManager: () -> Unit,
    requestPermission: () -> Unit,
    onBackPressed: () -> Unit,
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit
) {
    SaneScaffoldSettingsPage(
        headline = stringResource(id = R.string.settings_shizuku__title_shizuku),
        onBackPressed = onBackPressed
    ) {
        when {
            !status.installed -> notInstalled()
            !status.running -> notRunning(openManager = openManager)
            !status.permission -> noPermission(requestPermission = requestPermission)
            else -> {
                toggle(enabled = enabled, onEnabledChange = onEnabledChange)
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        item(key = R.string.settings_shizuku__text_info) {
            InfoText(textContent = textContent(R.string.settings_shizuku__text_info))
        }
    }
}

private fun SaneLazyListScope.toggle(enabled: Boolean, onEnabledChange: (Boolean) -> Unit) {
    item(key = R.string.settings_shizuku__title_enable, contentType = ContentType.SingleGroupItem) {
        SwitchListItem(
            checked = enabled,
            onCheckedChange = onEnabledChange,
            position = ContentPosition.Trailing,
            headlineContent = textContent(R.string.settings_shizuku__title_enable),
//            supportingContent = textContent(R.string.settings_shizuku__text),
        )
    }
}

private fun SaneLazyListScope.notInstalled() {
    item(
        key = R.string.settings_shizuku__title_not_installed,
        contentType = ContentType.SingleGroupItem
    ) {
        val uriHandler = LocalUriHandler.current
        AlertCard(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            icon = Icons.Rounded.InstallMobile.iconPainter,
            iconOffset = IconOffset(y = (-1).dp),
            iconContentDescription = stringResource(id = R.string.settings_shizuku__title_not_installed),
            headline = textContent(R.string.settings_shizuku__title_not_installed),
            subtitle = textContent(R.string.settings_shizuku__text_not_installed),
            onClick = {
                uriHandler.openUri(ShizukuDownload)
            }
        )
    }


}

private fun SaneLazyListScope.notRunning(openManager: () -> Unit) {
    item(
        key = R.string.settings_shizuku__title_not_running,
        contentType = ContentType.SingleGroupItem
    ) {
        AlertCard(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            icon = Icons.Rounded.NotStarted.iconPainter,
            iconOffset = IconOffset(y = (-1).dp),
            iconContentDescription = stringResource(id = R.string.settings_shizuku__title_not_running),
            headline = textContent(R.string.settings_shizuku__title_not_running),
            subtitle = textContent(R.string.settings_shizuku__text_not_running),
            onClick = openManager
        )
    }
}

private fun SaneLazyListScope.noPermission(requestPermission: () -> Unit) {
    item(
        key = R.string.settings_shizuku__title_missing_permission,
        contentType = ContentType.SingleGroupItem
    ) {
        AlertCard(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            icon = Icons.Rounded.WarningAmber.iconPainter,
            iconOffset = IconOffset(y = (-1).dp),
            iconContentDescription = stringResource(id = R.string.settings_shizuku__title_missing_permission),
            headline = textContent(R.string.settings_shizuku__title_missing_permission),
            subtitle = textContent(R.string.settings_shizuku__text_missing_permission),
            onClick = requestPermission
        )
    }
}

@Preview
@Composable
private fun ShizukuRouteInternalPreview() {
    PreviewThemeNew {
//        ShizukuRouteInternal(onBackPressed = {})
    }
}

@Preview
@Composable
private fun ShizukuRouteNotInstalledPreview() {
    ShizukuPreviewBase(status = ShizukuStatus(installed = false, permission = false, running = false))
}

@Preview
@Composable
private fun ShizukuRouteNoPermissionPreview() {
    ShizukuPreviewBase(status = ShizukuStatus(installed = true, permission = false, running = true))
}

@Preview
@Composable
private fun ShizukuRouteNotRunningPreview() {
    ShizukuPreviewBase(status = ShizukuStatus(installed = true, permission = true, running = false))
}

@Composable
private fun ShizukuPreviewBase(status: ShizukuStatus) {
    PreviewTheme {
        ShizukuRouteInternal(
            status = status,
            openManager = {},
            requestPermission = {},
            onBackPressed = {},
            enabled = true,
            onEnabledChange = {}
        )
    }
}

@Composable
private fun InfoText(textContent: TextContent) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Icon(imageVector = Icons.Outlined.Info, contentDescription = null)

        TextContent(
            textContent = textContent,
            textOptions = TextOptions(style = MaterialTheme.typography.bodySmall),
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun InfoTextPreview() {

}
