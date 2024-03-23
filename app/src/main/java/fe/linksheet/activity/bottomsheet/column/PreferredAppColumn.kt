package fe.linksheet.activity.bottomsheet.column

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fe.linksheet.R
import fe.linksheet.activity.bottomsheet.LaunchApp
import fe.linksheet.activity.bottomsheet.button.ChoiceButtons
import fe.linksheet.extension.compose.currentActivity
import fe.linksheet.module.resolver.KnownBrowser
import fe.linksheet.module.viewmodel.BottomSheetViewModel
import fe.linksheet.resolver.BottomSheetResult
import fe.linksheet.resolver.DisplayActivityInfo
import fe.linksheet.ui.HkGroteskFontFamily


@Composable
fun PreferredAppColumn(
    appInfo: DisplayActivityInfo,
    privateBrowser: KnownBrowser?,
    preferred: Boolean,
    // TODO: Refactor bottomsheet away
    bottomSheetViewModel: BottomSheetViewModel,
    showPackage: Boolean,
    hideBottomSheetChoiceButtons: Boolean,
    onClick: (ClickType) -> Unit
) {
    val activity = LocalContext.currentActivity()

    Column {
        ListBrowserColumn(
            appInfo = appInfo,
            selected = false,
            onClick = onClick,
            preferred = preferred,
            privateBrowser = privateBrowser,
            showPackage = showPackage,
//            launchApp = launchApp,
        )

        Spacer(modifier = Modifier.height(5.dp))

        val result = bottomSheetViewModel.resolveResult as? BottomSheetResult.BottomSheetSuccessResult
        if (!hideBottomSheetChoiceButtons && result != null) {
            ChoiceButtons(
                result = result,
                useTextShareCopyButtons = bottomSheetViewModel.useTextShareCopyButtons(),
                openSettings = { bottomSheetViewModel.startMainActivity(activity) },
                choiceClick = onClick,
            )
        }

//        ButtonColumn(
//            bottomSheetViewModel = bottomSheetViewModel,
//            enabled = true,
//            resources = resources,
//            onClick = { launchApp(appInfo, it, false) },
//            hideDrawer = hideDrawer,
//            ignoreLibRedirectClick = ignoreLibRedirectClick,
//            showToast = showToast
//        )


    }
}
