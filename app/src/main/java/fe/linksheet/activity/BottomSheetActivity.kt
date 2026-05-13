package fe.linksheet.activity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import app.linksheet.compose.extension.collectOnIO
import app.linksheet.feature.downloader.core.DownloaderMode
import app.linksheet.feature.libredirect.database.entity.LibRedirectDefault
import app.linksheet.feature.profile.core.switchTo
import app.linksheet.mozilla.components.support.base.log.logger.Logger
import app.linksheet.mozilla.components.support.utils.toSafeIntent
import fe.composekit.extension.setText
import fe.composekit.preference.collectAsStateWithLifecycle
import fe.linksheet.R
import fe.linksheet.activity.bottomsheet.AppInteraction
import fe.linksheet.activity.bottomsheet.BottomSheetApps
import fe.linksheet.activity.bottomsheet.BottomSheetInteraction
import fe.linksheet.activity.bottomsheet.BottomSheetStateController
import fe.linksheet.activity.bottomsheet.CopyUrlInteraction
import fe.linksheet.activity.bottomsheet.DefaultBottomSheetStateController
import fe.linksheet.activity.bottomsheet.IgnoreLibRedirectInteraction
import fe.linksheet.activity.bottomsheet.LaunchFailure
import fe.linksheet.activity.bottomsheet.LaunchHandler
import fe.linksheet.activity.bottomsheet.LaunchResult
import fe.linksheet.activity.bottomsheet.ManualDownloadInteraction
import fe.linksheet.activity.bottomsheet.ManualRedirectInteraction
import fe.linksheet.activity.bottomsheet.ShareUrlInteraction
import fe.linksheet.activity.bottomsheet.StartDownloadInteraction
import fe.linksheet.activity.bottomsheet.SwitchProfileInteraction
import fe.linksheet.activity.bottomsheet.compat.SettingsObserver
import fe.linksheet.activity.bottomsheet.compat.m3fix.M3FixModalBottomSheet
import fe.linksheet.activity.bottomsheet.compat.m3fix.rememberM3FixModalBottomSheetState
import fe.linksheet.activity.bottomsheet.content.failure.FailureSheetContentWrapper
import fe.linksheet.activity.bottomsheet.content.pending.LoadingIndicatorWrapper
import fe.linksheet.activity.bottomsheet.hideAndFinish
import fe.linksheet.composable.ui.AppTheme
import fe.linksheet.extension.android.showToast
import fe.linksheet.module.resolver.FollowRedirectsMode
import fe.linksheet.module.resolver.ImprovedIntentResolver
import fe.linksheet.module.resolver.IntentResolveResult
import fe.linksheet.module.resolver.ResolveEvent
import fe.linksheet.module.resolver.ResolveOptions
import fe.linksheet.module.resolver.ResolverInteraction
import fe.linksheet.module.resolver.util.LaunchIntent
import fe.linksheet.module.resolver.util.LaunchOtherProfileIntent
import fe.linksheet.module.resolver.util.Launchable
import fe.linksheet.module.viewmodel.BottomSheetViewModel
import fe.linksheet.util.intent.Intents
import fe.linksheet.util.intent.StandardIntents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent
import app.linksheet.compose.R as CommonR

//import relocated.androidx.compose.material3.SheetValue
//import relocated.androidx.compose.material3.rememberModalBottomSheetState

// Must not be moved or renamed since LinkSheetCompat hardcodes the package/name
class BottomSheetActivity : BaseComponentActivity(), KoinComponent {
    private val logger = Logger("BottomSheetActivity")
    private val viewModel by viewModel<BottomSheetViewModel>()

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Log.d(BottomSheetActivity::class.simpleName, "Received result for $result")
        // Apps may "refuse" to handle an intent and return back to LinkSheet instantly
        // * Amazon does this for Prime links, which results in a new intent being passed to onNewIntent
        // (and subsequently being written to latestNewIntent) and RESULT_CANCELED
        // * Hermit also sends RESULT_CANCELED for some reason, but doesn't provide a new intent first, meaning we can
        // still differentiate between a successful and a non-successful launch using the condition below
        if (result.resultCode == RESULT_OK || viewModel.latestNewIntentFlow.value == null) {
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

    private val editorLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode != RESULT_OK || result.data == null) return@registerForActivityResult

        val intent = result.data
            ?.getStringExtra(TextEditorActivity.EXTRA_TEXT)
            ?.toUri()
            ?.let { StandardIntents.createSelfIntent(it) }

