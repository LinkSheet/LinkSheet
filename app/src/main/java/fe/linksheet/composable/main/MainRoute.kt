package fe.linksheet.composable.main

import android.util.Patterns
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import dev.zwander.shared.ShizukuUtil
import fe.linksheet.LinkSheetAppConfig
import fe.linksheet.R
import fe.linksheet.extension.compose.currentActivity
import fe.linksheet.extension.compose.header
import fe.linksheet.extension.compose.observeAsState
import fe.linksheet.extension.compose.onStateChange
import fe.linksheet.module.viewmodel.MainViewModel
import fe.linksheet.settingsRoute
import fe.linksheet.ui.HkGroteskFontFamily
import fe.linksheet.util.AppSignature
import fe.linksheet.util.Results
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainRoute(navController: NavHostController, viewModel: MainViewModel = koinViewModel()) {
    val activity = LocalContext.currentActivity()
    val clipboardManager = LocalClipboardManager.current
    val uriHandler = LocalUriHandler.current

    var shizukuInstalled by remember { mutableStateOf(ShizukuUtil.isShizukuInstalled(activity)) }
    var shizukuRunning by remember { mutableStateOf(ShizukuUtil.isShizukuRunning()) }

    var defaultBrowserEnabled by remember { mutableStateOf(Results.loading()) }
    var sheetOpen by remember { mutableStateOf<String?>(null) }
    val useTime = viewModel.formatUseTime()

    val browserStatus by remember {
        mutableStateOf(viewModel.hasBrowser())
    }

    LaunchedEffect(Unit) {
        delay(200)
        defaultBrowserEnabled = Results.result(viewModel.checkDefaultBrowser())
    }

    val showOtherBanners by remember {
        derivedStateOf {
            defaultBrowserEnabled.isSuccess && MainViewModel.BrowserStatus.hasBrowser(browserStatus)
        }
    }

    LocalLifecycleOwner.current.lifecycle.onStateChange {
        defaultBrowserEnabled = Results.loading()
        defaultBrowserEnabled = Results.result(viewModel.checkDefaultBrowser())

        shizukuInstalled = ShizukuUtil.isShizukuInstalled(activity)
        shizukuRunning = ShizukuUtil.isShizukuRunning()

        sheetOpen = null
    }

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(title = {}, modifier = Modifier.padding(horizontal = 8.dp), navigationIcon = {
            IconButton(onClick = { navController.navigate(settingsRoute) }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(id = R.string.settings)
                )
            }
        })
    }) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.extraLarge)
                        .padding(horizontal = 12.dp)
                        .padding(top = 12.dp, bottom = 3.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
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
                }

                Spacer(modifier = Modifier.height(5.dp))
            }

            if (AppSignature.checkSignature(activity) == AppSignature.BuildType.Unofficial) {
                cardItem {
                    UnofficialBuild()
                }
            }

            if (useTime != null && showOtherBanners) {
                cardItem(header = R.string.donate) {
                    DonateCard(navController = navController, useTime = useTime)
                }
            }

            cardItem(header = R.string.app_setup) {
                OpenDefaultBrowserCard(
                    activity = activity,
                    defaultBrowserEnabled = defaultBrowserEnabled,
                    defaultBrowserChanged = { defaultBrowserEnabled = it },
                    viewModel = viewModel
                )
            }

            if (viewModel.featureFlagShizuku.value) {
                cardItem {
                    ShizukuCard(
                        activity = activity,
                        uriHandler = uriHandler,
                        shizukuInstalled = shizukuInstalled,
                        shizukuRunning = shizukuRunning
                    )
                }
            }

            cardItem {
                BrowserCard(browserStatus = browserStatus)
            }

            // sheetOpen is used to avoid the card flickering since clipboardManager.hasText() returns null once the activity looses focus
            if (clipboardManager.hasText() || sheetOpen != null) {
                val item = clipboardManager.getText()?.text

                if ((item != null && Patterns.WEB_URL.matcher(item).matches()) || sheetOpen != null) {
                    cardItem {
                        OpenCopiedLink(uriHandler = uriHandler, item = item ?: sheetOpen!!, sheetOpen = {
                            sheetOpen = item
                        })
                    }
                }
            }

            if (showOtherBanners) {
                val discord = viewModel.showDiscordBanner.value
                val newBottomSheet = viewModel.showNewBottomSheetBanner.value

                if (discord || newBottomSheet) {
                    header(header = R.string.other)
                }

                if (discord) {
                    cardItem {
                        DiscordCard(viewModel = viewModel, uriHandler = uriHandler)
                    }
                }

                if (newBottomSheet) {
                    cardItem {
                        NewBottomSheetCard(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

fun LazyListScope.cardItem(
    @StringRes header: Int? = null,
    height: Dp = 10.dp,
    content: @Composable LazyItemScope.() -> Unit
) {
    if (header != null) {
        header(header = header)
    }

    item {
        content(this)
        Spacer(modifier = Modifier.height(height))
    }
}
