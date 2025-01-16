package fe.linksheet.activity.bottomsheet

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.fix.SheetValue
import androidx.compose.material3.fix.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import fe.linksheet.R
import fe.linksheet.activity.BottomSheetActivity
import fe.linksheet.activity.bottomsheet.content.pending.LoadingIndicatorSheetContent
import fe.linksheet.activity.bottomsheet.content.failure.ExperimentalFailureSheetContent
import fe.linksheet.composable.ui.AppTheme
import fe.linksheet.module.resolver.ImprovedIntentResolver
import fe.linksheet.module.resolver.IntentResolveResult
import fe.linksheet.module.resolver.util.ReferrerHelper
import fe.linksheet.extension.android.setText
import fe.linksheet.extension.android.showToast
import fe.linksheet.extension.koin.injectLogger
import fe.linksheet.interconnect.LinkSheetConnector
import fe.linksheet.module.app.ActivityAppInfo
import fe.linksheet.module.resolver.KnownBrowser
import fe.linksheet.module.viewmodel.BottomSheetViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import mozilla.components.support.utils.SafeIntent
import mozilla.components.support.utils.toSafeIntent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class ImprovedBottomSheet(
    private val loopDetector: LoopDetector?,
    val activity: BottomSheetActivity,
    val viewModel: BottomSheetViewModel,
    val initialIntent: SafeIntent,
    val referrer: Uri?,
) : BottomSheetImpl(), KoinComponent {
    private val logger by injectLogger<ImprovedBottomSheet>()
    private val resolver by inject<ImprovedIntentResolver>()

    companion object {
        val preferredAppItemHeight = 60.dp
        val buttonPadding = 15.dp
        val buttonRowHeight = 50.dp
    }

    private fun finish() {
        activity.finish()
    }

    private fun startActivity(intent: Intent) {
        activity.startActivity(intent)
    }

    private fun showToast(textId: Int, duration: Int = Toast.LENGTH_SHORT, uiThread: Boolean = false) {
        activity.showToast(textId = textId, duration = duration, uiThread = uiThread)
    }

    private fun showToast(text: String, duration: Int = Toast.LENGTH_SHORT, uiThread: Boolean = false) {
        activity.showToast(text = text, duration = duration, uiThread = uiThread)
    }

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        activity.setContent(edgeToEdge = true) {
            AppTheme { Wrapper() }
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

    private val intentFlow = MutableStateFlow(initialIntent)
    private val resolveResultFlow = MutableStateFlow<IntentResolveResult>(IntentResolveResult.Pending)

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun Wrapper() {
        var sheetOpen by rememberSaveable { mutableStateOf(true) }
        val status by resolveResultFlow.collectAsStateWithLifecycle()
        val currentIntent by intentFlow.collectAsStateWithLifecycle()

        val coroutineScope = rememberCoroutineScope()

        val sheetState = rememberModalBottomSheetState {
//            if(it != SheetValue.Hidden) {
//                return@rememberModalBottomSheetState true
//            }
            true
        }

        LaunchedEffect(key1 = resolver, key2 = currentIntent) {
//            logger.info("intent=$intent, newIntent=${safeIntent.unsafe}, status=$status")
//            if (status !is IntentResolveResult.Pending) {
//                sheetState.hide()
//            }

            val resolveResult = resolver.resolve(currentIntent, referrer)
            val completed = resolveResult as? IntentResolveResult.Default
            if (completed?.hasAutoLaunchApp == true && completed.app != null) {
                val intent = viewModel.makeOpenAppIntent(
                    completed.app,
                    completed.intent,
                    completed.isRegularPreferredApp,
                    null,
                    false
                )

                return@LaunchedEffect handleLaunch(intent)
            }

            resolveResultFlow.value = resolveResult
        }

        LaunchedEffect(key1 = status) {
            // Need to do this in a separate effect as otherwise the preview image seems to mess up the layout-ing
            if (viewModel.improvedBottomSheetExpandFully()) {
                sheetState.expand()
            } else {
                sheetState.partialExpand()
            }
        }

        val controller = remember {
            DefaultBottomSheetStateController(activity, coroutineScope, sheetState, ::onNewIntent)
        }

        val configuration = LocalConfiguration.current
        val landscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE


//        sheetState
//        if(sheetOpen){
        ImprovedBottomDrawer(
            contentModifier = if (viewModel.interceptAccidentalTaps()) {
                Modifier.pointerInput(Unit) {
                    interceptTap { !sheetState.isAnimationRunning }
                }
            } else Modifier,
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
            sheetContent = {
                when (status) {
                    is IntentResolveResult.Pending -> {
                        LoadingIndicatorSheetContent(
                            events = resolver.events,
                            interactions = resolver.interactions,
                            requestExpand = { coroutineScope.launch { sheetState.expand() } }
                        )
                    }

                    is IntentResolveResult.Default -> {
                        BottomSheetApps(
                            result = status as IntentResolveResult.Default,
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
                                viewModel.viewModelScope.launch {
                                    handleLaunch(viewModel.makeOpenAppIntent(app, intent, modifier))
                                }
                            },
                            launch2 = { index, info, type, modifier ->
                                viewModel.viewModelScope.launch {
                                    val intent = viewModel.handleClick(
                                        activity, index, sheetState.currentValue == SheetValue.Expanded,
                                        {  }, (status as IntentResolveResult.Default).intent, info, type, modifier
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
                                viewModel.startDownload(activity.resources, url, downloadable)
                            },
                            isPrivateBrowser = ::isPrivateBrowser,
                            showToast = ::showToast,
                            isExpanded = sheetState.currentValue == SheetValue.Expanded,
                            requestExpand = {  },
                            controller = controller,
                            showPackage = viewModel.alwaysShowPackageName(),
                            previewUrl = viewModel.previewUrl(),
                            hideBottomSheetChoiceButtons = viewModel.hideBottomSheetChoiceButtons(),
                            urlCardDoubleTap = viewModel.improvedBottomSheetUrlDoubleTap()
                        )
                    }

                    is IntentResolveResult.IntentParseFailed, is IntentResolveResult.ResolveUrlFailed, is IntentResolveResult.UrlModificationFailed -> {
                        ExperimentalFailureSheetContent(
                            data = currentIntent.dataString,
                            onShareClick = {},
                            onCopyClick = {}
                        )
                    }

                    is IntentResolveResult.WebSearch -> {

                    }
                }
            }
        )
//        }
    }

    private fun isPrivateBrowser(hasUri: Boolean, info: ActivityAppInfo): KnownBrowser? {
        if (!viewModel.enableRequestPrivateBrowsingButton() || !hasUri) return null
        return KnownBrowser.isKnownBrowser(info.packageName, privateOnly = true)
    }

    private fun handleLaunch(intent: Intent) {
        val showAsReferrer = viewModel.showAsReferrer()

        intent.putExtra(
            LinkSheetConnector.EXTRA_REFERRER,
            if (showAsReferrer) ReferrerHelper.createReferrer(activity) else referrer
        )

        if (!showAsReferrer) {
            intent.putExtra(Intent.EXTRA_REFERRER, referrer)
        }

        if (loopDetector == null) {
            if (viewModel.safeStartActivity(activity, intent)) {
                finish()
            } else {
                showToast(R.string.resolve_activity_failure, uiThread = true)
            }
        } else {
            if (!loopDetector.start(intent)) {
                showToast(R.string.resolve_activity_failure, uiThread = true)
            }
        }
    }

    override fun onStop() {
        finish()
    }

    override fun onNewIntent(intent: Intent) {
        intentFlow.value = intent.toSafeIntent()
        resolveResultFlow.value = IntentResolveResult.Pending
    }
}
