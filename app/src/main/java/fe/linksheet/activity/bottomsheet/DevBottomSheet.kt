package fe.linksheet.activity.bottomsheet

import android.content.res.Configuration
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import fe.linksheet.R
import fe.linksheet.activity.AppListModifier
import fe.linksheet.activity.BottomSheetActivity
import fe.linksheet.activity.bottomsheet.dev.UrlBar
import fe.linksheet.activity.bottomsheet.dev.failure.FailureSheetColumn
import fe.linksheet.activity.bottomsheet.dev.grid.GridBrowserButton
import fe.linksheet.activity.bottomsheet.dev.list.ListBrowserColumn
import fe.linksheet.activity.bottomsheet.dev.preferred.PreferredAppColumn
import fe.linksheet.composable.util.BottomDrawer
import fe.linksheet.composable.util.defaultRoundedCornerShape
import fe.linksheet.extension.android.setText
import fe.linksheet.extension.android.shareUri
import fe.linksheet.extension.compose.nullClickable
import fe.linksheet.extension.compose.runIf
import fe.linksheet.module.database.entity.LibRedirectDefault
import fe.linksheet.module.downloader.Downloader
import fe.linksheet.module.resolver.LibRedirectResolver
import fe.linksheet.module.viewmodel.BottomSheetViewModel
import fe.linksheet.resolver.BottomSheetResult
import fe.linksheet.resolver.DisplayActivityInfo
import fe.linksheet.ui.AppTheme
import fe.linksheet.ui.HkGroteskFontFamily
import fe.linksheet.ui.Theme
import fe.linksheet.util.PrivateBrowsingBrowser
import fe.linksheet.util.selfIntent
import kotlinx.coroutines.launch
import kotlin.math.ceil

