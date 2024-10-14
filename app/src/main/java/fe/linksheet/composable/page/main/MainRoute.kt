package fe.linksheet.composable.page.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import dev.zwander.shared.ShizukuUtil
import fe.composekit.component.ContentType
import fe.composekit.component.list.column.SaneLazyColumnLayout
import fe.linksheet.*
import fe.linksheet.R
import fe.linksheet.debug.DebugComposable
import fe.linksheet.composable.page.home.status.StatusCardWrapper
import fe.linksheet.extension.compose.*
import fe.linksheet.module.viewmodel.MainViewModel
import fe.linksheet.composable.ui.HkGroteskFontFamily
import fe.linksheet.composable.ui.LocalActivity
import fe.linksheet.util.BuildType
import fe.linksheet.util.Results
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewMainRoute(navController: NavHostController, viewModel: MainViewModel = koinViewModel()) {
    val activity = LocalActivity.current
    val clipboardManager = LocalClipboardManager.current
    val uriHandler = LocalUriHandler.current

    val clipboardUri by viewModel.clipboardContent.collectAsStateWithLifecycle()
    var shizukuInstalled by remember { mutableStateOf(ShizukuUtil.isShizukuInstalled(activity)) }
    var shizukuRunning by remember { mutableStateOf(ShizukuUtil.isShizukuRunning()) }

    var defaultBrowserEnabled by remember { mutableStateOf(Results.loading()) }
    val useTime = viewModel.formatUseTime()

    val browserStatus by remember { mutableStateOf(viewModel.hasBrowser()) }

    LaunchedEffect(Unit) {
//        delay(200)
        defaultBrowserEnabled = Results.result(viewModel.checkDefaultBrowser())
    }

    val showOtherBanners by remember {
        mutableStateOf(defaultBrowserEnabled.isSuccess && MainViewModel.BrowserStatus.hasBrowser(browserStatus))
    }

    clipboardManager.ObserveClipboard {
        viewModel.tryUpdateClipboard()
    }

    LocalWindowInfo.current.OnFocused {
        viewModel.tryUpdateClipboard()
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
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = stringResource(id = R.string.settings)
                    )
                }
            }
        )
    }) { padding ->
        SaneLazyColumnLayout(padding = padding, verticalArrangement = Arrangement.spacedBy(6.dp)) {
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

            if (BuildType.current.allowDebug) {
                item {
                    DebugComposable.MainRoute.compose(currentComposer, 0)
                }
            }

            if (BuildType.current.allowDebug) {
                item {
                    Row {
                        FilledTonalButton(
                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                            onClick = { navController.navigate(Routes.RuleOverview) }
                        ) {
                            Text(text = "Rules")
                        }
                    }
                }
            }

            item(
                key = R.string.settings_main_setup_success__title_linksheet_setup_success,
                contentType = ContentType.ClickableAlert
            ) {
                StatusCardWrapper(
                    isDefaultBrowser = defaultBrowserEnabled.isSuccess,
                    launchIntent = { viewModel.launchIntent(activity, it) },
                    updateDefaultBrowser = {
                        defaultBrowserEnabled = Results.success()
                    }
                )
            }

            if (BuildType.current == BuildType.Debug || BuildType.current == BuildType.Nightly) {
                item(key = R.string.nightly_experiments_card, contentType = ContentType.ClickableAlert) {
                    NightlyExperimentsCard(navigate = { navController.navigate(it) })
                }
            }

            if (clipboardUri != null) {
                item(
                    key = R.string.open_copied_link,
                    contentType = ContentType.ClickableAlert
                ) {
                    OpenCopiedLink(
                        editClipboard = viewModel.editClipboard(),
                        uri = clipboardUri!!,
                        navigate = { navController.navigate(it) }
                    )
                }
            }

//            divider(id =  R.string.settings_main_news__text_header)

//            item(
//                key = R.string.settings_main_news__text_ui_overhaul,
//                contentType = ContentType.ClickableAlert
//            ) {
//                NewsCard(
//                    titleId = R.string.settings_main_news__title_ui_overhaul,
//                    icon = Icons.Outlined.AutoAwesome.iconPainter,
//                    contentId = R.string.settings_main_news__text_ui_overhaul,
//                    buttonTextId = R.string.settings_main_news__button_read_more,
//                    onClick = {
//                        Toast.makeText(activity, "News will become available at a later date!", Toast.LENGTH_SHORT)
//                            .show()
//                    }
//                )
//            }


//            if (AppSignature.checkSignature(activity) == AppSignature.SignatureBuildType.Unofficial) {
//                item(
//                    key = R.string.running_unofficial_build,
//                    contentType = ContentType.Alert
//                ) {
//                    UnofficialBuild()
//                }
//            }


//            if (useTime != null && showOtherBanners && !viewModel.donateCardDismissed()) {
//                cardItem(header = R.string.donate) {
//                    DonateCard(navController = navController, viewModel = viewModel, useTime = useTime)
//                }
//            }

//            item(
//                key = R.string.set_as_default_browser,
//                contentType = ContentType.ClickableAlert
//            ) {
//                OpenDefaultBrowserCard(
//                    activity = activity,
//                    defaultBrowserEnabled = defaultBrowserEnabled,
//                    defaultBrowserChanged = { defaultBrowserEnabled = it },
//                    viewModel = viewModel
//                )
//            }

//            item(
//                key = R.string.shizuku_integration,
//                contentType = ContentType.ClickableAlert
//            ) {
//                ShizukuCard(
//                    activity = activity,
//                    uriHandler = uriHandler,
//                    shizukuInstalled = shizukuInstalled,
//                    shizukuRunning = shizukuRunning
//                )
//            }

//            if (browserStatus != MainViewModel.BrowserStatus.Known) {
//            item(
//                key = R.string.browser_status,
//                contentType = ContentType.ClickableAlert
//            ) {
//                BrowserCard(browserStatus = browserStatus)
//            }
//            }


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

