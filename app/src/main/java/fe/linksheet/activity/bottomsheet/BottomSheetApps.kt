package fe.linksheet.activity.bottomsheet

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import app.linksheet.feature.browser.Browser
import app.linksheet.compose.preview.PreviewContainer
import app.linksheet.testing.asPreferredApp
import app.linksheet.testing.fake.PackageInfoFakes
import app.linksheet.testing.fake.toActivityAppInfo
import app.linksheet.testing.util.listOfFirstActivityResolveInfo
import app.linksheet.testing.util.packageName
import coil3.ImageLoader
import fe.linksheet.R
import fe.linksheet.activity.bottomsheet.content.success.AppContentRoot
import fe.linksheet.activity.bottomsheet.content.success.PreferredAppColumn
import fe.linksheet.activity.bottomsheet.content.success.url.UrlBarWrapper
import app.linksheet.compose.theme.HkGroteskFontFamily
import fe.linksheet.feature.app.ActivityAppInfo
import fe.linksheet.module.database.entity.PreferredApp
import fe.linksheet.module.downloader.DownloadCheckResult
import fe.linksheet.feature.profile.CrossProfile
import fe.linksheet.feature.profile.ProfileStatus
import fe.linksheet.feature.profile.ProfileSwitcher
import fe.linksheet.feature.profile.UserProfileInfo
import fe.linksheet.module.resolver.FilteredBrowserList
import fe.linksheet.module.resolver.IntentResolveResult
import fe.linksheet.module.resolver.ResolveModuleStatus
import fe.linksheet.module.resolver.browser.BrowserMode
import fe.linksheet.module.resolver.util.AppSorter
import kotlinx.coroutines.CompletionHandler
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


@Composable
fun BottomSheetApps(
    modifier: Modifier = Modifier,
    result: IntentResolveResult.Default,
    imageLoader: ImageLoader?,
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
    isPrivateBrowser: (Boolean, ActivityAppInfo) -> Browser?,
    showToast: (Int, Int, Boolean) -> Unit,
    copyUrl: (String, String) -> Unit,
    startDownload: (String, DownloadCheckResult.Downloadable) -> Unit,
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
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (previewUrl && hasUri) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 15.dp)
//                    .wrapContentHeight()
//                    .weight(0.2f, fill = false)
                ,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                UrlBarWrapper(
                    imageLoader = imageLoader,
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
                        controller.dispatch(
                            PreferredAppChoiceButtonInteraction(
                                result.filteredItem,
                                modifier,
                                result.intent
                            )
                        )
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
                dispatch = controller.dispatch
            )
        }
    }
}

private object ProfileSwitcherStub : ProfileSwitcher {
    override fun checkIsManagedProfile(): Boolean = false
    override fun getStatus(): ProfileStatus = ProfileStatus.Unsupported
    override fun getUserProfileInfo(status: ProfileStatus): UserProfileInfo? = null
    override fun launchCrossProfileInteractSettings(activity: Activity): Boolean = false
    override fun canQuickToggle(): Boolean = false
    override fun switchTo(profile: CrossProfile, url: String, activity: Activity) {}
    override fun startOther(profile: CrossProfile, activity: Activity) {}
    override fun getProfiles(status: ProfileStatus): List<CrossProfile>? = null
}

object BottomSheetStateControllerStub : BottomSheetStateController {
    override val editorLauncher: ActivityResultLauncher<Intent>
        get() = TODO("Not yet implemented")
    override val onNewIntent: (Intent) -> Unit = {}
    override fun hideAndFinish() {}
    override fun hide(onCompletion: CompletionHandler?) {}
    override fun startActivity(intent: Intent) {}
    override fun finish() {}
    override val dispatch: (Interaction) -> Unit = {}
}

private class PreviewStateProvider() : PreviewParameterProvider<PreviewState> {
    override val values: Sequence<PreviewState> = sequenceOf(
        PreviewState(
            filteredBrowserList = FilteredBrowserList(
                browserMode = BrowserMode.None,
                browsers = listOfFirstActivityResolveInfo(PackageInfoFakes.MiBrowser),
                apps = listOfFirstActivityResolveInfo(
                    PackageInfoFakes.Youtube,
                    PackageInfoFakes.NewPipe,
                    PackageInfoFakes.NewPipeEnhanced
                ),
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
                apps = listOfFirstActivityResolveInfo(
                    PackageInfoFakes.Youtube,
                    PackageInfoFakes.NewPipe,
                    PackageInfoFakes.NewPipeEnhanced
                ),
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
                apps = listOfFirstActivityResolveInfo(
                    PackageInfoFakes.Youtube,
                    PackageInfoFakes.NewPipe,
                    PackageInfoFakes.NewPipeEnhanced
                ),
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
        PreviewState(
            filteredBrowserList = FilteredBrowserList(
                browserMode = BrowserMode.None,
                browsers = listOfFirstActivityResolveInfo(PackageInfoFakes.MiBrowser),
                apps = listOfFirstActivityResolveInfo(
                    PackageInfoFakes.Youtube,
                    PackageInfoFakes.NewPipe,
                    PackageInfoFakes.NewPipeEnhanced,
                    PackageInfoFakes.Dummy
                ),
                isSingleOption = false,
                noBrowsersOnlySingleApp = false
            ),
            lastChosen = PackageInfoFakes.Dummy.asPreferredApp("google.com"),
            returnLastChosen = true,
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
@Preview(showBackground = true, group = "List")
private fun BottomSheetAppsPreview_List(
    @PreviewParameter(PreviewStateProvider::class) state: PreviewState,
) {
    BottomSheetAppsBasePreview(state = state, gridLayout = false)
}

@Composable
@Preview(showBackground = true, group = "Grid")
private fun BottomSheetAppsPreview_Grid(
    @PreviewParameter(PreviewStateProvider::class) state: PreviewState,
) {
    BottomSheetAppsBasePreview(state = state, gridLayout = true)
}

@OptIn(ExperimentalTime::class)
@Composable
private fun BottomSheetAppsBasePreview(state: PreviewState, gridLayout: Boolean) {
    val appSorter = AppSorter(
        queryAndAggregateUsageStats = { _, _ -> emptyMap() },
        toAppInfo = { resolveInfo, browser -> resolveInfo.toActivityAppInfo() },
        clock = Clock.System
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
        referringPackageName = null,
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
            isPrivateBrowser = { hasUri, info -> null },
            showToast = { textId, duration, uiThread -> },
            copyUrl = { label, url -> },
            startDownload = { uri, downloadable -> },
            controller = BottomSheetStateControllerStub,
            showPackage = false,
            previewUrl = true,
            hideBottomSheetChoiceButtons = state.hideBottomSheetChoiceButtons,
            urlCardDoubleTap = false,
            imageLoader = null
        )
    }
}
