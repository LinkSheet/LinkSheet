package fe.linksheet.experiment.improved.resolver.composable

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.fix.SheetValue
import androidx.compose.material3.fix.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import fe.kotlin.extension.iterable.getOrFirstOrNull
import fe.linksheet.R
import fe.linksheet.activity.BottomSheetActivity
import fe.linksheet.activity.bottomsheet.BottomSheetImpl
import fe.linksheet.activity.bottomsheet.button.ChoiceButtons
import fe.linksheet.activity.bottomsheet.column.GridBrowserButton
import fe.linksheet.activity.bottomsheet.column.ListBrowserColumn
import fe.linksheet.activity.bottomsheet.column.PreferredAppColumn
import fe.linksheet.composable.component.bottomsheet.ExperimentalFailureSheetColumn
import fe.linksheet.composable.ui.AppTheme
import fe.linksheet.composable.ui.HkGroteskFontFamily
import fe.linksheet.composable.ui.LocalActivity
import fe.linksheet.experiment.improved.resolver.ImprovedIntentResolver
import fe.linksheet.experiment.improved.resolver.IntentResolveResult
import fe.linksheet.experiment.improved.resolver.LoopDetectorExperiment
import fe.linksheet.experiment.improved.resolver.util.ReferrerHelper
import fe.linksheet.extension.android.setText
import fe.linksheet.extension.android.showToast
import fe.linksheet.extension.koin.injectLogger
import fe.linksheet.interconnect.LinkSheetConnector
import fe.linksheet.module.intent.BottomSheetStateController
import fe.linksheet.module.intent.DefaultBottomSheetStateController
import fe.linksheet.module.resolver.KnownBrowser
import fe.linksheet.module.viewmodel.BottomSheetViewModel
import fe.linksheet.resolver.BottomSheetResult
import fe.linksheet.resolver.DisplayActivityInfo
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import mozilla.components.support.utils.SafeIntent
import mozilla.components.support.utils.toSafeIntent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class ImprovedBottomSheet(
    private val loopDetectorExperiment: LoopDetectorExperiment?,
    val activity: BottomSheetActivity,
    val viewModel: BottomSheetViewModel,
    val initialIntent: SafeIntent,
    val referrer: Uri?,
) : BottomSheetImpl(), KoinComponent {
    private val logger by injectLogger<ImprovedBottomSheet>()
    private val resolver by inject<ImprovedIntentResolver>()


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
                        LoadingIndicator(
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
                            isExpanded = sheetState.currentValue == SheetValue.Expanded,
                            requestExpand = {},
                            controller = controller,
                            showPackage = viewModel.alwaysShowPackageName(),
                            previewUrl = viewModel.previewUrl(),
                            hideBottomSheetChoiceButtons = viewModel.hideBottomSheetChoiceButtons(),
                            urlCardDoubleTap = viewModel.improvedBottomSheetUrlDoubleTap()
                        )
                    }

                    is IntentResolveResult.IntentParseFailed, is IntentResolveResult.ResolveUrlFailed, is IntentResolveResult.UrlModificationFailed -> {
                        ExperimentalFailureSheetColumn(
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

    @Composable
    private fun BottomSheetApps(
        result: IntentResolveResult.Default,
        enableIgnoreLibRedirectButton: Boolean,
        enableSwitchProfile: Boolean,
        isExpanded: Boolean,
        requestExpand: () -> Unit,
        controller: BottomSheetStateController,
        showPackage: Boolean,
        previewUrl: Boolean,
        hideBottomSheetChoiceButtons: Boolean,
        urlCardDoubleTap: Boolean,
    ) {
        if (previewUrl && result.uri != null) {
            UrlBarWrapper(
                profileSwitcher = viewModel.profileSwitcher,
                result = result,
                enableIgnoreLibRedirectButton = enableIgnoreLibRedirectButton,
                enableSwitchProfile = enableSwitchProfile,
                enableUrlCopiedToast = viewModel.urlCopiedToast(),
                enableDownloadStartedToast = viewModel.downloadStartedToast(),
                enableUrlCardDoubleTap = urlCardDoubleTap,
                enableManualRedirect = viewModel.manualFollowRedirects(),
                hideAfterCopying = viewModel.hideAfterCopying(),
                controller = controller,
                showToast = { id -> showToast(id) },
                copyUrl = { label, url ->
                    viewModel.clipboardManager.setText(label, url)
                },
                startDownload = { url, downloadable ->
                    viewModel.startDownload(activity.resources, url, downloadable)
                },
                launchApp = { app, intent, modifier ->
                    viewModel.viewModelScope.launch {
                        handleLaunch(viewModel.makeOpenAppIntent(app, intent, modifier))
                    }
                }
            )
        }

        if (result.filteredItem != null) {
            val privateBrowser = isPrivateBrowser(result.uri != null, result.filteredItem)

            PreferredAppColumn(
                appInfo = result.filteredItem,
                privateBrowser = privateBrowser,
                preferred = true,
                showPackage = showPackage,
                hideBottomSheetChoiceButtons = hideBottomSheetChoiceButtons,
                onClick = { _, modifier ->
                    viewModel.viewModelScope.launch {
                        handleLaunch(viewModel.makeOpenAppIntent(result.filteredItem, result.intent, modifier))
                    }
                }
            )

            // TODO: Not sure if this divider should be kept
            if (result.resolved.isNotEmpty()) {
                HorizontalDivider(
                    modifier = Modifier.padding(start = 15.dp, end = 15.dp, top = 5.dp, bottom = 10.dp),
                    color = MaterialTheme.colorScheme.outline.copy(0.25f)
                )

                Text(
                    modifier = Modifier.padding(start = 15.dp),
                    text = stringResource(id = R.string.use_a_different_app),
                    fontFamily = HkGroteskFontFamily,
                    fontWeight = FontWeight.SemiBold,
                )

                Spacer(modifier = Modifier.height(10.dp))
            }
        } else {
            Row(modifier = Modifier.padding(horizontal = 15.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(id = R.string.open_with),
                    fontFamily = HkGroteskFontFamily,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        if (result.resolved.isNotEmpty()) {
            if (viewModel.gridLayout()) {
                Grid(
                    result = result,
                    hasPreferredApp = result.filteredItem != null,
                    hideChoiceButtons = viewModel.hideBottomSheetChoiceButtons(),
                    isExpanded = isExpanded,
                    requestExpand = requestExpand,
                    showPackage = showPackage
                )
            } else {
                List(
                    result = result,
                    hasPreferredApp = result.filteredItem != null,
                    hideChoiceButtons = viewModel.hideBottomSheetChoiceButtons(),
                    showNativeLabel = viewModel.bottomSheetNativeLabel(),
                    isExpanded = isExpanded,
                    requestExpand = requestExpand,
                    showPackage = showPackage
                )
            }
        }
    }


    data class GridItem(val info: DisplayActivityInfo, val privateBrowsingBrowser: KnownBrowser? = null) {
        override fun toString(): String {
            return info.flatComponentName + (privateBrowsingBrowser?.hashCode() ?: -1)
        }
    }

    // TODO: Grid and List are pretty similar, refactor maybe?
    @Composable
    private fun Grid(
        result: BottomSheetResult.SuccessResult,
        hasPreferredApp: Boolean,
        hideChoiceButtons: Boolean,
        isExpanded: Boolean,
        requestExpand: () -> Unit,
        showPackage: Boolean,
    ) {
        val items = mutableListOf<GridItem>()

        for (info in result.resolved) {
            items.add(GridItem(info))

            val privateBrowser = isPrivateBrowser(result.uri != null, info)
            if (privateBrowser != null) {
                items.add(GridItem(info, privateBrowser))
            }
        }

        val activity = LocalActivity.current

        Column {
            LazyVerticalGrid(modifier = Modifier.fillMaxWidth(), columns = GridCells.Adaptive(85.dp)) {
                itemsIndexed(items = items, key = { _, item -> item.toString() }) { index, (info, privateBrowser) ->
                    GridBrowserButton(
                        appInfo = info,
                        selected = if (!hasPreferredApp) index == viewModel.appListSelectedIdx.intValue else null,
                        onClick = { type, modifier ->
                            viewModel.viewModelScope.launch {
                                val intent = viewModel.handleClick(
                                    activity,
                                    index,
                                    isExpanded,
                                    requestExpand,
                                    result.intent,
                                    info,
                                    type,
                                    modifier
                                )

                                if (intent != null) {
                                    handleLaunch(intent)
                                }
                            }
                        },
                        privateBrowser = privateBrowser,
                        showPackage = showPackage
                    )
                }
            }

            if (!hasPreferredApp && !hideChoiceButtons) {
                NoPreferredAppChoiceButtons(result = result, selected = viewModel.appListSelectedIdx.intValue)
            }
        }
    }

    @Composable
    private fun List(
        result: BottomSheetResult.SuccessResult,
        hasPreferredApp: Boolean,
        hideChoiceButtons: Boolean,
        showNativeLabel: Boolean,
        isExpanded: Boolean,
        requestExpand: () -> Unit,
        showPackage: Boolean,
    ) {
        val activity = LocalActivity.current

        Column(modifier = Modifier.wrapContentHeight()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.0f, fill = false)
            ) {
                itemsIndexed(items = result.resolved, key = { _, item -> item.flatComponentName }) { index, info ->
                    val privateBrowser = isPrivateBrowser(result.uri != null, info)

                    ListBrowserColumn(
                        appInfo = info,
                        selected = if (!hasPreferredApp) index == viewModel.appListSelectedIdx.intValue else null,
                        onClick = { type, modifier ->
                            viewModel.viewModelScope.launch {
                                val intent = viewModel.handleClick(
                                    activity, index, isExpanded,
                                    requestExpand, result.intent, info, type, modifier
                                )

                                if (intent != null) {
                                    handleLaunch(intent)
                                }
                            }
                        },
                        preferred = false,
                        privateBrowser = privateBrowser,
                        showPackage = showPackage,
                        showNativeLabel = showNativeLabel
                    )

                    // TODO: Selector?
//                    Box(
//                        modifier = Modifier.fillMaxWidth(),
//                        contentAlignment = Alignment.CenterEnd
//                    ) {
//                        if (selectedItem == index && !hasPreferredApp) {
//                            Icon(
//                                imageVector = Icons.Default.CheckCircle,
//                                contentDescription = null,
//                                modifier = Modifier.align(Alignment.CenterEnd)
//                            )
//                        }
//                    }
                }
            }

            if (!hasPreferredApp && !hideChoiceButtons) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
//                        .wrapContentHeight()
//                        .weight(0.1f)

//                    .padding(padding)
//                        .padding(bottom = 40.dp)
//                        .border(1.dp, Color.Magenta)
                ) {
                    NoPreferredAppChoiceButtons(result = result, selected = viewModel.appListSelectedIdx.intValue)
                }
            }
        }
    }

    @Composable
    private fun NoPreferredAppChoiceButtons(result: BottomSheetResult.SuccessResult, selected: Int) {
        Spacer(modifier = Modifier.height(5.dp))

        ChoiceButtons(
            enabled = selected != -1,
            choiceClick = { _, modifier ->
                val info = result.resolved.getOrFirstOrNull(selected)
                if (info == null) {
                    showToast(R.string.something_went_wrong, uiThread = true)
                    return@ChoiceButtons
                }

                viewModel.viewModelScope.launch {
                    handleLaunch(viewModel.makeOpenAppIntent(info, result.intent, modifier))
                }
            },
        )
    }

    private fun isPrivateBrowser(hasUri: Boolean, info: DisplayActivityInfo): KnownBrowser? {
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

        if (loopDetectorExperiment == null) {
            if (viewModel.safeStartActivity(activity, intent)) {
                finish()
            } else {
                showToast(R.string.resolve_activity_failure, uiThread = true)
            }
        } else {
            if (!loopDetectorExperiment.start(intent)) {
                showToast(R.string.resolve_activity_failure, uiThread = true)
            }
        }
    }

    override fun onStop() {
        finish()
    }

    override fun onNewIntent(intent: Intent) {
//        if (loopDetectorExperiment != null) {
        intentFlow.value = intent.toSafeIntent()
        resolveResultFlow.value = IntentResolveResult.Pending
//        }
    }
}
