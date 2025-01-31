package fe.linksheet.activity.bottomsheet

import android.app.Activity
import android.app.usage.UsageStats
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import app.linksheet.testing.PackageInfoFakes
import app.linksheet.testing.listOfFirstActivityResolveInfo
import app.linksheet.testing.packageName
import app.linksheet.testing.toActivityAppInfo
import fe.linksheet.R
import fe.linksheet.activity.bottomsheet.content.success.AppContentRoot
import fe.linksheet.activity.bottomsheet.content.success.PreferredAppColumn
import fe.linksheet.activity.bottomsheet.content.success.url.UrlBarWrapper
import fe.linksheet.composable.ui.HkGroteskFontFamily
import fe.linksheet.composable.ui.LocalActivity
import fe.linksheet.composable.ui.PreviewTheme
import fe.linksheet.module.app.ActivityAppInfo
import fe.linksheet.module.app.PackageKeyService
import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.module.downloader.DownloadCheckResult
import fe.linksheet.module.profile.CrossProfile
import fe.linksheet.module.profile.ProfileSwitcher
import fe.linksheet.module.resolver.FilteredBrowserList
import fe.linksheet.module.resolver.IntentResolveResult
import fe.linksheet.module.resolver.KnownBrowser
import fe.linksheet.module.resolver.ResolveModuleStatus
import fe.linksheet.module.resolver.browser.BrowserMode
import fe.linksheet.module.resolver.util.AppSorter
import kotlinx.coroutines.CompletionHandler

typealias LaunchApp = (ActivityAppInfo, Intent, ClickModifier) -> Unit
typealias Launch2 = (index: Int, info: ActivityAppInfo, type: ClickType, modifier: ClickModifier) -> Unit

