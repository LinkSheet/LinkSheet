package fe.linksheet.composable.main

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import dev.zwander.shared.ShizukuUtil
import fe.linksheet.LinkSheetAppConfig
import fe.linksheet.R
import fe.linksheet.composable.util.ColoredIcon
import fe.linksheet.discordInvite
import fe.linksheet.extension.compose.currentActivity
import fe.linksheet.extension.compose.header
import fe.linksheet.extension.compose.observeAsState
import fe.linksheet.module.viewmodel.MainViewModel
import fe.linksheet.settingsRoute
import fe.linksheet.ui.HkGroteskFontFamily
import fe.linksheet.ui.Typography
import fe.linksheet.util.AppSignature
import fe.linksheet.util.Results
import fe.linksheet.util.lazyItemKeyCreator
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel


val itemKeyCreator = lazyItemKeyCreator()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainRoute(
    navController: NavHostController,
    viewModel: MainViewModel = koinViewModel()
) {
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


    val lifecycleState = LocalLifecycleOwner.current.lifecycle
        .observeAsState(ignoreFirst = Lifecycle.Event.ON_RESUME)

    LaunchedEffect(lifecycleState.first) {
        if (lifecycleState.first == Lifecycle.Event.ON_RESUME) {
            defaultBrowserEnabled = Results.loading()
            defaultBrowserEnabled = Results.result(viewModel.checkDefaultBrowser())

            shizukuInstalled = ShizukuUtil.isShizukuInstalled(activity)
            shizukuRunning = ShizukuUtil.isShizukuRunning()

            sheetOpen = null
        }
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
            item(itemKeyCreator.next()) {
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
                item(itemKeyCreator.next()) {
                    UnofficialBuild()
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            if (useTime != null && showOtherBanners) {
                header(header = R.string.donate, itemKey = itemKeyCreator.next())

                item(itemKeyCreator.next()) {
                    DonateCard(
                        navController = navController,
                        useTime = useTime
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            header(header = R.string.app_setup, itemKey = itemKeyCreator.next())

            item(key = itemKeyCreator.next()) {
                OpenDefaultBrowserCard(
                    activity = activity,
                    defaultBrowserEnabled = defaultBrowserEnabled,
                    defaultBrowserChanged = { defaultBrowserEnabled = it },
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            if (viewModel.featureFlagShizuku.value) {
                item(key = itemKeyCreator.next()) {
                    ShizukuCard(
                        activity = activity,
                        uriHandler = uriHandler,
                        shizukuInstalled = shizukuInstalled,
                        shizukuRunning = shizukuRunning
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                }
            }

            item(key = itemKeyCreator.next()) {
                BrowserCard(browserStatus = browserStatus)
                Spacer(modifier = Modifier.height(10.dp))
            }

            // sheetOpen is used to avoid the card flickering since clipboardManager.hasText() returns null once the activity looses focus
            if (clipboardManager.hasText() || sheetOpen != null) {
                val item = clipboardManager.getText()?.text

                if ((item != null && Patterns.WEB_URL.matcher(item)
                        .matches()) || sheetOpen != null
                ) {
                    item(key = itemKeyCreator.next()) {
                        OpenCopiedLink(
                            uriHandler = uriHandler,
                            item = item ?: sheetOpen!!,
                            sheetOpen = {
                                sheetOpen = item
                            }
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }

            if (viewModel.showDiscordBanner.value && showOtherBanners) {
                header(header = R.string.other, itemKey = itemKeyCreator.next())

                item(key = itemKeyCreator.next()) {
                    DiscordCard(viewModel = viewModel, uriHandler = uriHandler)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}