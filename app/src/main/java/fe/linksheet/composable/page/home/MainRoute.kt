package fe.linksheet.composable.page.home

import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import fe.composekit.component.ContentType
import fe.composekit.component.list.column.SaneLazyColumnLayout
import fe.linksheet.R
import fe.linksheet.composable.page.home.card.NightlyExperimentsCard
import fe.linksheet.composable.page.home.card.OpenCopiedLink
import fe.linksheet.composable.page.home.card.compat.MiuiCompatCardWrapper
import fe.linksheet.composable.page.home.card.news.ExperimentUpdatedCard
import fe.linksheet.composable.page.home.card.status.StatusCardWrapper
import fe.linksheet.composable.ui.HkGroteskFontFamily
import fe.linksheet.extension.android.showToast
import fe.linksheet.extension.compose.ObserveClipboard
import fe.linksheet.extension.compose.OnFocused
import fe.linksheet.extension.kotlinx.collectRefreshableAsStateWithLifecycle
import fe.linksheet.module.viewmodel.MainViewModel
import fe.linksheet.navigation.MarkdownViewerRoute
import fe.linksheet.navigation.settingsRoute
import fe.linksheet.util.LinkSheet
import fe.linksheet.util.buildconfig.Build
import fe.linksheet.util.buildconfig.BuildType
import fe.linksheet.util.buildconfig.LinkSheetAppConfig
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewMainRoute(navController: NavHostController, viewModel: MainViewModel = koinViewModel()) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val clipboardUri by viewModel.clipboardContent.collectAsStateWithLifecycle()

    val showMiuiAlert by viewModel.showMiuiAlert.collectRefreshableAsStateWithLifecycle(
        minActiveState = Lifecycle.State.RESUMED,
        initialValue = false
    )

    val defaultBrowser by viewModel.defaultBrowser.collectAsStateWithLifecycle(
        minActiveState = Lifecycle.State.RESUMED,
        initialValue = true
    )

    clipboardManager.ObserveClipboard {
        viewModel.tryReadClipboard()
    }

    LocalWindowInfo.current.OnFocused {
        viewModel.tryReadClipboard()
    }

//    var shizukuInstalled by remember { mutableStateOf(ShizukuUtil.isShizukuInstalled(context)) }
//    var shizukuRunning by remember { mutableStateOf(ShizukuUtil.isShizukuRunning()) }
//    LocalLifecycleOwner.current.lifecycle.ObserveStateChange(observeEvents = focusGainedEvents) {
//        shizukuInstalled = ShizukuUtil.isShizukuInstalled(context)
//        shizukuRunning = ShizukuUtil.isShizukuRunning()
//    }
    val activity = LocalActivity.current
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
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
        }
    ) { padding ->
        SaneLazyColumnLayout(padding = padding, verticalArrangement = Arrangement.spacedBy(6.dp)) {
            item(key = R.string.app_name, contentType = ContentType.TextItem) {
                Text(
                    modifier = Modifier,
                    text = stringResource(R.string.app_name),
                    fontFamily = HkGroteskFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 30.sp,
                )
            }

            item(key = R.string.thanks_for_donating, contentType = ContentType.TextItem) {
                if (!LinkSheetAppConfig.showDonationBanner()) {
                    Text(text = stringResource(id = R.string.thanks_for_donating))
                } else if (!Build.IsDebug) {
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            if (viewModel.debugMenu.enabled) {
                item {
                    viewModel.debugMenu.SlotContent(navigate = navController::navigate)
                }
            }

            item(
                key = R.string.settings_main_setup_success__title_linksheet_setup_success,
                contentType = ContentType.ClickableAlert
            ) {
                StatusCardWrapper(
                    isDefaultBrowser = defaultBrowser,
                    launchIntent = { viewModel.launchIntent(activity, it) },
                    updateDefaultBrowser = {}
                )
            }

            if (showMiuiAlert) {
                MiuiCompatCardWrapper(onClick = {
                    coroutineScope.launch {
                        if (!viewModel.updateMiuiAutoStartAppOp(activity)) {
                            activity?.showToast(
                                textId = R.string.settings_main_miui_compat__text_request_failed,
                                duration = Toast.LENGTH_LONG
                            )
                        }
                    }
                })
            }

            if (BuildType.current == BuildType.Debug || BuildType.current == BuildType.Nightly) {
                item(key = R.string.nightly_experiments_card, contentType = ContentType.ClickableAlert) {
                    NightlyExperimentsCard(navigate = { navController.navigate(it) })
                }
            }

            if (!viewModel.newDefaultsDismissed()) {
                item(
                    key = R.string.settings_main_experiment_news__title_experiment_state_updated,
                    contentType = ContentType.ClickableAlert
                ) {
                    ExperimentUpdatedCard(
                        onClick = {
                            navController.navigate(MarkdownViewerRoute(LinkSheet.WikiExperiments))
                        },
                        onDismiss = {
                            viewModel.newDefaultsDismissed(true)
                        }
                    )
                }
            }

            if (clipboardUri != null) {
                item(key = R.string.open_copied_link, contentType = ContentType.ClickableAlert) {
                    OpenCopiedLink(
                        editClipboard = viewModel.editClipboard(),
                        uri = clipboardUri!!,
                        navigate = { navController.navigate(it) }
                    )
                }
            }

//            divider(id = R.string.settings_main_news__text_header)
        }
    }
}

