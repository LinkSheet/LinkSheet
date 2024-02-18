package fe.linksheet.activity.bottomsheet.dev.column

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
import fe.linksheet.activity.bottomsheet.dev.LaunchApp
import fe.linksheet.activity.bottomsheet.dev.button.ChoiceButtons
import fe.linksheet.extension.compose.currentActivity
import fe.linksheet.module.viewmodel.BottomSheetViewModel
import fe.linksheet.resolver.BottomSheetResult
import fe.linksheet.resolver.DisplayActivityInfo
import fe.linksheet.ui.HkGroteskFontFamily
import fe.linksheet.util.PrivateBrowsingBrowser


@Composable
fun PreferredAppColumn(
    appInfo: DisplayActivityInfo,
    privateBrowser: PrivateBrowsingBrowser?,
    preferred: Boolean,
    // TODO: Refactor bottomsheet away
    bottomSheetViewModel: BottomSheetViewModel,
    showPackage: Boolean,
    hideBottomSheetChoiceButtons: Boolean,
    launchApp: LaunchApp,
) {
    val activity = LocalContext.currentActivity()

    Column {
        ListBrowserColumn(
            appInfo = appInfo,
            selected = false,
            onClick = {},
            preferred = preferred,
            privateBrowser = privateBrowser,
            showPackage = showPackage,
            launchApp = launchApp,
        )

        Spacer(modifier = Modifier.height(5.dp))

        val result = bottomSheetViewModel.resolveResult as? BottomSheetResult.BottomSheetSuccessResult
        if (!hideBottomSheetChoiceButtons && result != null) {
            ChoiceButtons(
                result = result,
                useTextShareCopyButtons = bottomSheetViewModel.useTextShareCopyButtons(),
                openSettings = { bottomSheetViewModel.startMainActivity(activity) },
                choiceClick = { launchApp(appInfo, it, false) },
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

        // TODO: Not sure if this divider should be kept

        HorizontalDivider(
            modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 5.dp, bottom = 10.dp),
            color = MaterialTheme.colorScheme.outline.copy(0.25f)
        )

        Text(
            modifier = Modifier.padding(start = 15.dp),
            text = stringResource(id = R.string.use_a_different_app),
            fontFamily = HkGroteskFontFamily,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(10.dp))
    }
}
