package fe.linksheet.activity.bottomsheet.content.success

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.linksheet.preview.PreviewContainer
import app.linksheet.testing.fake.PackageInfoFakes
import app.linksheet.testing.fake.toActivityAppInfo
import fe.linksheet.activity.bottomsheet.ClickModifier
import fe.linksheet.activity.bottomsheet.ClickType
import fe.linksheet.activity.bottomsheet.content.success.appcontent.AppListItem
import fe.linksheet.feature.app.ActivityAppInfo
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
    Column(
        modifier = Modifier.height(IntrinsicSize.Min),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
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

@Preview(showBackground = true)
@Composable
private fun PreferredAppColumnPreview() {
    PreviewContainer {
        PreferredAppColumn(
            appInfo = PackageInfoFakes.Dummy.toActivityAppInfo(),
            privateBrowser = null,
            preferred = true,
            showPackage = false,
            hideBottomSheetChoiceButtons = false,
            onClick = { _, _ ->

            }
        )
    }
}