class DevBottomSheet(
    activity: BottomSheetActivity,
    viewModel: BottomSheetViewModel
) : BottomSheet(activity, viewModel, initPadding = true) {

    @Composable
    override fun ShowSheet(bottomSheetViewModel: BottomSheetViewModel) {
        AppTheme {
            BottomSheet(bottomSheetViewModel)
        }
    }

    companion object {
        val utilButtonWidth = 80.dp
        val buttonPadding = 15.dp

        val buttonRowHeight = 50.dp

        val appListItemPadding = 10.dp
        val appListItemHeight = 40.dp
        val preferredAppItemHeight = 60.dp

        val gridSize = 120.dp
        val gridItemHeightPackageText = 30.dp
        val gridItemHeightPrivateButton = 40.dp

        //            + 50.dp
        var gridItemHeight = 60.dp
//          + 50.dp


        // token from androidx.compose.material.ModelBottomSheet
        val maxModalBottomSheetWidth = 640.dp
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun BottomSheet(bottomSheetViewModel: BottomSheetViewModel) {
        val isBlackTheme = bottomSheetViewModel.theme() == Theme.AmoledBlack
        val configuration = LocalConfiguration.current
        val landscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val result = bottomSheetViewModel.resolveResult

        val coroutineScope = rememberCoroutineScope()

        val drawerState = rememberModalBottomSheetState()
        LaunchedEffect(Unit) {
            drawerState.expand()
        }

//        LaunchedEffect(drawerState.currentValue) {
//            if (drawerState.currentValue == SheetValue.Hidden) {
//                finish()
//            }
//        }

        val hide: () -> Unit = {
            coroutineScope.launch { drawerState.hide() }.invokeOnCompletion { finish() }
        }

        val interactionSource = remember { MutableInteractionSource() }
        BottomDrawer(modifier = Modifier
            .runIf(landscape) {
                it
                    .fillMaxWidth(0.55f)
                    .fillMaxHeight()
            }
            .nullClickable(),
            isBlackTheme = isBlackTheme,
            drawerState = drawerState,
            shape = RoundedCornerShape(
                topStart = 16.0.dp,
                topEnd = 16.0.dp,
                bottomEnd = 0.0.dp,
                bottomStart = 0.0.dp
            ),
            hide = hide,
            sheetContent = {
                SheetContent(result = result, landscape = landscape, hideDrawer = hide)
            }
        )
    }

    @Composable
    private fun SheetContent(result: BottomSheetResult?, landscape: Boolean, hideDrawer: () -> Unit) {
        if (result != null && result is BottomSheetResult.BottomSheetSuccessResult && !result.hasAutoLaunchApp) {
            val showPackage = remember {
                result.showExtended || viewModel.alwaysShowPackageName()
            }

            val maxHeight = (if (landscape) LocalConfiguration.current.screenWidthDp
            else LocalConfiguration.current.screenHeightDp) / 3f

            val itemHeight = if (viewModel.gridLayout.value) {
                val gridItemHeight = gridItemHeight.value + if (showPackage) 10f else 0.0f

                gridItemHeight
            } else appListItemHeight.value

            val baseHeight = ((ceil((maxHeight / itemHeight).toDouble()) - 1) * itemHeight).dp

            BottomSheetApps(
                bottomSheetViewModel = viewModel,
                hideDrawer = hideDrawer,
                showPackage = showPackage,
                previewUrl = viewModel.previewUrl(),
                hasPreferredApp = result.filteredItem != null
            )
//            if (result.filteredItem == null) {
//                OpenWith(
//                    bottomSheetViewModel = viewModel,
//                    hideDrawer = hideDrawer,
//                    showPackage = showPackage,
//                    previewUrl = viewModel.previewUrl()
//                )
//            } else {
//                OpenWithPreferred(
//                    bottomSheetViewModel = viewModel,
//                    hideDrawer = hideDrawer,
//                    showPackage = showPackage,
//                    previewUrl = viewModel.previewUrl()
//                )
//            }
        } else {
            FailureSheetColumn(
                result = result,
                useTextShareCopyButtons = viewModel.useTextShareCopyButtons(),
                onShareClick = {
                    startActivity(shareUri(result?.uri))
                    finish()
                },
                onCopyClick = {
                    viewModel.clipboardManager.setText(
                        "URL", result?.uri.toString()
                    )

                    if (!viewModel.urlCopiedToast()) {
                        showToast(R.string.url_copied)
                    }

                    if (viewModel.hideAfterCopying()) {
                        hideDrawer()
                    }
                }
            )
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun BottomSheetApps(
        bottomSheetViewModel: BottomSheetViewModel,
        hideDrawer: () -> Unit,
        showPackage: Boolean,
        previewUrl: Boolean,
        hasPreferredApp: Boolean
    ) {
        val result = bottomSheetViewModel.resolveResult!!
        if (result !is BottomSheetResult.BottomSheetSuccessResult) return

        if (result.isEmpty) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(text = stringResource(id = R.string.no_app_to_handle_link_found))
            }

            Spacer(modifier = Modifier.height(10.dp))
            return
        }

        var selectedItem by remember { mutableIntStateOf(-1) }
        val modifier: AppListModifier = @Composable { index, info ->
            Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp)
                .clip(defaultRoundedCornerShape)
                .combinedClickable(onClick = {
//                    bottomSheetViewModel.privateBrowser.value = shouldShowRequestPrivate(info)
//                    bottomSheetViewModel.appInfo.value = info
                    if (hasPreferredApp) {
                        launchApp(result, info)
                    } else {
                        if (bottomSheetViewModel.singleTap.value) {
                            launchApp(result, info)
                        } else {
                            if (selectedItem == index) launchApp(result, info)
                            else selectedItem = index
                        }
                    }
                }, onDoubleClick = {
                    if (!bottomSheetViewModel.singleTap.value) {
                        launchApp(result, info)
                    } else {
                        startPackageInfoActivity(info)
                    }
                }, onLongClick = {
                    if (bottomSheetViewModel.singleTap.value) {
                        selectedItem = index
                    } else {
                        startPackageInfoActivity(info)
                    }
                })
                .background(
                    if (selectedItem == index) LocalContentColor.current.copy(0.1f)
                    else Color.Transparent
                )
                .padding(appListItemPadding)
        }

        val ignoreLibRedirectClick: (LibRedirectResolver.LibRedirectResult.Redirected) -> Unit = {
            finish()
            startActivity(selfIntent(it.originalUri, bundleOf(LibRedirectDefault.libRedirectIgnore to true)))
        }

        // TODO: Add "Once" and "Always" to non-pref UI, re-add grid, long/double tap options/single tap, limit height?
        //      "Loading Link" show progress / downloader fails

        if (previewUrl && result.uri != null) {
            UrlBar(
                uri = result.uri,
                downloadable = result.downloadable.isDownloadable(),
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
                }
            )
        }

        if (hasPreferredApp) {
            val privateBrowser = isPrivateBrowser(result.filteredItem!!)
            val ignoreLibRedirect = if (viewModel.enableIgnoreLibRedirectButton()) {
                result.libRedirectResult as? LibRedirectResolver.LibRedirectResult.Redirected
            } else null

            PreferredAppColumn(
                appInfo = result.filteredItem,
                privateBrowser = privateBrowser,
                preferred = true,
                bottomSheetViewModel = bottomSheetViewModel,
                showPackage = showPackage,
                launchApp = { item, always, private ->
                    launchApp(result, item, always, if (private) privateBrowser else null)
                },
                libRedirectResult = ignoreLibRedirect,
                ignoreLibRedirectClick = ignoreLibRedirectClick
            )
        } else {
            Text(
                text = stringResource(id = R.string.open_with),
                fontFamily = HkGroteskFontFamily,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 15.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))
        }

        if (bottomSheetViewModel.gridLayout()) {
            Grid(result = result, showPackage = showPackage, ignoreLibRedirectClick = ignoreLibRedirectClick)
        } else {
            List(result = result, showPackage = showPackage, ignoreLibRedirectClick = ignoreLibRedirectClick)
        }
    }

    @Composable
    private fun Grid(
        result: BottomSheetResult.BottomSheetSuccessResult,
        showPackage: Boolean,
        ignoreLibRedirectClick: (LibRedirectResolver.LibRedirectResult.Redirected) -> Unit
    ) {
        LazyVerticalGrid(modifier = Modifier.fillMaxWidth(), columns = GridCells.Adaptive(85.dp)) {
            itemsIndexed(items = result.resolved, key = { _, item -> item.flatComponentName }) { _, info ->
                val privateBrowser = isPrivateBrowser(info)
                val ignoreLibRedirect = if (viewModel.enableIgnoreLibRedirectButton()) {
                    result.libRedirectResult as? LibRedirectResolver.LibRedirectResult.Redirected
                } else null

                GridBrowserButton(
                    appInfo = info,
                    privateBrowser = privateBrowser,
                    showPackage = showPackage,
                    launchApp = { item, always, private ->
                        launchApp(result, item, always, if (private) privateBrowser else null)
                    },
                    libRedirectResult = ignoreLibRedirect,
                    ignoreLibRedirectClick = ignoreLibRedirectClick
                )

//                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    Image(
//                        bitmap = info.iconBitmap,
//                        contentDescription = info.label,
//                        modifier = Modifier.size(32.dp)
//                    )
//
//                    Text(
//                        text = info.label,
//                        maxLines = 1,
//                        overflow = TextOverflow.Ellipsis,
//                        fontSize = 14.sp,
//                        modifier = Modifier.padding(top = 3.dp)
//                    )
//
//                    if (showPackage) {
//                        Text(
//                            text = info.packageName,
//                            fontSize = 12.sp,
//                            overflow = TextOverflow.Ellipsis,
//                            maxLines = 1
//                        )
//                    }
//                }
            }
        }
    }

    @Composable
    private fun List(
        result: BottomSheetResult.BottomSheetSuccessResult,
        showPackage: Boolean,
        ignoreLibRedirectClick: (LibRedirectResolver.LibRedirectResult.Redirected) -> Unit
    ) {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            itemsIndexed(items = result.resolved, key = { _, item -> item.flatComponentName }) { _, info ->
                val privateBrowser = isPrivateBrowser(info)
                val ignoreLibRedirect = if (viewModel.enableIgnoreLibRedirectButton()) {
                    result.libRedirectResult as? LibRedirectResolver.LibRedirectResult.Redirected
                } else null

                ListBrowserColumn(
                    appInfo = info,
                    preferred = false,
                    privateBrowser = privateBrowser,
                    showPackage = showPackage,
                    launchApp = { item, always, private ->
                        launchApp(result, item, always, if (private) privateBrowser else null)
                    },
                    libRedirectResult = ignoreLibRedirect,
                    ignoreLibRedirectClick = ignoreLibRedirectClick
                )

                // TODO: Selector?

//                Box(
//                                modifier = Modifier.fillMaxWidth(),
//                                contentAlignment = Alignment.CenterEnd
//                            ) {
//                                if (selectedItem == index && !hasPreferredApp) {
//                                    Icon(
//                                        imageVector = Icons.Default.CheckCircle,
//                                        contentDescription = null,
//                                        modifier = Modifier.align(Alignment.CenterEnd)
//                                    )
//                                }
//                            }


//            if (!hasPreferredApp) {
//                item {
//                    Spacer(modifier = Modifier.height(10.dp))
//
//                    ButtonColumn(
//                        bottomSheetViewModel = bottomSheetViewModel,
//                        enabled = selectedItem != -1,
//                        resources = resources,
//                        onClick = { always -> launchApp(result, result.resolved[selectedItem], always) },
//                        hideDrawer = hideDrawer,
//                        showToast = { showToast(it) },
//                        ignoreLibRedirectClick = ignoreLibRedirectClick
//                    )
//                }
            }
        }
    }

    private fun isPrivateBrowser(info: DisplayActivityInfo): PrivateBrowsingBrowser? {
        if (!viewModel.enableRequestPrivateBrowsingButton()) return null
        return PrivateBrowsingBrowser.getSupportedBrowser(info.packageName)
    }
}
