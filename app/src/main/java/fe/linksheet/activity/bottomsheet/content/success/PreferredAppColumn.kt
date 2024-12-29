package fe.linksheet.activity.bottomsheet.content.success

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fe.linksheet.activity.bottomsheet.ClickModifier
import fe.linksheet.activity.bottomsheet.ClickType
import fe.linksheet.activity.bottomsheet.content.success.appcontent.AppListItem
import fe.linksheet.module.resolver.KnownBrowser
import fe.linksheet.resolver.DisplayActivityInfo


@Composable
fun PreferredAppColumn(
    appInfo: DisplayActivityInfo,
    privateBrowser: KnownBrowser?,
    preferred: Boolean,
    showPackage: Boolean,
    hideBottomSheetChoiceButtons: Boolean,
    onClick: (ClickType, ClickModifier) -> Unit,
) {
    Column {
        AppListItem(
            appInfo = appInfo,
            selected = false,
            onClick = onClick,
            preferred = preferred,
            privateBrowser = privateBrowser,
            showPackage = showPackage,
        )

        Spacer(modifier = Modifier.height(5.dp))

        if (!hideBottomSheetChoiceButtons) {
            ChoiceButtons(choiceClick = onClick)
        }
    }
}