        onNewIntent(intent!!)
    }

    private val isReducedMotionEnabledFlow by lazy {
        val settingsObserver = SettingsObserver(
            applicationContext = applicationContext,
            settingName = Settings.Global.ANIMATOR_DURATION_SCALE,
            getValue = { ctx, name -> Settings.Global.getFloat(ctx.contentResolver, name, 1.0f) },
            getUri = Settings.Global::getUriFor,
        )

        settingsObserver
            .createFlow()
            .map { it == 0.0f }
            .stateIn(
                scope = lifecycleScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = settingsObserver.readValue() == 0.0f
            )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.resolveResultFlow
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .mapNotNull(viewModel::maybeHandleResult)
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
        val currentIntent by viewModel.intentFlow.collectAsStateWithLifecycle()

        val coroutineScope = rememberCoroutineScope()
        val sheetState = rememberM3FixModalBottomSheetState(
            isReducedMotionEnabled = isReducedMotionEnabledFlow::value
        )
//        val sheetState = rememberModalBottomSheetState(
////            confirmValueChange = {
////                if(it == SheetValue.Hidden) true else true
////            }
//        )

        LaunchedEffect(key1 = resolveResult) {
            logger.debug("Expanding bottom sheet, status: $resolveResult, isPending=${resolveResult == IntentResolveResult.Pending}")
            if (resolveResult != IntentResolveResult.Pending) {
                // Need to do this in a separate effect as otherwise the preview image seems to mess up the layout-ing
                if (viewModel.expandFully.value) {
                    sheetState.expand()
                } else {
                    sheetState.partialExpand()
                }
            }
        }

        LaunchedEffect(key1 = interaction) {
            Log.d(
                "LoadingIndicatorSheetContent",
                "Interaction=$interaction, isClear=${interaction == ResolverInteraction.Clear}, " +
                        "isInitialized=${interaction == ResolverInteraction.Initialized}"
            )
            if (resolveResult == IntentResolveResult.Pending && interaction != ResolverInteraction.Initialized) {
                // Request resize on interaction change to accommodate interaction UI
                sheetState.expand()
            }
        }

        LaunchedEffect(key1 = event) {
            logger.debug("Latest event: $event")
        }

        val controller = remember {
            DefaultBottomSheetStateController(
                editorLauncher = editorLauncher,
                dispatch = { interaction ->
                    handleInteraction(
                        interaction = interaction,
                        resolveResult = resolveResult,
                        hideSheet = {
                            coroutineScope.launch { sheetState.hide() }
                        }
                    )
                },
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
                SheetContent(
                    resolveResult = resolveResult,
                    modifier = modifier,
                    event = event,
                    interaction = interaction,
                    controller = controller
                )
            }
        )
    }

    @Composable
    private fun SheetContent(
        resolveResult: IntentResolveResult,
        modifier: Modifier,
        event: ResolveEvent,
        interaction: ResolverInteraction,
        controller: BottomSheetStateController,
    ) {
        when (resolveResult) {
            is IntentResolveResult.Pending -> {
                LoadingIndicatorWrapper(
                    event = event,
                    interaction = interaction,
                )
            }

            is IntentResolveResult.Default -> {
                val enableIgnoreLibRedirectButton by viewModel.enableIgnoreLibRedirectButton.collectAsStateWithLifecycle()
                val bottomSheetProfileSwitcher by viewModel.bottomSheetProfileSwitcher.collectAsStateWithLifecycle()
                val bottomSheetNativeLabel by viewModel.bottomSheetNativeLabel.collectAsStateWithLifecycle()
                val gridLayout by viewModel.gridLayout.collectAsStateWithLifecycle()
                val previewUrl by viewModel.previewUrl.collectAsStateWithLifecycle()
                val hideBottomSheetChoiceButtons by viewModel.hideBottomSheetChoiceButtons.collectAsStateWithLifecycle()
                val alwaysShowPackageName by viewModel.alwaysShowPackageName.collectAsStateWithLifecycle()
                val followRedirectsEnabled by viewModel.followRedirectsEnabled.collectAsStateWithLifecycle()
                val followRedirectsMode by viewModel.followRedirectsMode.collectAsStateWithLifecycle()
                val downloaderEnable by viewModel.downloaderEnabled.collectAsStateWithLifecycle()
                val downloaderMode by viewModel.downloaderMode.collectAsStateWithLifecycle()
                val doubleTapUrl by viewModel.doubleTapUrl.collectAsStateWithLifecycle()

                BottomSheetApps(
                    modifier = modifier,
                    result = resolveResult,
                    imageLoader = viewModel.imageLoader,
                    enableDownloader = downloaderEnable,
                    enableIgnoreLibRedirectButton = enableIgnoreLibRedirectButton,
                    profiles = if (bottomSheetProfileSwitcher) viewModel.profileSwitcher.getProfiles() else null,
                    enableManualRedirect = followRedirectsEnabled && followRedirectsMode == FollowRedirectsMode.Manual,
                    enableManualDownload = downloaderEnable && downloaderMode == DownloaderMode.Manual,
                    bottomSheetNativeLabel = bottomSheetNativeLabel,
                    gridLayout = gridLayout,
                    appListSelectedIdx = viewModel.appListSelectedIdx.intValue,
                    isPrivateBrowser = viewModel::isPrivateBrowser,
                    controller = controller,
                    showPackage = alwaysShowPackageName,
                    previewUrl = previewUrl,
                    hideBottomSheetChoiceButtons = hideBottomSheetChoiceButtons,
                    urlCardDoubleTap = doubleTapUrl
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

    private fun handleInteraction(
        interaction: BottomSheetInteraction,
        resolveResult: IntentResolveResult,
        hideSheet: () -> Job
    ) {
        when (interaction) {
            is AppInteraction -> {
                lifecycleScope.launch {
                    if (interaction.info == null) {
                        showToast(R.string.something_went_wrong, Toast.LENGTH_SHORT)
                    } else {
                        (resolveResult as? IntentResolveResult.Default)
                            ?.let { viewModel.handle(this@BottomSheetActivity, it, interaction) }
                            ?.let { handleLaunch(it) }
                    }
                }
            }

            is SwitchProfileInteraction -> {
                hideAndFinish(hideSheet)
                viewModel.profileSwitcher.switchTo<BottomSheetActivity>(interaction.crossProfile, interaction.url, this)
            }

            is ManualRedirectInteraction -> {
                val intent = StandardIntents.createSelfIntent(
                    uri = interaction.uri.toUri(),
                    extras = Bundle().apply {
                        putBoolean(ImprovedIntentResolver.IntentKeyResolveRedirects, true)
                    }
                )
                onNewIntent(intent)
            }

            is ManualDownloadInteraction -> {
                val intent = StandardIntents.createSelfIntent(
                    uri = interaction.uri.toUri(),
                    extras = Bundle().apply {
                        putBoolean(ImprovedIntentResolver.IntentKeyDownloader, true)
                    }
                )
                onNewIntent(intent)
            }

            is IgnoreLibRedirectInteraction -> {
                val intent = StandardIntents.createSelfIntent(
                    uri = interaction.result.originalUri,
                    extras = Bundle().apply {
                        putBoolean(LibRedirectDefault.IgnoreIntentKey, true)
                    }
                )
                onNewIntent(intent)
            }

            is CopyUrlInteraction -> {
                val clipboardLabel = resources.getString(CommonR.string.generic__text_url)
                viewModel.clipboardManager.setText(clipboardLabel, interaction.url)
                if (viewModel.urlCopiedToast()) {
                    lifecycleScope.launch { showToast(R.string.url_copied, Toast.LENGTH_SHORT) }
                }

                if (viewModel.hideAfterCopying()) {
                    hideAndFinish(hideSheet)
                }
            }

            is StartDownloadInteraction -> {
                viewModel.startDownload(resources, interaction.url, interaction.downloadable)
                if (viewModel.downloadStartedToast()) {
                    lifecycleScope.launch { showToast(R.string.download_started, Toast.LENGTH_SHORT) }
                }

                if (viewModel.hideAfterCopying()) {
                    hideAndFinish(hideSheet)
                }
            }

            is ShareUrlInteraction -> {
                hideAndFinish(hideSheet)
                startActivity(Intent.createChooser(Intents.createShareUriIntent(interaction.url), null))
            }
        }
    }

    private suspend fun showToast(textId: Int, duration: Int = Toast.LENGTH_SHORT) {
        val text = getString(textId)
        withContext(Dispatchers.Main) {
            Toast.makeText(this@BottomSheetActivity, text, duration).show()
        }
    }

    private suspend fun handleLaunch(intent: Launchable) {
        when (intent) {
            is LaunchOtherProfileIntent -> {
                finish()
                viewModel.profileSwitcher.switchTo<BottomSheetActivity>(intent.profile, intent.url, this)
            }
            is LaunchIntent -> {
                val result = launchHandler.start(intent.intent)
                if (result !is LaunchFailure) return

                logger.error("Launch failed: $result", result.ex)
                val textId = when (result) {
                    is LaunchResult.Illegal -> R.string.bottom_sheet__text_launch_illegal
                    is LaunchResult.NotAllowed -> R.string.bottom_sheet__text_launch_not_allowed
                    is LaunchResult.Other -> R.string.bottom_sheet__text_launch_failure_other
                    is LaunchResult.Unknown -> R.string.bottom_sheet__text_launch_failure_unknown
                    is LaunchResult.NotFound -> R.string.resolve_activity_failure
                }

                showToast(textId)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        logger.debug("onStop")

        finish()
    }

    override fun onResume() {
        super.onResume()
        logger.debug("onResume")
    }

    override fun onPause() {
        super.onPause()
        logger.debug("onPause")
    }

    fun setInitialIntent(intent: Intent) {
        viewModel.tryEmitIntent(intent, null)

        val options = ResolveOptions(referrer, viewModel.getMetaData(this))
        viewModel.resolveAsync(intent.toSafeIntent(), options)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        logger.debug("onNewIntent: $intent")

        viewModel.tryEmitIntent(null, intent)

        val options = ResolveOptions(referrer, viewModel.getMetaData(this))
        viewModel.resolveAsync(intent.toSafeIntent(), options)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (!viewModel.noBottomSheetStateSave.value) {
            super.onSaveInstanceState(outState)
        }
    }
}
