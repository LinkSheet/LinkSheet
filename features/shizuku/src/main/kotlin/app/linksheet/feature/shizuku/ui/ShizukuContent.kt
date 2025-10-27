package app.linksheet.feature.shizuku.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import app.linksheet.compose.preview.PreviewTheme
import app.linksheet.feature.shizuku.R
import app.linksheet.feature.shizuku.ShizukuStatus
import fe.android.compose.text.StringResourceContent.Companion.textContent
import fe.composekit.component.ContentType
import fe.composekit.component.list.item.ContentPosition
import fe.composekit.component.list.item.type.SwitchListItem
import fe.composekit.layout.column.SaneLazyListScope
import fe.composekit.preference.BooleanVmPref
import fe.composekit.preference.collectAsStateWithLifecycle

internal fun SaneLazyListScope.content(
    enableShizukuPref: BooleanVmPref,
    autoDisableLinkHandlingPref: BooleanVmPref
) {
    item(key = R.string.settings_shizuku__title_enable, contentType = ContentType.SingleGroupItem) {
        val enableShizuku by enableShizukuPref.collectAsStateWithLifecycle()
        SwitchListItem(
            checked = enableShizuku,
            onCheckedChange = enableShizukuPref,
            position = ContentPosition.Trailing,
            headlineContent = textContent(R.string.settings_shizuku__title_enable),
//            supportingContent = textContent(R.string.settings_shizuku__text),
        )
    }

    divider(id = R.string.settings_shizuku__divider_settings)

    item(key = R.string.settings_shizuku__title_auto_disable_link_handling, contentType = ContentType.SingleGroupItem) {
        val autoDisableLinkHandling by autoDisableLinkHandlingPref.collectAsStateWithLifecycle()
        SwitchListItem(
            checked = autoDisableLinkHandling,
            onCheckedChange = autoDisableLinkHandlingPref,
            position = ContentPosition.Trailing,
            headlineContent = textContent(R.string.settings_shizuku__title_auto_disable_link_handling),
            supportingContent = textContent(R.string.settings_shizuku__text_auto_disable_link_handling),
        )
    }
}

@Preview
@Composable
private fun ShizukuRouteNotRunningPreview() {
//    ShizukuPreviewBase(status = ShizukuStatus(installed = true, permission = true, running = false))
}

@Composable
private fun ShizukuContentPreviewBase(status: ShizukuStatus) {
    PreviewTheme {

    }
}
