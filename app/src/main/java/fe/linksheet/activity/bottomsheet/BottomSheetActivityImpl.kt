package fe.linksheet.activity.bottomsheet

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.CrossProfileApps
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import fe.linksheet.activity.bottomsheet.button.ChoiceButtons
import fe.linksheet.activity.bottomsheet.column.ClickModifier
import fe.linksheet.activity.bottomsheet.column.GridBrowserButton
import fe.linksheet.activity.bottomsheet.column.ListBrowserColumn
import fe.linksheet.activity.bottomsheet.column.PreferredAppColumn
import fe.linksheet.activity.bottomsheet.failure.FailureSheetColumn
import fe.linksheet.composable.util.BottomDrawer
import fe.linksheet.experiment.ui.overhaul.composable.component.bottomsheet.ExperimentalFailureSheetColumn
import fe.linksheet.experiment.url.bar.ExperimentalUrlBar
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


class BottomSheetActivityImpl(
    val activity: BottomSheetActivity,
    val viewModel: BottomSheetViewModel,
) : BottomSheetImpl() {

    companion object {
        val preferredAppItemHeight = 60.dp
        val buttonPadding = 15.dp
        val buttonRowHeight = 50.dp
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val deferred = resolveAsync(viewModel)
        if (viewModel.showLoadingBottomSheet()) {
            activity.setContent(true) {
                LaunchedEffect(viewModel.resolveResult) {
                    (viewModel.resolveResult as? BottomSheetResult.BottomSheetSuccessResult)?.let {
                        showResolveToasts(it)
                    }
                }

                AppTheme {
                    BottomSheet(viewModel)
                }
            }
        } else {
            deferred.invokeOnCompletion {
                activity.setContent(true) {
                    AppTheme {
                        BottomSheet(viewModel)
                    }
                }
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun BottomSheet(bottomSheetViewModel: BottomSheetViewModel) {
        val isBlackTheme = bottomSheetViewModel.themeAmoled()
        val configuration = LocalConfiguration.current
        val landscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val result = bottomSheetViewModel.resolveResult

        val coroutineScope = rememberCoroutineScope()

        val drawerState = rememberModalBottomSheetState()
//        val drawerState = rememberFlexibleBottomSheetState(
//            flexibleSheetSize = FlexibleSheetSize(
//                fullyExpanded = 0.9f,
//                intermediatelyExpanded = 0.5f,
//                slightlyExpanded = 0.15f,
//            ),
//            isModal = true,
//            skipSlightlyExpanded = false,
//        )

        val hide: () -> Unit = {
            coroutineScope.launch { drawerState.hide() }.invokeOnCompletion { activity.finish() }
        }

        BottomDrawer(
            landscape = landscape,
            isBlackTheme = isBlackTheme,
            drawerState = drawerState,
            shape = RoundedCornerShape(
                topStart = 22.0.dp,
                topEnd = 22.0.dp,
                bottomEnd = 0.0.dp,
                bottomStart = 0.0.dp
            ),
            hide = hide,
            sheetContent = {
//                val scope: ColumnScope = this@BottomDrawer
//                defaultVerticalPadding
//                Column(modifier = Modifier.weight(1.0f, fill = false)) {
                SheetContent(
                    result = result,
//                    isExpanded = drawerState.currentValue == FlexibleSheetValue.SlightlyExpanded,
                    isExpanded = drawerState.currentValue == SheetValue.Expanded,
                    hideDrawer = hide,
                    requestExpand = {
                        coroutineScope.launch { drawerState.expand() }
                    }
                )


//                }
            }
        )

        LaunchedEffect(Unit) {
            drawerState.show()
        }
    }

    @Composable
    private fun SheetContent(
        result: BottomSheetResult?,
        isExpanded: Boolean,
        hideDrawer: () -> Unit,
        requestExpand: () -> Unit,
    ) {
        val uriSuccessResult = result as? BottomSheetResult.BottomSheetSuccessResult
        val canShowApps = uriSuccessResult != null && !result.hasAutoLaunchApp
                || result is BottomSheetResult.BottomSheetWebSearchResult

//        LoadingIndicator()

        if (canShowApps) {
            val showPackage = remember {
                uriSuccessResult?.showExtended == true || viewModel.alwaysShowPackageName()
            }

            BottomSheetApps(
                bottomSheetViewModel = viewModel,
                result = result as BottomSheetResult.SuccessResult,
                declutterUrl = viewModel.declutterUrl(),
                experimentalUrlBar = viewModel.experimentalUrlBar(),
                enableSwitchProfile = viewModel.switchProfile(),
                isExpanded = isExpanded,
                requestExpand = requestExpand,
                hideDrawer = hideDrawer,
                showPackage = showPackage,
                previewUrl = viewModel.previewUrl(),
                hasPreferredApp = uriSuccessResult?.filteredItem != null,
                hideBottomSheetChoiceButtons = viewModel.hideBottomSheetChoiceButtons()
            )
        } else if (result !is BottomSheetResult.BottomSheetNoHandlersFound) {
            LoadingIndicator()
        } else {
            FailureSheetColumn(
                onShareClick = {
                    activity.startActivity(shareUri(result.uri))
                    activity.finish()
                },
                onCopyClick = {
                    viewModel.clipboardManager.setText(
                        "URL", result.uri.toString()
                    )

                    if (!viewModel.urlCopiedToast()) {
                        activity.showToast(R.string.url_copied)
                    }

                    if (viewModel.hideAfterCopying()) {
                        hideDrawer()
                    }
                }
            )

            if (false) {
                ExperimentalFailureSheetColumn(onShareClick = {}, onCopyClick = {})
            }
        }
    }

    @Composable
    private fun BottomSheetApps(
        // TODO: Refactor this away
        bottomSheetViewModel: BottomSheetViewModel,
        result: BottomSheetResult.SuccessResult,
        experimentalUrlBar: Boolean,
        declutterUrl: Boolean,
        enableSwitchProfile: Boolean,
        isExpanded: Boolean,
        requestExpand: () -> Unit,
        hideDrawer: () -> Unit,
        showPackage: Boolean,
        previewUrl: Boolean,
        hasPreferredApp: Boolean,
        hideBottomSheetChoiceButtons: Boolean,
    ) {
        if (previewUrl && result.uri != null) {
            val uriSuccess = result as? BottomSheetResult.BottomSheetSuccessResult
            if (experimentalUrlBar) {
                val uriString = if (declutterUrl) {
                    UriUtil.declutter(result.uri)
                } else result.uri.toString()

                val (crossProfileApps, canSwitch, target) = if (enableSwitchProfile && AndroidVersion.AT_LEAST_API_30_R) {
                    val crossProfileApps = activity.getSystemService<CrossProfileApps>()!!
                    val canSwitch =
                        crossProfileApps.canInteractAcrossProfiles() && crossProfileApps.targetUserProfiles.isNotEmpty()
                    val target = crossProfileApps.targetUserProfiles.firstOrNull()

                    Triple(crossProfileApps, canSwitch, target)
                } else Triple(null, false, null)

                ExperimentalUrlBar(
                    uri = uriString,
                    canSwitchProfile = canSwitch,
                    profileSwitchText = if (canSwitch && AndroidVersion.AT_LEAST_API_30_R) crossProfileApps!!.getProfileSwitchingLabel(
                        target!!
                    )
                        .toString() else null,
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

                            activity.finish()
                        }
                    },
                    unfurlResult = uriSuccess?.unfurlResult,
                    downloadable = uriSuccess?.downloadable?.isDownloadable() ?: false,
                    libRedirected = uriSuccess?.libRedirectResult is LibRedirectResult.Redirected,
                    copyUri = {
                        viewModel.clipboardManager.setText("URL", result.uri.toString())

                        if (bottomSheetViewModel.urlCopiedToast()) {
                            activity.showToast(R.string.url_copied)
                        }

                        if (bottomSheetViewModel.hideAfterCopying()) {
                            hideDrawer()
                        }
                    },
                    shareUri = {
                        activity.startActivity(shareUri(result.uri))
                        activity.finish()
                    },
                    downloadUri = if (result is BottomSheetResult.BottomSheetSuccessResult) {
                        {
                            bottomSheetViewModel.startDownload(
                                activity.resources, result.uri,
                                result.downloadable as DownloadCheckResult.Downloadable
                            )

                            if (!bottomSheetViewModel.downloadStartedToast()) {
                                activity.showToast(R.string.download_started)
                            }

                            hideDrawer()
                        }
                    } else null,
                    ignoreLibRedirect = if (result is BottomSheetResult.BottomSheetSuccessResult) {
                        {
                            val redirected =
                                result.libRedirectResult as LibRedirectResult.Redirected

                            activity.finish()
                            activity.startActivity(
                                selfIntent(
                                    redirected.originalUri,
                                    bundleOf(LibRedirectDefault.libRedirectIgnore to true)
                                )
                            )
                        }
                    } else null
                )
            } else {
                UrlBar(
                    uri = result.uri,
                    downloadable = uriSuccess?.downloadable?.isDownloadable() ?: false,
                    libRedirected = uriSuccess?.libRedirectResult is LibRedirectResult.Redirected,
                    copyUri = {
                        viewModel.clipboardManager.setText("URL", result.uri.toString())

                        if (bottomSheetViewModel.urlCopiedToast()) {
                            activity.showToast(R.string.url_copied)
                        }

                        if (bottomSheetViewModel.hideAfterCopying()) {
                            hideDrawer()
                        }
                    },
                    shareUri = {
                        activity.startActivity(shareUri(result.uri))
                        activity.finish()
                    },
                    downloadUri = if (result is BottomSheetResult.BottomSheetSuccessResult) {
                        {
                            bottomSheetViewModel.startDownload(
                                activity.resources, result.uri,
                                result.downloadable as DownloadCheckResult.Downloadable
                            )

                            if (!bottomSheetViewModel.downloadStartedToast()) {
                                activity.showToast(R.string.download_started)
                            }

                            hideDrawer()
                        }
                    } else null,
                    ignoreLibRedirect = if (result is BottomSheetResult.BottomSheetSuccessResult) {
                        {
                            val redirected =
                                result.libRedirectResult as LibRedirectResult.Redirected

                            activity.finish()
                            activity.startActivity(
                                selfIntent(
                                    redirected.originalUri,
                                    bundleOf(LibRedirectDefault.libRedirectIgnore to true)
                                )
                            )
                        }
                    } else null
                )
            }
        }

        if (hasPreferredApp && result is BottomSheetResult.BottomSheetSuccessResult) {
            val privateBrowser = isPrivateBrowser(result.uri != null, result.filteredItem!!)

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
                    hasPreferredApp = hasPreferredApp,
                    hideChoiceButtons = bottomSheetViewModel.hideBottomSheetChoiceButtons(),
                    isExpanded = isExpanded,
                    requestExpand = requestExpand,
                    showPackage = showPackage
                )
            } else {
                List(
                    result = result,
                    hasPreferredApp = hasPreferredApp,
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
                itemsIndexed(
                    items = items,
                    key = { index, item -> item.toString() + index }) { index, (info, privateBrowser) ->
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
                itemsIndexed(
                    items = result.resolved,
                    key = { index, item -> item.flatComponentName + index }) { index, info ->
                    val privateBrowser = isPrivateBrowser(result.uri != null, info)

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
            val completed = viewModel.resolveAsync(activity.intent, activity.referrer).await()

            if (completed is BottomSheetResult.BottomSheetSuccessResult && completed.hasAutoLaunchApp) {
                showResolveToasts(completed, uiThread = true)

                if (viewModel.openingWithAppToast()) {
                    activity.showToast(
                        activity.getString(R.string.opening_with_app, completed.app.label),
                        uiThread = true
                    )
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

    fun launchApp(
        result: BottomSheetResult.SuccessResult,
        info: DisplayActivityInfo?,
        always: Boolean = false,
        privateBrowsingBrowser: KnownBrowser? = null,
        persist: Boolean = true,
    ) {
        if (info == null) {
            activity.showToast(R.string.something_went_wrong, uiThread = true)
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
                if (showAsReferrer) Uri.parse("android-app://${activity.packageName}") else activity.referrer,
            )

            if (!showAsReferrer) {
                intent.putExtra(Intent.EXTRA_REFERRER, activity.referrer)
            }

            if (viewModel.safeStartActivity(activity, intent)) {
                activity.finish()
            } else {
                activity.showToast(R.string.resolve_activity_failure, uiThread = true)
            }
        }
    }

    private fun showResolveToasts(result: BottomSheetResult.BottomSheetSuccessResult, uiThread: Boolean = false) {
        viewModel.getResolveToastTexts(result.resolveModuleStatus).forEach {
            activity.showToast(it, uiThread = uiThread)
        }
    }

    override fun onStop() {
        activity.finish()
    }
}
