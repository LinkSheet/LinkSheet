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
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import app.linksheet.compose.debug.LocalUiDebug
import app.linksheet.compose.debugBorder
import app.linksheet.compose.extension.collectOnIO
import app.linksheet.feature.libredirect.database.entity.LibRedirectDefault
import app.linksheet.feature.profile.core.switchTo
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
import fe.linksheet.module.resolver.*
import fe.linksheet.module.resolver.util.LaunchIntent
import fe.linksheet.module.resolver.util.LaunchOtherProfileIntent
import fe.linksheet.module.resolver.util.Launchable
import fe.linksheet.module.viewmodel.BottomSheetViewModel
import fe.linksheet.util.intent.Intents
import fe.linksheet.util.intent.StandardIntents
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.mapNotNull
import mozilla.components.support.base.log.logger.Logger
import mozilla.components.support.utils.toSafeIntent
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent

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
        val sheetState = rememberM3FixModalBottomSheetState()
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

        LaunchedEffect(key1 = event) {
            logger.debug("Latest event: $event")
        }

        val controller = remember {
            val hideSheet = {
                coroutineScope.launch { sheetState.hide() }
            }

            DefaultBottomSheetStateController(
                editorLauncher = editorLauncher,
                dispatch = { interaction ->
                    handleInteraction(interaction, resolveResult, hideSheet)
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
                LoadingIndicatorWrapper(
                    event = event,
                    interaction = interaction,
                    requestExpand = {
                        logger.debug("Loading indicator: Pre-Request expand")
                        coroutineScope.launch {
                            logger.debug("Loading indicator: Request expand")
                            sheetState.expand()
                        }
                    }
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
                val followRedirectsMode by viewModel.followRedirectsMode.collectAsStateWithLifecycle()
                val doubleTapUrl by viewModel.doubleTapUrl.collectAsStateWithLifecycle()

                BottomSheetApps(
                    modifier = modifier,
                    result = resolveResult,
                    imageLoader = viewModel.imageLoader,
                    enableIgnoreLibRedirectButton = enableIgnoreLibRedirectButton,
                    profiles = if (bottomSheetProfileSwitcher) viewModel.profileSwitcher.getProfiles() else null,
                    enableManualRedirect = followRedirectsMode == FollowRedirectsMode.Manual,
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
                    extras = bundleOf(ImprovedIntentResolver.IntentKeyResolveRedirects to true)
                )
                onNewIntent(intent)
            }

            is IgnoreLibRedirectInteraction -> {
                val intent = StandardIntents.createSelfIntent(
                    uri = interaction.result.originalUri,
                    extras = bundleOf(LibRedirectDefault.IgnoreIntentKey to true)
                )
                onNewIntent(intent)
            }

            is CopyUrlInteraction -> {
                val clipboardLabel = resources.getString(R.string.generic__text_url)
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