@Composable
fun BottomSheetApps(
    result: IntentResolveResult.Default,
    enableIgnoreLibRedirectButton: Boolean,
    enableSwitchProfile: Boolean,
    profileSwitcher: ProfileSwitcher,
    enableUrlCopiedToast: Boolean,
    enableDownloadStartedToast: Boolean,
    enableManualRedirect: Boolean,
    hideAfterCopying: Boolean,
    bottomSheetNativeLabel: Boolean,
    gridLayout: Boolean,
    appListSelectedIdx: Int,
    launchApp: LaunchApp,
    launch2: Launch2,
    isPrivateBrowser: (hasUri: Boolean, info: ActivityAppInfo) -> KnownBrowser?,
    showToast: (textId: Int, duration: Int, uiThread: Boolean) -> Unit,
    copyUrl: (String, String) -> Unit,
    startDownload: (String, DownloadCheckResult.Downloadable) -> Unit,
    isExpanded: Boolean,
    requestExpand: () -> Unit,
    controller: BottomSheetStateController,
    showPackage: Boolean,
    previewUrl: Boolean,
    hideBottomSheetChoiceButtons: Boolean,
    urlCardDoubleTap: Boolean,
) {
    val hasUri = result.uri != null
    val hasResolvedApps = result.resolved.isNotEmpty()
    val hasPreferredApp = result.filteredItem != null

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (previewUrl && hasUri) {
            Column(
                modifier = Modifier.padding(horizontal = 15.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                UrlBarWrapper(
                    profileSwitcher = profileSwitcher,
                    result = result,
                    enableIgnoreLibRedirectButton = enableIgnoreLibRedirectButton,
                    enableSwitchProfile = enableSwitchProfile,
                    enableUrlCopiedToast = enableUrlCopiedToast,
                    enableDownloadStartedToast = enableDownloadStartedToast,
                    enableUrlCardDoubleTap = urlCardDoubleTap,
                    enableManualRedirect = enableManualRedirect,
                    hideAfterCopying = hideAfterCopying,
                    controller = controller,
                    showToast = { id -> showToast(id, Toast.LENGTH_SHORT, false) },
                    copyUrl = copyUrl,
                    startDownload = startDownload,
                    launchApp = launchApp
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(0.25f))
            }
        }

        if (hasPreferredApp) {
            val privateBrowser = isPrivateBrowser(hasUri, result.filteredItem)

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                PreferredAppColumn(
                    appInfo = result.filteredItem,
                    privateBrowser = privateBrowser,
                    preferred = true,
                    showPackage = showPackage,
                    hideBottomSheetChoiceButtons = hideBottomSheetChoiceButtons,
                    onClick = { _, modifier ->
                        launchApp(result.filteredItem, result.intent, modifier)
                    }
                )

                if (hasResolvedApps) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 15.dp),
                        color = MaterialTheme.colorScheme.outline.copy(0.25f)
                    )

                    Text(
                        modifier = Modifier.padding(start = 15.dp),
                        text = stringResource(id = R.string.use_a_different_app),
                        fontFamily = HkGroteskFontFamily,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        } else {
            Row(modifier = Modifier.padding(horizontal = 15.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(id = R.string.open_with),
                    fontFamily = HkGroteskFontFamily,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        if (hasResolvedApps) {
            AppContentRoot(
                gridLayout = gridLayout,
                apps = result.resolved,
                uri = result.uri,
                appListSelectedIdx = appListSelectedIdx,
                hasPreferredApp = hasPreferredApp,
                hideChoiceButtons = hideBottomSheetChoiceButtons,
                showPackage = showPackage,
                isPrivateBrowser = isPrivateBrowser,
                showToast = showToast,
                showNativeLabel = bottomSheetNativeLabel,
                launch = { info, modifier ->
                    launchApp(info, result.intent, modifier)
                },
                launch2 = launch2
            )
        }
    }
}

object ProfileSwitcherStub : ProfileSwitcher {
    override fun launchCrossProfileInteractSettings(activity: Activity): Boolean = false
    override fun needsSetupAtLeastR(): Boolean = false
    override fun needsSetup(): Boolean = false
    override fun switchTo(profile: CrossProfile, url: String, activity: Activity) {}
    override fun startOther(profile: CrossProfile, activity: Activity) {}
    override fun getProfiles(): List<CrossProfile>? = null
    override fun getProfilesInternal(): List<CrossProfile>? = null
}

object BottomSheetStateControllerStub : BottomSheetStateController {
    override val editorLauncher: ActivityResultLauncher<Intent>
        get() = TODO("Not yet implemented")
    override val onNewIntent: (Intent) -> Unit = {}
    override fun hideAndFinish() {}
    override fun hide(onCompletion: CompletionHandler?) {}
    override fun startActivity(intent: Intent) {}
    override fun finish() {}
}

private class PreviewStateProvider() : PreviewParameterProvider<PreviewState> {
    override val values: Sequence<PreviewState> = sequenceOf(
        PreviewState(
            filteredBrowserList = FilteredBrowserList(
                browserMode = BrowserMode.None,
                browsers = listOfFirstActivityResolveInfo(PackageInfoFakes.MiBrowser),
                apps = listOfFirstActivityResolveInfo(PackageInfoFakes.Youtube, PackageInfoFakes.NewPipe, PackageInfoFakes.NewPipeEnhanced),
                isSingleOption = false,
                noBrowsersOnlySingleApp = false
            ),
            lastChosen = PreferredApp(
                _packageName = PackageInfoFakes.MiBrowser.packageInfo.packageName,
                _component = null,
                host = "google.com",
                alwaysPreferred = false
            ),
            returnLastChosen = true,
            hasSingleMatchingOption = false,
            hideBottomSheetChoiceButtons = true,
        ),
        PreviewState(
            filteredBrowserList = FilteredBrowserList(
                browserMode = BrowserMode.None,
                browsers = listOfFirstActivityResolveInfo(PackageInfoFakes.MiBrowser),
                apps = listOfFirstActivityResolveInfo(PackageInfoFakes.Youtube, PackageInfoFakes.NewPipe, PackageInfoFakes.NewPipeEnhanced),
                isSingleOption = false,
                noBrowsersOnlySingleApp = false
            ),
            lastChosen = PreferredApp(
                _packageName = PackageInfoFakes.MiBrowser.packageName,
                _component = null,
                host = "google.com",
                alwaysPreferred = false
            ),
            returnLastChosen = true,
            hasSingleMatchingOption = false,
            hideBottomSheetChoiceButtons = false,
        ),
        PreviewState(
            filteredBrowserList = FilteredBrowserList(
                browserMode = BrowserMode.None,
                browsers = listOfFirstActivityResolveInfo(PackageInfoFakes.MiBrowser),
                apps = listOfFirstActivityResolveInfo(PackageInfoFakes.Youtube, PackageInfoFakes.NewPipe, PackageInfoFakes.NewPipeEnhanced),
                isSingleOption = false,
                noBrowsersOnlySingleApp = false
            ),
            lastChosen = PreferredApp(
                _packageName = PackageInfoFakes.MiBrowser.packageName,
                _component = null,
                host = "google.com",
                alwaysPreferred = false
            ),
            returnLastChosen = false,
            hasSingleMatchingOption = false,
            hideBottomSheetChoiceButtons = false,
        ),
    )
}

private data class PreviewState(
    val filteredBrowserList: FilteredBrowserList,
    val lastChosen: PreferredApp,
    val returnLastChosen: Boolean,
    val hasSingleMatchingOption: Boolean,
    val hideBottomSheetChoiceButtons: Boolean,
)

@Composable
@Preview(showBackground = true)
private fun BottomSheetAppsPreview_List(
    @PreviewParameter(PreviewStateProvider::class) state: PreviewState,
) {
    BottomSheetAppsBasePreview(state = state, gridLayout = false)
}

@Composable
@Preview(showBackground = true)
private fun BottomSheetAppsPreview_Grid(
    @PreviewParameter(PreviewStateProvider::class) state: PreviewState,
) {
    BottomSheetAppsBasePreview(state = state, gridLayout = true)
}

@Composable
private fun BottomSheetAppsBasePreview(state: PreviewState, gridLayout: Boolean) {
    val packageKey = PackageKeyService(
        checkDisableDeduplicationExperiment = { false }
    )
    val appSorter = AppSorter(
        queryAndAggregateUsageStats = { _, _ -> emptyMap<String, UsageStats>() },
        toAppInfo = { resolveInfo, browser -> resolveInfo.toActivityAppInfo() },
        toPackageKey = packageKey::getDuplicationKey
    )

    val (sorted, filtered) = appSorter.sort(
        appList = state.filteredBrowserList,
        lastChosen = state.lastChosen,
        historyMap = emptyMap(),
        returnLastChosen = state.returnLastChosen
    )

    val result = IntentResolveResult.Default(
        intent = Intent(),
        uri = Uri.parse("https://google.com"),
        unfurlResult = null,
        referrer = null,
        resolved = sorted,
        filteredItem = filtered,
        alwaysPreferred = state.lastChosen.alwaysPreferred,
        hasSingleMatchingOption = state.hasSingleMatchingOption,
        resolveModuleStatus = ResolveModuleStatus(),
        libRedirectResult = null,
        downloadable = DownloadCheckResult.NonDownloadable
    )

    PreviewContainer {
        BottomSheetApps(
            result = result,
            enableIgnoreLibRedirectButton = false,
            enableSwitchProfile = false,
            profileSwitcher = ProfileSwitcherStub,
            enableUrlCopiedToast = false,
            enableDownloadStartedToast = false,
            enableManualRedirect = false,
            hideAfterCopying = false,
            bottomSheetNativeLabel = false,
            gridLayout = gridLayout,
            appListSelectedIdx = -1,
            launchApp = { info, intent, modifier -> },
            launch2 = { index, info, type, modifier -> },
            isPrivateBrowser = { hasUri, info -> null },
            showToast = { textId, duration, uiThread -> },
            copyUrl = { label, url -> },
            startDownload = { uri, downloadable -> },
            isExpanded = false,
            requestExpand = {},
            controller = BottomSheetStateControllerStub,
            showPackage = false,
            previewUrl = true,
            hideBottomSheetChoiceButtons = state.hideBottomSheetChoiceButtons,
            urlCardDoubleTap = false
        )
    }
}

@Composable
private fun PreviewContainer(content: @Composable () -> Unit) {
    PreviewTheme {
        CompositionLocalProvider(LocalActivity provides Activity()) {
            Column(modifier = Modifier.wrapContentSize()) {
                content()
            }
        }
    }
}
