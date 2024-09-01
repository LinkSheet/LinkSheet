package fe.linksheet.activity.bottomsheet.column

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fe.linksheet.activity.bottomsheet.button.ChoiceButtons
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
        ListBrowserColumn(
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
