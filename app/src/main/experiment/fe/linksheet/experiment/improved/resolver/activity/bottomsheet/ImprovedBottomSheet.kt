package fe.linksheet.experiment.improved.resolver.activity.bottomsheet

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.CrossProfileApps
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.getSystemService
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import fe.kotlin.extension.iterable.getOrFirstOrNull
import fe.linksheet.R
import fe.linksheet.activity.BottomSheetActivity
import fe.linksheet.activity.bottomsheet.BottomSheetImpl
import fe.linksheet.activity.bottomsheet.button.ChoiceButtons
import fe.linksheet.activity.bottomsheet.column.ClickModifier
import fe.linksheet.activity.bottomsheet.column.GridBrowserButton
import fe.linksheet.activity.bottomsheet.column.ListBrowserColumn
import fe.linksheet.activity.bottomsheet.column.PreferredAppColumn
import fe.linksheet.experiment.improved.resolver.ImprovedIntentResolver
import fe.linksheet.experiment.improved.resolver.IntentResolveResult
import fe.linksheet.experiment.improved.resolver.material3.SheetValue
import fe.linksheet.activity.bottomsheet.UrlBar
import fe.linksheet.extension.android.setText
import fe.linksheet.extension.android.shareUri
import fe.linksheet.extension.android.showToast
import fe.linksheet.interconnect.LinkSheetConnector
import fe.linksheet.module.database.entity.LibRedirectDefault
import fe.linksheet.module.downloader.DownloadCheckResult
import fe.linksheet.module.resolver.KnownBrowser
import fe.linksheet.module.resolver.LibRedirectResult
import fe.linksheet.module.viewmodel.BottomSheetViewModel
import fe.linksheet.resolver.BottomSheetResult
import fe.linksheet.resolver.DisplayActivityInfo
import fe.linksheet.ui.AppTheme
import fe.linksheet.ui.HkGroteskFontFamily
import fe.linksheet.ui.LocalActivity
import fe.linksheet.util.AndroidVersion
import fe.linksheet.util.UriUtil
import fe.linksheet.util.selfIntent
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import mozilla.components.support.utils.toSafeIntent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class ImprovedBottomSheet(
    val activity: BottomSheetActivity,
    val viewModel: BottomSheetViewModel,
    val intent: Intent,
    val referrer: Uri?,
) : BottomSheetImpl(), KoinComponent {
    private val resolver by inject<ImprovedIntentResolver>()

    private inline fun <reified T : Any> getSystemService(): T? {
        return activity.getSystemService<T>()
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun Wrapper() {
        var status by remember { mutableStateOf<IntentResolveResult>(IntentResolveResult.Pending) }

        // TODO: Use intent and referrer as keys?
        LaunchedEffect(key1 = resolver) {
            status = resolver.resolve(intent.toSafeIntent(), referrer)
        }

//        LaunchedEffect(key1 = status) {
////            Toast.makeText(this@ImprovedBottomSheetActivity, "Status: $status", Toast.LENGTH_SHORT).show()
//            Log.d("BottomSheet", "Status: ${status.javaClass.simpleName}")
//        }

        val configuration = LocalConfiguration.current
        val landscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        val coroutineScope = rememberCoroutineScope()
        val drawerState = fe.linksheet.experiment.improved.resolver.material3.rememberModalBottomSheetState()

        LaunchedEffect(key1 = status) {
            if (viewModel.improvedBottomSheetExpandFully()) {
                drawerState.expand()
            } else {
                drawerState.partialExpand()
            }
        }

        val hide: () -> Unit = {
            coroutineScope.launch { drawerState.hide() }.invokeOnCompletion { finish() }
        }

        ImprovedBottomDrawer(
            landscape = landscape,
            // TODO: Replace with pref
            isBlackTheme = false,
            drawerState = drawerState,
            shape = RoundedCornerShape(
                topStart = 22.0.dp,
                topEnd = 22.0.dp,
                bottomEnd = 0.0.dp,
                bottomStart = 0.0.dp
            ),
            hide = hide,
            sheetContent = {
                if (status is IntentResolveResult.Pending) {
                    LoadingIndicator(events = resolver.events, interactions = resolver.interactions, requestExpand = {
                        coroutineScope.launch { drawerState.expand() }
                    })
                } else if (status is IntentResolveResult.Default) {
                    AppWrapper_Temp(
                        status = status as IntentResolveResult.Default,
                        isExpanded = drawerState.currentValue == SheetValue.Expanded,
                        hideDrawer = hide
                    )
                }
//                val scope: ColumnScope = this@BottomDrawer
//                defaultVerticalPadding
//                Column(modifier = Modifier.weight(1.0f, fill = false)) {
//                SheetContent(
//                    result = result,
////                    isExpanded = drawerState.currentValue == FlexibleSheetValue.SlightlyExpanded,
//                    isExpanded = drawerState.currentValue == SheetValue.Expanded,
//                    hideDrawer = hide,
//                    requestExpand = {
//                        coroutineScope.launch { drawerState.expand() }
//                    }
//                )
            }
        )
    }

    @Composable
    private fun AppWrapper_Temp(status: IntentResolveResult.Default, isExpanded: Boolean, hideDrawer: () -> Unit) {
        BottomSheetApps(
            bottomSheetViewModel = viewModel,
            result = status,
            declutterUrl = viewModel.declutterUrl(),
            enableIgnoreLibRedirectButton = viewModel.enableIgnoreLibRedirectButton(),
            enableSwitchProfile = viewModel.switchProfile(),
            isExpanded = isExpanded,
            requestExpand = {},
            hideDrawer = hideDrawer,
            showPackage = viewModel.alwaysShowPackageName(),
            previewUrl = viewModel.previewUrl(),
            hideBottomSheetChoiceButtons = viewModel.hideBottomSheetChoiceButtons(),
            urlCardDoubleTap = viewModel.improvedBottomSheetUrlDoubleTap()
        )
    }

    @Composable
    private fun BottomSheetApps(
        // TODO: Refactor this away
        bottomSheetViewModel: BottomSheetViewModel,
        result: IntentResolveResult.Default,
        declutterUrl: Boolean,
        enableIgnoreLibRedirectButton: Boolean,
        enableSwitchProfile: Boolean,
        isExpanded: Boolean,
        requestExpand: () -> Unit,
        hideDrawer: () -> Unit,
        showPackage: Boolean,
        previewUrl: Boolean,
        hideBottomSheetChoiceButtons: Boolean,
        urlCardDoubleTap: Boolean,
    ) {
        if (previewUrl && result.uri != null) {
            val uriString = if (declutterUrl) {
                UriUtil.declutter(result.uri)
            } else result.uri.toString()

            val (crossProfileApps, canSwitch, target) = if (enableSwitchProfile && AndroidVersion.AT_LEAST_API_30_R) {
                val crossProfileApps = getSystemService<CrossProfileApps>()!!
                val canSwitch =
                    crossProfileApps.canInteractAcrossProfiles() && crossProfileApps.targetUserProfiles.isNotEmpty()
                val target = crossProfileApps.targetUserProfiles.firstOrNull()

                Triple(crossProfileApps, canSwitch, target)
            } else Triple(null, false, null)

            UrlBar(
                uri = uriString,
                canSwitchProfile = canSwitch,
                profileSwitchText = if (canSwitch && AndroidVersion.AT_LEAST_API_30_R) crossProfileApps!!.getProfileSwitchingLabel(
                    target!!
                ).toString() else null,
                profileSwitchDrawable = if (canSwitch && AndroidVersion.AT_LEAST_API_30_R) crossProfileApps!!.getProfileSwitchingIconDrawable(
                    target!!
                ) else null,
                switchProfile = {
                    if (AndroidVersion.AT_LEAST_API_30_R) {
                        val switchIntent = Intent(
                            Intent.ACTION_VIEW,
                            result.uri
                        ).setComponent(activity.componentName)
                        crossProfileApps!!.startActivity(
                            switchIntent,
                            target!!,
                            activity
                        )

                        finish()
                    }
                },
                unfurlResult = result.unfurlResult,
                downloadable = result.downloadable.isDownloadable(),
                libRedirected = enableIgnoreLibRedirectButton && result.libRedirectResult is LibRedirectResult.Redirected,
                copyUri = {
                    viewModel.clipboardManager.setText("URL", result.uri.toString())

                    if (bottomSheetViewModel.urlCopiedToast()) {
                        showToast(R.string.url_copied)
                    }

                    if (bottomSheetViewModel.hideAfterCopying()) {
                        hideDrawer()
                    }
                },
                shareUri = {
                    startActivity(shareUri(result.uri))
                    finish()
                },
                downloadUri = {
                    bottomSheetViewModel.startDownload(
                        activity.resources, result.uri,
                        result.downloadable as DownloadCheckResult.Downloadable
                    )

                    if (bottomSheetViewModel.downloadStartedToast()) {
                        showToast(R.string.download_started)
                    }

                    if (bottomSheetViewModel.hideAfterCopying()) {
                        hideDrawer()
                    }
                },
                ignoreLibRedirect = {
                    val redirected =
                        result.libRedirectResult as LibRedirectResult.Redirected

                    finish()
                    startActivity(
                        selfIntent(
                            redirected.originalUri,
                            bundleOf(LibRedirectDefault.libRedirectIgnore to true)
                        )
                    )
                },
                onDoubleClick = { launchApp(result, result.app, false) }.takeIf { urlCardDoubleTap }
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
                    launchApp(result, result.filteredItem, modifier == ClickModifier.Always)
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
            if (bottomSheetViewModel.gridLayout()) {
                Grid(
                    result = result,
                    hasPreferredApp = result.filteredItem != null,
                    hideChoiceButtons = bottomSheetViewModel.hideBottomSheetChoiceButtons(),
                    isExpanded = isExpanded,
                    requestExpand = requestExpand,
                    showPackage = showPackage
                )
            } else {
                List(
                    result = result,
                    hasPreferredApp = result.filteredItem != null,
                    hideChoiceButtons = bottomSheetViewModel.hideBottomSheetChoiceButtons(),
                    showNativeLabel = bottomSheetViewModel.bottomSheetNativeLabel(),
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
                            val job = viewModel.handleClick(
                                activity, index, isExpanded,
                                requestExpand,
                                result.intent, info, type, modifier
                            )
                            if (job != null) {
                                handleLaunch(job)
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

//                    info.

                    ListBrowserColumn(
                        appInfo = info,
                        selected = if (!hasPreferredApp) index == viewModel.appListSelectedIdx.intValue else null,
                        onClick = { type, modifier ->
                            val job = viewModel.handleClick(
                                activity,
                                index,
                                isExpanded,
                                requestExpand,
                                result.intent,
                                info,
                                type,
                                modifier
                            )
                            if (job != null) {
                                handleLaunch(job)
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
//        AlertDialog(onDismissRequest = { /*TODO*/ }, confirmButton = { /*TODO*/ })

            if (!hasPreferredApp && !hideChoiceButtons) {
//                Box(
//                    modifier = Modifier
//                        .align(Alignment.BottomCenter)
//                        .fillMaxWidth()
//                        .navigationBarsPadding()
//                ) {

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
                launchApp(result, result.resolved.getOrFirstOrNull(selected), modifier == ClickModifier.Always)
            },
        )
    }

    private fun isPrivateBrowser(hasUri: Boolean, info: DisplayActivityInfo): KnownBrowser? {
        if (!viewModel.enableRequestPrivateBrowsingButton() || !hasUri) return null
        return KnownBrowser.isKnownBrowser(info.packageName, privateOnly = true)
    }

    private fun resolveAsync(viewModel: BottomSheetViewModel): Deferred<Unit> {
        return activity.lifecycleScope.async {
            val completed = viewModel.resolveAsync(intent, referrer).await()

            if (completed is BottomSheetResult.BottomSheetSuccessResult && completed.hasAutoLaunchApp) {
                showResolveToasts(completed, uiThread = true)

                if (viewModel.openingWithAppToast()) {
                    showToast(getString(R.string.opening_with_app, completed.app.label), uiThread = true)
                }

                launchApp(
                    completed,
                    completed.app,
                    always = completed.isRegularPreferredApp,
                    persist = false,
                )
            }
        }
    }

    private fun getString(resId: Int, vararg formatArgs: String): String {
        return activity.getString(resId, *formatArgs)
    }

    fun launchApp(
        result: BottomSheetResult.SuccessResult,
        info: DisplayActivityInfo?,
        always: Boolean = false,
        privateBrowsingBrowser: KnownBrowser? = null,
        persist: Boolean = true,
    ) {
        if (info == null) {
            showToast(R.string.something_went_wrong, uiThread = true)
            return
        }

        handleLaunch(viewModel.launchAppAsync(info, result.intent, always, privateBrowsingBrowser, persist))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun handleLaunch(deferred: Deferred<Intent>) {
        deferred.invokeOnCompletion {
            val showAsReferrer = viewModel.showAsReferrer()
            val intent = deferred.getCompleted()

            intent.putExtra(
                LinkSheetConnector.EXTRA_REFERRER,
                if (showAsReferrer) Uri.parse("android-app://${activity.packageName}") else referrer,
            )

            if (!showAsReferrer) {
                intent.putExtra(Intent.EXTRA_REFERRER, referrer)
            }

            if (viewModel.safeStartActivity(activity, intent)) {
                finish()
            } else {
                showToast(R.string.resolve_activity_failure, uiThread = true)
            }
        }
    }

    private fun showResolveToasts(result: BottomSheetResult.BottomSheetSuccessResult, uiThread: Boolean = false) {
        viewModel.getResolveToastTexts(result.resolveModuleStatus).forEach {
            showToast(it, uiThread = uiThread)
        }
    }

    override fun onStop() {
        finish()
    }
}
