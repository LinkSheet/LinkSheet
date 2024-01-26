package fe.linksheet.activity.bottomsheet

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.os.bundleOf
import fe.linksheet.R
import fe.linksheet.activity.AppListModifier
import fe.linksheet.activity.BottomSheetActivity
import fe.linksheet.activity.bottomsheet.dev.UrlBar
import fe.linksheet.activity.bottomsheet.dev.failure.FailureSheetColumn
import fe.linksheet.activity.bottomsheet.dev.list.BrowserColumn
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
) : BottomSheet(activity, viewModel) {

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

        val drawerState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        LaunchedEffect(key1 = Unit) {
            drawerState.expand()
        }
        LaunchedEffect(drawerState.currentValue) {
            if (drawerState.currentValue == SheetValue.Hidden) {
                finish()
            }
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
            shape = ShapeDefaults.Large,
            sheetContent = {
                SheetContent(result = result, landscape = landscape, hideDrawer = {
                    coroutineScope.launch { drawerState.hide() }
                })
            })
    }

    @Composable
    private fun SheetContent(result: BottomSheetResult?, landscape: Boolean, hideDrawer: () -> Unit) {
        if (result != null && result is BottomSheetResult.BottomSheetSuccessResult && !result.hasAutoLaunchApp) {
            val showPackage = remember {
                result.showExtended || viewModel.alwaysShowPackageName.value
            }

            val maxHeight = (if (landscape) LocalConfiguration.current.screenWidthDp
            else LocalConfiguration.current.screenHeightDp) / 3f

            val itemHeight = if (viewModel.gridLayout.value) {
                val gridItemHeight = gridItemHeight.value + if (showPackage) 10f else 0.0f

                gridItemHeight
            } else appListItemHeight.value

            val baseHeight = ((ceil((maxHeight / itemHeight).toDouble()) - 1) * itemHeight).dp

            if (result.filteredItem == null) {
                OpenWith(
                    bottomSheetViewModel = viewModel,
                    hideDrawer = hideDrawer,
                    showPackage = showPackage,
                    previewUrl = viewModel.previewUrl.value
                )
            } else {
                OpenWithPreferred(
                    bottomSheetViewModel = viewModel,
                    hideDrawer = hideDrawer,
                    showPackage = showPackage,
                    previewUrl = viewModel.previewUrl.value
                )
            }
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

                    if (!viewModel.urlCopiedToast.value) {
                        showToast(R.string.url_copied)
                    }

                    if (viewModel.hideAfterCopying.value) {
                        hideDrawer()
                    }
                }
            )
        }
    }


    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun OpenWithPreferred(
        bottomSheetViewModel: BottomSheetViewModel,
        hideDrawer: () -> Unit,
        showPackage: Boolean,
        previewUrl: Boolean
    ) {
        if (bottomSheetViewModel.gridLayout.value) {
//            BtmSheetGridUI(
//                bottomSheetViewModel = bottomSheetViewModel,
//                hideDrawer = hideDrawer,
//                showPackage = showPackage,
//                previewUrl = previewUrl,
//                forPreferred = true
//            )
        } else {
            BtmSheetNonGridUI(
                bottomSheetViewModel = bottomSheetViewModel,
                hideDrawer = hideDrawer,
                showPackage = showPackage,
                previewUrl = previewUrl,
                hasPreferredApp = true
            )
        }
    }

    @OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    private fun OpenWith(
        bottomSheetViewModel: BottomSheetViewModel,
        hideDrawer: () -> Unit,
        showPackage: Boolean,
        previewUrl: Boolean,
    ) {
        if (bottomSheetViewModel.gridLayout.value) {
//            BtmSheetGridUI(
//                bottomSheetViewModel = bottomSheetViewModel,
//                hideDrawer = hideDrawer,
//                showPackage = showPackage,
//                previewUrl = previewUrl,
//                forPreferred = false
//            )
        } else {
            BtmSheetNonGridUI(
                bottomSheetViewModel = bottomSheetViewModel,
                hideDrawer = hideDrawer,
                showPackage = showPackage,
                previewUrl = previewUrl,
                hasPreferredApp = false
            )
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun BtmSheetNonGridUI(
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

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            if (previewUrl && result.uri != null) {
                item(key = "previewUrl") {
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
            }

            item {
                if (hasPreferredApp) {
                    val privateBrowser = isPrivateBrowser(result.filteredItem!!)
                    val ignoreLibRedirect = if(viewModel.enableIgnoreLibRedirectButton()){
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
            }

            itemsIndexed(items = result.resolved, key = { _, item -> item.flatComponentName }) { _, info ->
                val privateBrowser = isPrivateBrowser(info)
                val ignoreLibRedirect = if(viewModel.enableIgnoreLibRedirectButton()){
                    result.libRedirectResult as? LibRedirectResolver.LibRedirectResult.Redirected
                } else null

                BrowserColumn(
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