package fe.linksheet.activity.bottomsheet.content.success

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import fe.linksheet.activity.bottomsheet.ClickModifier
import fe.linksheet.activity.bottomsheet.ClickType
import fe.linksheet.activity.bottomsheet.content.success.appcontent.AppListItem
import fe.linksheet.module.app.ActivityAppInfo
import fe.linksheet.module.resolver.KnownBrowser


@Composable
fun PreferredAppColumn(
    appInfo: ActivityAppInfo,
    privateBrowser: KnownBrowser?,
    preferred: Boolean,
    showPackage: Boolean,
    hideBottomSheetChoiceButtons: Boolean,
    onClick: (ClickType, ClickModifier) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        AppListItem(
            appInfo = appInfo,
            selected = false,
            onClick = onClick,
            preferred = preferred,
            privateBrowser = privateBrowser,
            showPackage = showPackage,
        )

        if (!hideBottomSheetChoiceButtons) {
            ChoiceButtons(choiceClick = onClick)
        }
    }
}
