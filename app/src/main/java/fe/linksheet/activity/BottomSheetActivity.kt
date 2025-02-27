package fe.linksheet.activity

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.fix.SheetValue
import androidx.compose.material3.fix.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import fe.linksheet.R
import fe.linksheet.activity.bottomsheet.BottomSheetApps
import fe.linksheet.activity.bottomsheet.DefaultBottomSheetStateController
import fe.linksheet.activity.bottomsheet.ImprovedBottomDrawer
import fe.linksheet.activity.bottomsheet.content.failure.FailureSheetContentWrapper
import fe.linksheet.activity.bottomsheet.content.pending.LoadingIndicatorSheetContent
import fe.linksheet.composable.ui.AppTheme
import fe.linksheet.composable.util.debugBorder
import fe.linksheet.extension.android.setText
import fe.linksheet.extension.android.showToast
import fe.linksheet.extension.koin.injectLogger
import fe.linksheet.extension.kotlin.collectOnIO
import fe.linksheet.interconnect.LinkSheetConnector
import fe.linksheet.module.app.ActivityAppInfo
import fe.linksheet.module.resolver.IntentResolveResult
import fe.linksheet.module.resolver.KnownBrowser
import fe.linksheet.module.resolver.util.ReferrerHelper
import fe.linksheet.module.viewmodel.BottomSheetViewModel
import fe.linksheet.util.intent.Intents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
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
    companion object {
        val preferredAppItemHeight = 60.dp
        val buttonPadding = 15.dp
        val buttonRowHeight = 50.dp
    }

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
        if (result.resultCode == Activity.RESULT_OK || latestNewIntent.value == null) {
            finish()
        } else {
            showToast(
                textId = R.string.bottom_sheet__event_app_refusal,
                duration = Toast.LENGTH_LONG,
                uiThread = true
            )
        }
    }

    val editorLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK && result.data != null) {
            val uri = result.data?.getStringExtra(TextEditorActivity.EXTRA_TEXT)
                ?.let { Uri.parse(it) }
            onNewIntent(Intents.createSelfIntent(uri))
        }
    }

    private val intentFlow = MutableStateFlow(initialIntent.value)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.resolveResultFlow
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .mapNotNull { it as? IntentResolveResult.Default }
                .filter { it.hasAutoLaunchApp && it.app != null }
                .map {
                    viewModel.makeOpenAppIntent(it.app!!, it.intent, it.isRegularPreferredApp, null, false)
                }
                .collectLatest {
                    handleLaunch(it)
                }
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
        val sheetState = rememberModalBottomSheetState(
//            confirmValueChange = {
//                if(it == SheetValue.Hidden) true else true
//            }
        )

        LaunchedEffect(key1 = resolveResult) {
            logger.info("Expanding bottom sheet, status: $resolveResult, isPending=${resolveResult == IntentResolveResult.Pending}")
            if (resolveResult != IntentResolveResult.Pending) {
                // Need to do this in a separate effect as otherwise the preview image seems to mess up the layout-ing
                if (viewModel.improvedBottomSheetExpandFully()) {
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
                this@BottomSheetActivity,
                editorLauncher,
                coroutineScope,
                sheetState,
                ::onNewIntent
            )
        }

        val configuration = LocalConfiguration.current
        val landscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE


//        sheetState
//        if(sheetOpen){
        ImprovedBottomDrawer(
            contentModifier = (if (viewModel.interceptAccidentalTaps()) {
                Modifier.pointerInput(Unit) {
                    interceptTap { !sheetState.isAnimationRunning }
                }
            } else Modifier)
                .debugBorder(1.dp, Color.Red),
            landscape = landscape,
            // TODO: Replace with pref
            isBlackTheme = false,
            sheetState = sheetState,
            shape = RoundedCornerShape(
                topStart = 22.0.dp,
                topEnd = 22.0.dp,
                bottomEnd = 0.0.dp,
                bottomStart = 0.0.dp
            ),
            hide = {
                finish()
//                sheetOpen = false
            },
            sheetContent = { modifier ->
                when (resolveResult) {
                    is IntentResolveResult.Pending -> {
                        LoadingIndicatorSheetContent(
                            modifier = modifier,
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
                        BottomSheetApps(
                            modifier = modifier,
                            result = resolveResult as IntentResolveResult.Default,
                            enableIgnoreLibRedirectButton = viewModel.enableIgnoreLibRedirectButton(),
                            enableSwitchProfile = viewModel.bottomSheetProfileSwitcher(),
                            profileSwitcher = viewModel.profileSwitcher,
                            enableUrlCopiedToast = viewModel.urlCopiedToast(),
                            enableDownloadStartedToast = viewModel.downloadStartedToast(),
                            enableManualRedirect = viewModel.manualFollowRedirects(),
                            hideAfterCopying = viewModel.hideAfterCopying(),
                            bottomSheetNativeLabel = viewModel.bottomSheetNativeLabel(),
                            gridLayout = viewModel.gridLayout(),
                            appListSelectedIdx = viewModel.appListSelectedIdx.intValue,
                            launchApp = { app, intent, modifier ->
                                coroutineScope.launch {
                                    handleLaunch(viewModel.makeOpenAppIntent(app, intent, modifier))
                                }
                            },
                            launch2 = { index, info, type, modifier ->
                                coroutineScope.launch {
                                    val intent = viewModel.handleClick(
                                        this@BottomSheetActivity, index, sheetState.currentValue == SheetValue.Expanded,
                                        { }, (resolveResult as IntentResolveResult.Default).intent, info, type, modifier
                                    )

                                    if (intent != null) {
                                        handleLaunch(intent)
                                    }
                                }
                            },
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
                            showPackage = viewModel.alwaysShowPackageName(),
                            previewUrl = viewModel.previewUrl(),
                            hideBottomSheetChoiceButtons = viewModel.hideBottomSheetChoiceButtons(),
                            urlCardDoubleTap = viewModel.improvedBottomSheetUrlDoubleTap()
                        )
                    }

                    is IntentResolveResult.IntentParseFailed -> {
                        FailureSheetContentWrapper(
                            modifier = modifier,
                            exception = (resolveResult as IntentResolveResult.IntentParseFailed).exception,
                            onShareClick = {},
                            onCopyClick = {},
                            onSearchClick = {

                            }
                        )
                    }

                    is IntentResolveResult.ResolveUrlFailed, is IntentResolveResult.UrlModificationFailed -> {

                    }

                    is IntentResolveResult.WebSearch -> {

                    }

                    else -> {}
                }
            }
        )
//        }
    }


    private suspend fun showToast(textId: Int, duration: Int = Toast.LENGTH_SHORT) {
        val text = getString(textId)
        withContext(Dispatchers.Main) {
            Toast.makeText(this@BottomSheetActivity, text, duration).show()
        }
    }


    // https://stackoverflow.com/a/76168038
    private suspend fun PointerInputScope.interceptTap(
        pass: PointerEventPass = PointerEventPass.Initial,
        shouldCancel: (PointerEvent) -> Boolean,
    ) = coroutineScope {
        awaitEachGesture {
            val down = awaitFirstDown(pass = pass)
            val downTime = System.currentTimeMillis()
            val tapTimeout = viewConfiguration.longPressTimeoutMillis

            do {
                val event = awaitPointerEvent(pass)
                if (shouldCancel(event)) break

                val currentTime = System.currentTimeMillis()

                if (event.changes.size != 1) break // More than one event: not a tap
                if (currentTime - downTime >= tapTimeout) break // Too slow: not a tap

                val change = event.changes[0]

                if (change.id == down.id && !change.pressed) {
                    change.consume()
                }
            } while (event.changes.any { it.id == down.id && it.pressed })
        }
    }

    private fun isPrivateBrowser(hasUri: Boolean, info: ActivityAppInfo): KnownBrowser? {
        if (!viewModel.enableRequestPrivateBrowsingButton() || !hasUri) return null
        return KnownBrowser.isKnownBrowser(info.packageName, privateOnly = true)
    }

    private suspend fun handleLaunch(intent: Intent) {
        val showAsReferrer = viewModel.showAsReferrer()

        intent.putExtra(
            LinkSheetConnector.EXTRA_REFERRER,
            if (showAsReferrer) ReferrerHelper.createReferrer(this) else referrer
        )

        if (!showAsReferrer) {
            intent.putExtra(Intent.EXTRA_REFERRER, referrer)
        }

//        if (loopDetector == null) {
//            if (viewModel.safeStartActivity(activity, intent)) {
//                finish()
//            } else {
//                showToast(R.string.resolve_activity_failure, uiThread = true)
//            }
//        } else {
//            if (!loopDetector.start(intent)) {
//                showToast(R.string.resolve_activity_failure, uiThread = true)
//            }
//        }
        if (!start(intent)) {
            showToast(R.string.resolve_activity_failure, Toast.LENGTH_SHORT)
        }
    }

    fun start(intent: Intent): Boolean {
        try {
            launcher.launch(intent, ActivityOptionsCompat.makeBasic())
            return true
        } catch (e: ActivityNotFoundException) {
            return false
        }
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
        if (!viewModel.noBottomSheetStateSave()) {
            super.onSaveInstanceState(outState)
        }
    }
}
