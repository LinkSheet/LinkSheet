package fe.linksheet.activity.bottomsheet.dev.preferred

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fe.linksheet.R
import fe.linksheet.activity.bottomsheet.dev.OpenButtons
import fe.linksheet.activity.bottomsheet.dev.list.ListBrowserColumn
import fe.linksheet.module.resolver.LibRedirectResolver
import fe.linksheet.module.viewmodel.BottomSheetViewModel
import fe.linksheet.resolver.DisplayActivityInfo
import fe.linksheet.ui.HkGroteskFontFamily
import fe.linksheet.util.PrivateBrowsingBrowser


@Composable
fun PreferredAppColumn(
    appInfo: DisplayActivityInfo,
    privateBrowser: PrivateBrowsingBrowser?,
    preferred: Boolean,
    bottomSheetViewModel: BottomSheetViewModel,
    showPackage: Boolean,
    launchApp: (DisplayActivityInfo, Boolean, Boolean) -> Unit,
    libRedirectResult: LibRedirectResolver.LibRedirectResult.Redirected? = null,
    ignoreLibRedirectClick: ((LibRedirectResolver.LibRedirectResult.Redirected) -> Unit)? = null,
) {
    Column {
        ListBrowserColumn(
            appInfo = appInfo,
            preferred = preferred,
            privateBrowser = privateBrowser,
            showPackage = showPackage,
            launchApp = launchApp,
            libRedirectResult = libRedirectResult,
            ignoreLibRedirectClick = ignoreLibRedirectClick
        )

        Spacer(modifier = Modifier.height(5.dp))

        OpenButtons(
            bottomSheetViewModel = bottomSheetViewModel,
            onClick = { launchApp(appInfo, it, false) },
        )

//        ButtonColumn(
//            bottomSheetViewModel = bottomSheetViewModel,
//            enabled = true,
//            resources = resources,
//            onClick = { launchApp(appInfo, it, false) },
//            hideDrawer = hideDrawer,
//            ignoreLibRedirectClick = ignoreLibRedirectClick,
//            showToast = showToast
//        )

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
