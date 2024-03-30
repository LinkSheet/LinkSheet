package fe.linksheet.experiment.ui.overhaul.composable.page.main

import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import dev.zwander.shared.ShizukuUtil
import fe.linksheet.LinkSheetAppConfig
import fe.linksheet.R
import fe.linksheet.composable.main.DonateCard
import fe.linksheet.debug.DebugComposable
import fe.linksheet.experiment.ui.overhaul.composable.ContentTypeDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.page.SaneLazyColumnPageLayout
import fe.linksheet.extension.compose.ObserveStateChange
import fe.linksheet.extension.compose.OnFocused
import fe.linksheet.extension.compose.focusGainedEvents
import fe.linksheet.extension.compose.header
import fe.linksheet.module.viewmodel.MainViewModel
import fe.linksheet.settingsRoute
import fe.linksheet.ui.HkGroteskFontFamily
import fe.linksheet.ui.LocalActivity
import fe.linksheet.util.AppSignature
import fe.linksheet.util.BuildType
import fe.linksheet.util.Results
import fe.linksheet.util.UriUtil
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewMainRoute(navController: NavHostController, viewModel: MainViewModel = koinViewModel()) {
    val activity = LocalActivity.current
    val clipboardManager = LocalClipboardManager.current
    val uriHandler = LocalUriHandler.current

    var shizukuInstalled by remember { mutableStateOf(ShizukuUtil.isShizukuInstalled(activity)) }
    var shizukuRunning by remember { mutableStateOf(ShizukuUtil.isShizukuRunning()) }

    var defaultBrowserEnabled by remember { mutableStateOf(Results.loading()) }
    val useTime = viewModel.formatUseTime()

    var clipboardUri by remember { mutableStateOf(getClipboardUrl(clipboardManager)) }
    val browserStatus by remember { mutableStateOf(viewModel.hasBrowser()) }

    LaunchedEffect(Unit) {
//        delay(200)
        defaultBrowserEnabled = Results.result(viewModel.checkDefaultBrowser())
    }

    val showOtherBanners by remember {
        derivedStateOf {
            defaultBrowserEnabled.isSuccess && MainViewModel.BrowserStatus.hasBrowser(browserStatus)
        }
    }

    LocalWindowInfo.current.OnFocused {
        clipboardUri = getClipboardUrl(clipboardManager)
    }

    LocalLifecycleOwner.current.lifecycle.ObserveStateChange(observeEvents = focusGainedEvents) {
        defaultBrowserEnabled = Results.loading()
        defaultBrowserEnabled = Results.result(viewModel.checkDefaultBrowser())

        shizukuInstalled = ShizukuUtil.isShizukuInstalled(activity)
        shizukuRunning = ShizukuUtil.isShizukuRunning()
    }

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = { navController.navigate(settingsRoute) }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(id = R.string.settings)
                    )
                }
            }
        )
    }) { padding ->
        SaneLazyColumnPageLayout(padding = padding, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            item {
                Text(
                    modifier = Modifier,
                    text = stringResource(R.string.app_name),
                    fontFamily = HkGroteskFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 30.sp,
                )

                if (!LinkSheetAppConfig.showDonationBanner()) {
                    Text(text = stringResource(id = R.string.thanks_for_donating))
                }
            }

            if (BuildType.current?.allowDebug == true) {
                item {
                    DebugComposable.MainRoute.compose(currentComposer, 0)
                }
            }

            if (BuildType.current == BuildType.Debug || BuildType.current == BuildType.Nightly) {
                item(
                    key = R.string.nightly_experiments_card,
                    contentType = ContentTypeDefaults.ClickableAlert
                ) {
                    NightlyExperimentsCard(navigate = { navController.navigate(it) })
                }
            }

            if (AppSignature.checkSignature(activity) == AppSignature.SignatureBuildType.Unofficial) {
                item(
                    key = R.string.running_unofficial_build,
                    contentType = ContentTypeDefaults.Alert
                ) {
                    UnofficialBuild()
                }
            }

            if (useTime != null && showOtherBanners && !viewModel.donateCardDismissed()) {
                cardItem(header = R.string.donate) {
                    DonateCard(navController = navController, viewModel = viewModel, useTime = useTime)
                }
            }

            item(
                key = R.string.set_as_default_browser,
                contentType = ContentTypeDefaults.ClickableAlert
            ) {
                OpenDefaultBrowserCard(
                    activity = activity,
                    defaultBrowserEnabled = defaultBrowserEnabled,
                    defaultBrowserChanged = { defaultBrowserEnabled = it },
                    viewModel = viewModel
                )
            }

            item(
                key = R.string.shizuku_integration,
                contentType = ContentTypeDefaults.ClickableAlert
            ) {
                ShizukuCard(
                    activity = activity,
                    uriHandler = uriHandler,
                    shizukuInstalled = shizukuInstalled,
                    shizukuRunning = shizukuRunning
                )
            }

//            if (browserStatus != MainViewModel.BrowserStatus.Known) {
            item(
                key = R.string.browser_status,
                contentType = ContentTypeDefaults.ClickableAlert
            ) {
                BrowserCard(browserStatus = browserStatus)
            }
//            }

            if (clipboardUri != null) {
                item(
                    key = R.string.open_copied_link,
                    contentType = ContentTypeDefaults.ClickableAlert
                ) {
                    OpenCopiedLink(uri = clipboardUri!!)
                }
            }

//            if (showOtherBanners) {
//                val discord = viewModel.showDiscordBanner()
//
//                if (discord) {
//                    header(header = R.string.other)
//                }
//
//                if (discord) {
//                    cardItem {
//                        DiscordCard(viewModel = viewModel, uriHandler = uriHandler)
//                    }
//                }
//            }

//            items(count = 100, key = { it }) { idx ->
//                OutlinedCard(modifier = Modifier.requiredHeight(100.dp), onClick = { /*TODO*/ }) {
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(all = 16.dp)
//                    ) {
//                        Text(
//                            text = "$idx",
//                            fontFamily = HkGroteskFontFamily,
//                            fontWeight = FontWeight.SemiBold,
//                            fontSize = 20.sp,
//                        )
//                    }
//                }
//            }
        }
    }
}

private fun getClipboardUrl(clipboardManager: ClipboardManager): Uri? {
    return clipboardManager.getText()?.text?.let { text -> UriUtil.parseWebUriStrict(text) }
}

private fun LazyListScope.cardItem(
    @StringRes header: Int? = null,
    content: @Composable LazyItemScope.() -> Unit,
) {
    if (header != null) {
        header(header = header)
    }

    item {
        content(this)
    }
}
