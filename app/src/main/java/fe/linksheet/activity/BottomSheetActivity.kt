package fe.linksheet.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import app.linksheet.compose.debug.LocalUiDebug
import app.linksheet.compose.debugBorder
import app.linksheet.feature.browser.Browser
import fe.composekit.extension.setText
import fe.composekit.preference.collectAsStateWithLifecycle
import fe.linksheet.R
import fe.linksheet.activity.bottomsheet.*
import fe.linksheet.activity.bottomsheet.compat.CompatSheetState
import fe.linksheet.activity.bottomsheet.compat.m3fix.M3FixModalBottomSheet
import fe.linksheet.activity.bottomsheet.compat.m3fix.rememberM3FixModalBottomSheetState
import fe.linksheet.activity.bottomsheet.content.failure.FailureSheetContentWrapper
import fe.linksheet.activity.bottomsheet.content.pending.LoadingIndicatorWrapper
import fe.linksheet.composable.ui.AppTheme
import fe.linksheet.extension.android.showToast
import fe.linksheet.extension.koin.injectLogger
import fe.linksheet.extension.kotlin.collectOnIO
import fe.linksheet.feature.app.ActivityAppInfo
import fe.linksheet.module.resolver.IntentResolveResult
import fe.linksheet.module.resolver.ResolveEvent
import fe.linksheet.module.resolver.ResolverInteraction
import fe.linksheet.module.resolver.util.LaunchIntent
import fe.linksheet.module.resolver.util.LaunchRawIntent
import fe.linksheet.module.viewmodel.BottomSheetViewModel
import fe.linksheet.util.intent.Intents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import mozilla.components.support.utils.toSafeIntent
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent

//import relocated.androidx.compose.material3.SheetValue
//import relocated.androidx.compose.material3.rememberModalBottomSheetState

// Must not be moved or renamed since LinkSheetCompat hardcodes the package/name
class BottomSheetActivity : BaseComponentActivity(), KoinComponent {
    private val logger by injectLogger<BottomSheetActivity>()
    private val viewModel by viewModel<BottomSheetViewModel>()

