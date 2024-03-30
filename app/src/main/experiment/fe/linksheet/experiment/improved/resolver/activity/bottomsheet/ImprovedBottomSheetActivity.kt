package fe.linksheet.experiment.improved.resolver.activity.bottomsheet

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.CrossProfileApps
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
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
import fe.linksheet.R
import fe.linksheet.activity.bottomsheet.UrlBar
import fe.linksheet.activity.bottomsheet.button.ChoiceButtons
import fe.linksheet.activity.bottomsheet.column.ClickModifier
import fe.linksheet.activity.bottomsheet.column.GridBrowserButton
import fe.linksheet.activity.bottomsheet.column.ListBrowserColumn
import fe.linksheet.activity.bottomsheet.column.PreferredAppColumn
import fe.linksheet.composable.util.BottomDrawer
import fe.linksheet.experiment.improved.resolver.ImprovedIntentResolver
import fe.linksheet.experiment.improved.resolver.IntentResolveResult
import fe.linksheet.experiment.url.bar.ExperimentalUrlBar
import fe.linksheet.extension.android.initPadding
import fe.linksheet.extension.android.setText
import fe.linksheet.extension.android.shareUri
import fe.linksheet.extension.android.showToast
import fe.linksheet.extension.compose.setContentWithKoin
import fe.linksheet.extension.kotlin.collectOnIO
import fe.linksheet.interconnect.LinkSheetConnector
import fe.linksheet.module.database.entity.LibRedirectDefault
import fe.linksheet.module.downloader.Downloader
import fe.linksheet.module.resolver.KnownBrowser
import fe.linksheet.module.resolver.LibRedirectResolver
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
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinComponent


class ImprovedBottomSheetActivity : ComponentActivity(), KoinComponent {
    private val viewModel by viewModel<BottomSheetViewModel>()

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val resolver = ImprovedIntentResolver(application, lifecycleScope)