    private val initialIntent = MutableStateFlow<Intent?>(null)
    private val latestNewIntent = MutableStateFlow<Intent?>(null)

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Log.d(BottomSheetActivity::class.simpleName, "Received result for $result")
        // Apps may "refuse" to handle an intent and return back to LinkSheet instantly
        // * Amazon does this for Prime links, which results in a new intent being passed to onNewIntent
        // (and subsequently being written to latestNewIntent) and RESULT_CANCELED
        // * Hermit also sends RESULT_CANCELED for some reason, but doesn't provide a new intent first, meaning we can
        // still differentiate between a successful and a non-successful launch using the condition below
        if (result.resultCode == RESULT_OK || latestNewIntent.value == null) {
            finish()
        } else {
            showToast(
                textId = R.string.bottom_sheet__event_app_refusal,
                duration = Toast.LENGTH_LONG,
                uiThread = true
            )
        }
    }
    private val launchHandler = LaunchHandler(launcher)

    val editorLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode != RESULT_OK || result.data == null) return@registerForActivityResult

        val intent = result.data
            ?.getStringExtra(TextEditorActivity.EXTRA_TEXT)
            ?.toUri()
            ?.let { Intents.createSelfIntent(it) }

        onNewIntent(intent!!)
    }

    private val intentFlow = MutableStateFlow(initialIntent.value)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.resolveResultFlow
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .mapNotNull(::maybeHandleResult)
                .collectLatest(::handleLaunch)
        }

        lifecycleScope.launch {
            viewModel.warmupAsync()
        }

        setInitialIntent(intent)
        setContent(edgeToEdge = true) {
            AppTheme { Wrapper() }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun Wrapper() {
        val event by viewModel.events.collectOnIO()
        val interaction by viewModel.interactions.collectOnIO()

        val resolveResult by viewModel.resolveResultFlow.collectAsStateWithLifecycle()
        val currentIntent by intentFlow.collectAsStateWithLifecycle()

        val coroutineScope = rememberCoroutineScope()
        val sheetState = rememberM3FixModalBottomSheetState()
//        val sheetState = rememberModalBottomSheetState(
////            confirmValueChange = {
////                if(it == SheetValue.Hidden) true else true
////            }
//        )

        LaunchedEffect(key1 = resolveResult) {
            logger.info("Expanding bottom sheet, status: $resolveResult, isPending=${resolveResult == IntentResolveResult.Pending}")
            if (resolveResult != IntentResolveResult.Pending) {
                // Need to do this in a separate effect as otherwise the preview image seems to mess up the layout-ing
                if (viewModel.improvedBottomSheetExpandFully.value) {
                    sheetState.expand()
                } else {
                    sheetState.partialExpand()
                }
            }
        }

        LaunchedEffect(key1 = event) {
            logger.info("Latest event: $event")
        }

        val controller = remember {
            DefaultBottomSheetStateController(
                activity = this@BottomSheetActivity,
                editorLauncher = editorLauncher,
                coroutineScope = coroutineScope,
                drawerState = sheetState,
                onNewIntent = ::onNewIntent,
                dispatch = { interaction ->
                    coroutineScope.launch {
                        (resolveResult as? IntentResolveResult.Default)
                            ?.let { viewModel.handle(this@BottomSheetActivity, it, interaction) }
                            ?.let { handleLaunch(it) }
                    }
                }
            )
        }

        val themeAmoled by viewModel.themeAmoled.collectAsStateWithLifecycle()
        val interceptAccidentalTaps by viewModel.interceptAccidentalTaps.collectAsStateWithLifecycle()
        val debug by LocalUiDebug.current.drawBorders.collectAsStateWithLifecycle()
        M3FixModalBottomSheet(
            contentModifier = Modifier
                .interceptTaps(sheetState, interceptAccidentalTaps)
                .debugBorder(debug, 1.dp, Color.Red),
            debug = debug,
            // TODO: Replace with pref
            isBlackTheme = themeAmoled,
            sheetState = sheetState,
            shape = RoundedCornerShape(
                topStart = 22.0.dp,
                topEnd = 22.0.dp,
                bottomEnd = 0.0.dp,
                bottomStart = 0.0.dp
            ),
            hide = {
                finish()
            },
            sheetContent = { modifier ->
                SheetContent(resolveResult, modifier, event, interaction, coroutineScope, sheetState, controller)
            }
        )
    }

    @Composable
    private fun SheetContent(
        resolveResult: IntentResolveResult,
        modifier: Modifier,
        event: ResolveEvent,
        interaction: ResolverInteraction,
        coroutineScope: CoroutineScope,
        sheetState: CompatSheetState,
        controller: BottomSheetStateController,
    ) {
        when (resolveResult) {
            is IntentResolveResult.Pending -> {
                val expressiveLoadingSheet by viewModel.expressiveLoadingSheet.collectAsStateWithLifecycle()
                LoadingIndicatorWrapper(
                    expressiveLoadingSheet = expressiveLoadingSheet,
                    event = event,
                    interaction = interaction,
                    requestExpand = {
                        logger.info("Loading indicator: Pre-Request expand")
                        coroutineScope.launch {
                            logger.info("Loading indicator: Request expand")
                            sheetState.expand()
                        }
                    }
                )
            }

            is IntentResolveResult.Default -> {
                val enableIgnoreLibRedirectButton by viewModel.enableIgnoreLibRedirectButton.collectAsStateWithLifecycle()
                val bottomSheetProfileSwitcher by viewModel.bottomSheetProfileSwitcher.collectAsStateWithLifecycle()
                val urlCopiedToast by viewModel.urlCopiedToast.collectAsStateWithLifecycle()
                val downloadStartedToast by viewModel.downloadStartedToast.collectAsStateWithLifecycle()
                val hideAfterCopying by viewModel.hideAfterCopying.collectAsStateWithLifecycle()
                val bottomSheetNativeLabel by viewModel.bottomSheetNativeLabel.collectAsStateWithLifecycle()
                val gridLayout by viewModel.gridLayout.collectAsStateWithLifecycle()
                val previewUrl by viewModel.previewUrl.collectAsStateWithLifecycle()
                val hideBottomSheetChoiceButtons by viewModel.hideBottomSheetChoiceButtons.collectAsStateWithLifecycle()
                val alwaysShowPackageName by viewModel.alwaysShowPackageName.collectAsStateWithLifecycle()
                val manualFollowRedirects by viewModel.manualFollowRedirects.collectAsStateWithLifecycle()
                val improvedBottomSheetUrlDoubleTap by viewModel.improvedBottomSheetUrlDoubleTap.collectAsStateWithLifecycle()

                BottomSheetApps(
                    modifier = modifier,
                    result = resolveResult,
                    imageLoader = viewModel.imageLoader,
                    enableIgnoreLibRedirectButton = enableIgnoreLibRedirectButton,
                    enableSwitchProfile = bottomSheetProfileSwitcher,
                    profileSwitcher = viewModel.profileSwitcher,
                    enableUrlCopiedToast = urlCopiedToast,
                    enableDownloadStartedToast = downloadStartedToast,
                    enableManualRedirect = manualFollowRedirects,
                    hideAfterCopying = hideAfterCopying,
                    bottomSheetNativeLabel = bottomSheetNativeLabel,
                    gridLayout = gridLayout,
                    appListSelectedIdx = viewModel.appListSelectedIdx.intValue,
                    copyUrl = { label, url ->
                        viewModel.clipboardManager.setText(label, url)
                    },
                    startDownload = { url, downloadable ->
                        viewModel.startDownload(resources, url, downloadable)
                    },
                    isPrivateBrowser = ::isPrivateBrowser,
                    showToast = { textId, duration, _ ->
                        coroutineScope.launch { showToast(textId = textId, duration = duration) }
                    },
                    controller = controller,
                    showPackage = alwaysShowPackageName,
                    previewUrl = previewUrl,
                    hideBottomSheetChoiceButtons = hideBottomSheetChoiceButtons,
                    urlCardDoubleTap = improvedBottomSheetUrlDoubleTap
                )
            }

            is IntentResolveResult.IntentParseFailed -> {
                FailureSheetContentWrapper(
                    modifier = modifier,
                    exception = resolveResult.exception,
                    onShareClick = {},
                    onCopyClick = {},
                    onSearchClick = {

                    }
                )
            }

            is IntentResolveResult.ResolveUrlFailed, is IntentResolveResult.UrlModificationFailed -> {}
            is IntentResolveResult.WebSearch -> {}
            IntentResolveResult.NoScenarioFound -> {}
            else -> {}
        }
    }

    private suspend fun showToast(textId: Int, duration: Int = Toast.LENGTH_SHORT) {
        val text = getString(textId)
        withContext(Dispatchers.Main) {
            Toast.makeText(this@BottomSheetActivity, text, duration).show()
        }
    }

    private fun isPrivateBrowser(hasUri: Boolean, info: ActivityAppInfo): Browser? {
        if (!viewModel.enableRequestPrivateBrowsingButton.value || !hasUri) return null
        return viewModel.isKnownBrowser(info.packageName, privateOnly = true)
    }

    private suspend fun maybeHandleResult(result: IntentResolveResult?): LaunchIntent? {
        return when (result) {
            is IntentResolveResult.Default if result.hasAutoLaunchApp && result.app != null -> {
                viewModel.makeOpenAppIntent(
                    result.app,
                    result.intent,
                    referrer,
                    result.isRegularPreferredApp,
                    null,
                    false
                )
            }
            is IntentResolveResult.IntentResult -> LaunchRawIntent(result.intent)
            else -> null
        }
    }

    private suspend fun handleLaunch(intent: LaunchIntent) {
        val result = launchHandler.start(intent.intent)
        if (result !is LaunchFailure) return

        logger.error(result.ex, "Launch failed: $result")
        val textId = when (result) {
            is LaunchResult.Illegal -> R.string.bottom_sheet__text_launch_illegal
            is LaunchResult.NotAllowed -> R.string.bottom_sheet__text_launch_not_allowed
            is LaunchResult.Other -> R.string.bottom_sheet__text_launch_failure_other
            is LaunchResult.Unknown -> R.string.bottom_sheet__text_launch_failure_unknown
            is LaunchResult.NotFound -> R.string.resolve_activity_failure
        }

        showToast(textId)
    }

    override fun onStop() {
        super.onStop()
        logger.info("onStop")

        finish()
    }

    override fun onResume() {
        super.onResume()
        logger.info("onResume")
    }

    override fun onPause() {
        super.onPause()
        logger.info("onPause")
    }

    fun setInitialIntent(intent: Intent) {
        initialIntent.tryEmit(intent)
        latestNewIntent.tryEmit(null)
        viewModel.resolveAsync(intent.toSafeIntent(), referrer)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        logger.info("onNewIntent: $intent")

        latestNewIntent.tryEmit(intent)
        viewModel.resolveAsync(intent.toSafeIntent(), referrer)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (!viewModel.noBottomSheetStateSave.value) {
            super.onSaveInstanceState(outState)
        }
    }
}