        initPadding()
        setContentWithKoin {
            AppTheme { Wrapper(resolver) }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun Wrapper(resolver: ImprovedIntentResolver) {
        var status by remember { mutableStateOf<IntentResolveResult>(IntentResolveResult.Pending) }

        // TODO: Use intent and referrer as keys?
        LaunchedEffect(key1 = resolver) {
            // TODO: Internet check
            status = resolver.resolve(intent.toSafeIntent(), referrer)
        }


        LaunchedEffect(key1 = status) {
//            Toast.makeText(this@ImprovedBottomSheetActivity, "Status: $status", Toast.LENGTH_SHORT).show()
            Log.d("BottomSheet", "Status: ${status.javaClass.simpleName}")
        }

        val configuration = LocalConfiguration.current
        val landscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        val coroutineScope = rememberCoroutineScope()
        val drawerState = rememberModalBottomSheetState()

        LaunchedEffect(key1 = Unit) {
            drawerState.expand()
        }

        val hide: () -> Unit = {
            coroutineScope.launch { drawerState.hide() }.invokeOnCompletion { finish() }
        }

        BottomDrawer(
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
                    LoadingIndicator(resolver = resolver)
                } else if (status is IntentResolveResult.Default) {
                    AppWrapper_Temp(
                        status as IntentResolveResult.Default,
                        drawerState.currentValue == SheetValue.Expanded
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
    private fun AppWrapper_Temp(status: IntentResolveResult.Default, isExpanded: Boolean) {
        BottomSheetApps(
            bottomSheetViewModel = viewModel,
            result = status,
            declutterUrl = viewModel.declutterUrl(),
            experimentalUrlBar = viewModel.experimentalUrlBar(),
            enableSwitchProfile = viewModel.switchProfile(),
            isExpanded = isExpanded,
            requestExpand = {},
            hideDrawer = {

            },
            showPackage = false,
            previewUrl = viewModel.previewUrl(),
            hideBottomSheetChoiceButtons = viewModel.hideBottomSheetChoiceButtons()
        )
    }

    @Composable
    private fun BottomSheetApps(
        // TODO: Refactor this away
        bottomSheetViewModel: BottomSheetViewModel,
        result: IntentResolveResult.Default,
        experimentalUrlBar: Boolean,
        declutterUrl: Boolean,
        enableSwitchProfile: Boolean,
        isExpanded: Boolean,
        requestExpand: () -> Unit,
        hideDrawer: () -> Unit,
        showPackage: Boolean,
        previewUrl: Boolean,
        hideBottomSheetChoiceButtons: Boolean,
    ) {
        if (previewUrl && result.uri != null) {
            val uriSuccess = result as? BottomSheetResult.BottomSheetSuccessResult
            if (experimentalUrlBar) {
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
                            ).setComponent(this@ImprovedBottomSheetActivity.componentName)
                            crossProfileApps!!.startActivity(
                                switchIntent,
                                target!!,
                                this@ImprovedBottomSheetActivity
                            )

                            finish()
                        }
                    },
                    unfurlResult = uriSuccess?.unfurlResult,
                    downloadable = uriSuccess?.downloadable?.isDownloadable() ?: false,
                    libRedirected = uriSuccess?.libRedirectResult is LibRedirectResolver.LibRedirectResult.Redirected,
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
                            resources, result.uri,
                            result.downloadable as Downloader.DownloadCheckResult.Downloadable
                        )

                        if (!bottomSheetViewModel.downloadStartedToast()) {
                            showToast(R.string.download_started)
                        }

                        hideDrawer()
                    },
                    ignoreLibRedirect = {
                        val redirected =
                            result.libRedirectResult as LibRedirectResolver.LibRedirectResult.Redirected

                        finish()
                        startActivity(
                            selfIntent(
                                redirected.originalUri,
                                bundleOf(LibRedirectDefault.libRedirectIgnore to true)
                            )
                        )
                    }
                )
            } else {
                UrlBar(
                    uri = result.uri,
                    downloadable = uriSuccess?.downloadable?.isDownloadable() ?: false,
                    libRedirected = uriSuccess?.libRedirectResult is LibRedirectResolver.LibRedirectResult.Redirected,
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
                            resources, result.uri,
                            result.downloadable as Downloader.DownloadCheckResult.Downloadable
                        )

                        if (!bottomSheetViewModel.downloadStartedToast()) {
                            showToast(R.string.download_started)
                        }

                        hideDrawer()
                    },
                    ignoreLibRedirect = {
                        val redirected =
                            result.libRedirectResult as LibRedirectResolver.LibRedirectResult.Redirected

                        finish()
                        startActivity(
                            selfIntent(
                                redirected.originalUri,
                                bundleOf(LibRedirectDefault.libRedirectIgnore to true)
                            )
                        )
                    }
                )
            }
        }

        if (result.filteredItem != null) {
            val privateBrowser = isPrivateBrowser(result.uri != null, result.filteredItem)

            PreferredAppColumn(
                appInfo = result.filteredItem,
                privateBrowser = privateBrowser,
                preferred = true,
                bottomSheetViewModel = bottomSheetViewModel,
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
        val activity = LocalActivity.current

        Spacer(modifier = Modifier.height(5.dp))

        ChoiceButtons(
            result = result,
            enabled = selected != -1,
            openSettings = { viewModel.startMainActivity(activity) },
            choiceClick = { _, modifier ->
                launchApp(result, result.resolved[selected], modifier == ClickModifier.Always)
            },
        )
    }

    private fun isPrivateBrowser(hasUri: Boolean, info: DisplayActivityInfo): KnownBrowser? {
        if (!viewModel.enableRequestPrivateBrowsingButton() || !hasUri) return null
        return KnownBrowser.isKnownBrowser(info.packageName, privateOnly = true)
    }

    private fun resolveAsync(viewModel: BottomSheetViewModel): Deferred<Unit> {
        return lifecycleScope.async {
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

    fun launchApp(
        result: BottomSheetResult.SuccessResult,
        info: DisplayActivityInfo,
        always: Boolean = false,
        privateBrowsingBrowser: KnownBrowser? = null,
        persist: Boolean = true,
    ) {
        handleLaunch(viewModel.launchAppAsync(info, result.intent, always, privateBrowsingBrowser, persist))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun handleLaunch(deferred: Deferred<Intent>) {
        deferred.invokeOnCompletion {
            val showAsReferrer = viewModel.showAsReferrer()
            val intent = deferred.getCompleted()

            intent.putExtra(
                LinkSheetConnector.EXTRA_REFERRER,
                if (showAsReferrer) Uri.parse("android-app://${packageName}") else referrer,
            )

            if (!showAsReferrer) {
                intent.putExtra(Intent.EXTRA_REFERRER, referrer)
            }

            startActivity(intent)
            finish()
        }
    }

    private fun showResolveToasts(result: BottomSheetResult.BottomSheetSuccessResult, uiThread: Boolean = false) {
        viewModel.getResolveToastTexts(result.resolveModuleStatus).forEach {
            showToast(it, uiThread = uiThread)
        }
    }

    override fun onStop() {
        super.onStop()
        finish()
    }
}
