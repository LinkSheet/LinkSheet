package fe.linksheet.activity.bottomsheet

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import fe.linksheet.R
import fe.linksheet.activity.AppListModifier
import fe.linksheet.activity.BottomSheetActivity
import fe.linksheet.composable.util.LegacyBottomDrawer
import fe.linksheet.composable.util.defaultRoundedCornerShape
import fe.linksheet.extension.android.*
import fe.linksheet.extension.compose.currentActivity
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

class LegacyBottomSheet(
    activity: BottomSheetActivity,
    viewModel: BottomSheetViewModel,
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


    @OptIn(androidx.compose.material.ExperimentalMaterialApi::class)
    @Composable
    private fun BottomSheet(
        bottomSheetViewModel: BottomSheetViewModel,
    ) {
        val isBlackTheme = bottomSheetViewModel.theme.value == Theme.AmoledBlack
        val configuration = LocalConfiguration.current
        val landscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val result = bottomSheetViewModel.resolveResult

        val coroutineScope = rememberCoroutineScope()

        val drawerState = androidx.compose.material.rememberModalBottomSheetState(
            initialValue = androidx.compose.material.ModalBottomSheetValue.Expanded,
            skipHalfExpanded = false
        )

        LaunchedEffect(drawerState.currentValue) {
            if (drawerState.currentValue == androidx.compose.material.ModalBottomSheetValue.Hidden) {
                finish()
            }
        }

        val interactionSource = remember { MutableInteractionSource() }
        LegacyBottomDrawer(
            modifier = Modifier
                .runIf(landscape) {
                    it
                        .fillMaxWidth(0.55f)
                        .fillMaxHeight()
                }
                .nullClickable(),
            isBlackTheme = isBlackTheme,
            drawerState = drawerState,
            sheetContent = {
                SheetContent(result = result, landscape = landscape, hideDrawer = {
                    coroutineScope.launch { drawerState.hide() }
                })
            }
        )

//        val sheetState =
//            androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true)
//        LaunchedEffect(Unit) { sheetState.show() }
//
//        val dismissDrawer = { finish() }
//
//        BottomDrawer(
//            onDismissRequest = dismissDrawer,
//            modifier = Modifier
//                .runIf(landscape) {
//                    it
//                        .fillMaxWidth(0.55f)
//                        .fillMaxHeight()
//                },
//            isBlackTheme = isBlackTheme,
//            sheetState = sheetState
//        ) {
//            SheetContent(result = result, landscape = landscape, hideDrawer = {
//                coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
//                    dismissDrawer()
//                }
//            })
//        }
    }

    @Composable
    private fun SheetContent(
        result: BottomSheetResult?,
        landscape: Boolean,
        hideDrawer: () -> Unit
    ) {
        if (result != null && result is BottomSheetResult.BottomSheetSuccessResult && !result.hasAutoLaunchApp) {
            val showPackage = remember {
                result.showExtended || viewModel.alwaysShowPackageName.value
            }


            val maxHeight = (if (landscape) LocalConfiguration.current.screenWidthDp
            else LocalConfiguration.current.screenHeightDp) / 3f

            val itemHeight = if (viewModel.gridLayout.value) {
                val gridItemHeight =
                    gridItemHeight.value + if (showPackage) gridItemHeightPackageText.value else 0.0f + gridItemHeightPrivateButton.value

                gridItemHeight
            } else appListItemHeight.value

            val baseHeight = ((ceil((maxHeight / itemHeight).toDouble()) - 1) * itemHeight).dp

            if (result.filteredItem == null) {
                OpenWith(
                    bottomSheetViewModel = viewModel,
                    hideDrawer = hideDrawer,
                    baseHeight = baseHeight,
                    showPackage = showPackage,
                    previewUrl = viewModel.previewUrl.value
                )
            } else {
                OpenWithPreferred(
                    bottomSheetViewModel = viewModel,
                    hideDrawer = hideDrawer,
                    baseHeight = baseHeight,
                    showPackage = showPackage,
                    previewUrl = viewModel.previewUrl.value
                )
            }
        } else {
            val hasNoHandlers = result is BottomSheetResult.BottomSheetNoHandlersFound

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (hasNoHandlers) {
                    Text(
                        text = stringResource(id = R.string.no_handlers_found),
                        fontFamily = HkGroteskFontFamily,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = stringResource(id = R.string.no_handlers_found_explainer),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.loading_link),
                        fontFamily = HkGroteskFontFamily,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (!hasNoHandlers) {
                    Spacer(modifier = Modifier.height(10.dp))
                    CircularProgressIndicator()
                }
            }

            if (hasNoHandlers) {
                val padding = PaddingValues(horizontal = 10.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = buttonRowHeight)
                        .padding(buttonPadding)
                ) {
                    CopyButton(result, hideDrawer, padding)
                    Spacer(modifier = Modifier.width(2.dp))
                    ShareToButton(result, padding)
                }
            }
        }
    }

    @Composable
    private fun CopyButton(
        result: BottomSheetResult?,
        hideDrawer: () -> Unit,
        padding: PaddingValues,
    ) {
        OutlinedOrTextButton(
            textButton = viewModel.useTextShareCopyButtons.value,
            contentPadding = padding,
            onClick = {
                viewModel.clipboardManager.setText(
                    "URL",
                    result?.uri.toString()
                )

                if (!viewModel.urlCopiedToast.value) {
                    showToast(R.string.url_copied)
                }

                if (viewModel.hideAfterCopying.value) {
                    hideDrawer()
                }
            },
            buttonText = R.string.copy_url
        )
    }

    @Composable
    private fun ShareToButton(result: BottomSheetResult?, padding: PaddingValues) {
        OutlinedOrTextButton(
            textButton = viewModel.useTextShareCopyButtons.value,
            contentPadding = padding,
            onClick = {
                startActivity(Intent().buildSendTo(result?.uri))
                finish()
            },
            buttonText = R.string.send_to
        )
    }


    @Composable
    private fun OpenWithPreferred(
        bottomSheetViewModel: BottomSheetViewModel,
        hideDrawer: () -> Unit,
        baseHeight: Dp,
        showPackage: Boolean,
        previewUrl: Boolean
    ) {
        val result = bottomSheetViewModel.resolveResult!!
        if (result !is BottomSheetResult.BottomSheetSuccessResult) return

        val filteredItem = result.filteredItem!!

        Column(modifier = Modifier
            .fillMaxWidth()
            .clickable {
                launchApp(result, filteredItem, always = false)
            }
        ) {
            Spacer(modifier = Modifier.height(5.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp)
                    .heightIn(min = preferredAppItemHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    bitmap = filteredItem.iconBitmap,
                    contentDescription = filteredItem.label,
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.width(5.dp))

                Column {
                    Text(
                        text = stringResource(
                            id = R.string.open_with_app,
                            filteredItem.label,
                        ),
                        fontFamily = HkGroteskFontFamily,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (showPackage) {
                        Text(
                            text = filteredItem.packageName,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }

                    if (previewUrl) {
                        UrlPreview(uri = result.uri)
                    }
                }
            }

            ButtonRow(
                bottomSheetViewModel = bottomSheetViewModel,
                enabled = true,
                app = filteredItem,
                uri = result.uri,
                onClick = { launchApp(result, filteredItem, always = it) },
                hideDrawer = hideDrawer
            )
        }

        Divider(color = MaterialTheme.colorScheme.tertiary, thickness = 0.5.dp)

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            modifier = Modifier.padding(horizontal = 28.dp),
            text = stringResource(id = R.string.use_a_different_app),
            fontFamily = HkGroteskFontFamily,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(5.dp))

        Column(modifier = Modifier.nestedScroll(rememberNestedScrollInteropConnection())) {
            AppList(
                bottomSheetViewModel = bottomSheetViewModel,
                selectedItem = -1,
                onSelectedItemChange = { },
                baseHeight = baseHeight,
                showPackage = showPackage
            )
        }
    }

    @Composable
    private fun OpenWith(
        bottomSheetViewModel: BottomSheetViewModel,
        hideDrawer: () -> Unit,
        baseHeight: Dp,
        showPackage: Boolean,
        previewUrl: Boolean,
    ) {
        val result = bottomSheetViewModel.resolveResult!!
        if (result !is BottomSheetResult.BottomSheetSuccessResult) return

        Spacer(modifier = Modifier.height(15.dp))

        Column(modifier = Modifier.padding(horizontal = 28.dp)) {
            Text(
                text = stringResource(id = R.string.open_with),
                fontFamily = HkGroteskFontFamily,
                fontWeight = FontWeight.SemiBold,
            )

            if (previewUrl) {
                UrlPreview(uri = result.uri)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        var selectedItem by remember { mutableIntStateOf(-1) }
        Column(modifier = Modifier.nestedScroll(rememberNestedScrollInteropConnection())) {
            AppList(
                bottomSheetViewModel = bottomSheetViewModel,
                selectedItem = selectedItem,
                onSelectedItemChange = { selectedItem = it },
                baseHeight = baseHeight,
                showPackage = showPackage
            )

            ButtonRow(
                bottomSheetViewModel = bottomSheetViewModel,
                enabled = selectedItem != -1,
                app = null,
                uri = null,
                onClick = { always ->
                    launchApp(
                        result,
                        result.resolved[selectedItem],
                        always
                    )
                },
                hideDrawer = hideDrawer
            )
        }
    }

    private fun shouldShowRequestPrivate(info: DisplayActivityInfo): PrivateBrowsingBrowser.Firefox? {
        if (!viewModel.enableRequestPrivateBrowsingButton.value) return null
        return PrivateBrowsingBrowser.getSupportedBrowser(info.packageName)
    }


    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun AppList(
        bottomSheetViewModel: BottomSheetViewModel,
        selectedItem: Int,
        onSelectedItemChange: (Int) -> Unit,
        baseHeight: Dp,
        showPackage: Boolean
    ) {
        val result = bottomSheetViewModel.resolveResult!!
        if (result !is BottomSheetResult.BottomSheetSuccessResult) return

        if (result.isEmpty) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.no_app_to_handle_link_found)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            return
        }

        val modifier: AppListModifier = @Composable { index, info ->
            Modifier
                .fillMaxWidth()
                .clip(defaultRoundedCornerShape)
                .combinedClickable(
                    onClick = {
                        if (bottomSheetViewModel.singleTap.value) {
                            launchApp(result, info)
                        } else {
                            if (selectedItem == index) launchApp(result, info)
                            else onSelectedItemChange(index)
                        }
                    },
                    onDoubleClick = {
                        if (!bottomSheetViewModel.singleTap.value) {
                            launchApp(result, info)
                        } else {
                            startPackageInfoActivity(info)
                        }
                    },
                    onLongClick = {
                        if (bottomSheetViewModel.singleTap.value) {
                            onSelectedItemChange(index)
                        } else {
                            startPackageInfoActivity(info)
                        }
                    }
                )
                .background(if (selectedItem == index) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
                .padding(appListItemPadding)
        }

        if (bottomSheetViewModel.gridLayout.value) {
            LazyVerticalGrid(columns = GridCells.Adaptive(gridSize),
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .heightIn(0.dp, baseHeight),
                content = {
                    itemsIndexed(
                        items = result.resolved,
                        key = { _, item -> item.flatComponentName }
                    ) { index, info ->
                        val privateBrowser = shouldShowRequestPrivate(info)

                        Column(
                            modifier = modifier(
                                index,
                                info
                            ).height(
                                gridItemHeight
                                        + if (showPackage) gridItemHeightPackageText else 0.dp
                                        + if (privateBrowser != null) gridItemHeightPrivateButton else 0.dp
                            ),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Image(
                                bitmap = info.iconBitmap,
                                contentDescription = info.label,
                                modifier = Modifier.size(32.dp)
                            )

                            Spacer(modifier = Modifier.width(5.dp))

                            Text(
                                text = info.label,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )

                            if (showPackage) {
                                Text(
                                    text = info.packageName,
                                    fontSize = 12.sp,
                                    overflow = TextOverflow.Visible,
                                    lineHeight = 12.sp,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }

                            if (privateBrowser != null) {
                                RequestPrivateBrowsingButton(
                                    wrapInRow = false,
                                    supportedBrowser = privateBrowser,
                                    app = info,
                                    result = result
                                )
                            }
                        }
                    }
                })
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .heightIn(0.dp, baseHeight),
                content = {
                    itemsIndexed(
                        items = result.resolved,
                        key = { _, item -> item.flatComponentName }) { index, info ->

                        val shouldShowRequestPrivate = shouldShowRequestPrivate(info)

                        Row(
                            modifier = modifier(index, info).height(appListItemHeight),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    bitmap = info.iconBitmap,
                                    contentDescription = info.label,
                                    modifier = Modifier.size(32.dp)
                                )

                                Spacer(modifier = Modifier.width(5.dp))

                                Column {
                                    Text(text = info.label)
                                    if (showPackage) {
                                        Text(
                                            text = info.packageName,
                                            fontSize = 12.sp,
                                            lineHeight = 12.sp,
                                            color = MaterialTheme.colorScheme.tertiary
                                        )
                                    }
                                }
                            }

                            if (shouldShowRequestPrivate != null) {
                                RequestPrivateBrowsingButton(
                                    supportedBrowser = shouldShowRequestPrivate,
                                    app = info,
                                    result = result
                                )
                            }
                        }
                    }
                }
            )
        }
    }

    @Composable
    private fun RequestPrivateBrowsingButton(
        wrapInRow: Boolean = true,
        supportedBrowser: PrivateBrowsingBrowser,
        app: DisplayActivityInfo,
        result: BottomSheetResult.BottomSheetSuccessResult
    ) {
        if (wrapInRow) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Column {
                    RequestPrivateBrowsingTextButton(
                        app = app,
                        result = result,
                        supportedBrowser = supportedBrowser
                    )
                }
            }
        } else {
            RequestPrivateBrowsingTextButton(
                app = app,
                result = result,
                supportedBrowser = supportedBrowser
            )
        }
    }

    @Composable
    private fun RequestPrivateBrowsingTextButton(
        result: BottomSheetResult.BottomSheetSuccessResult,
        app: DisplayActivityInfo,
        supportedBrowser: PrivateBrowsingBrowser
    ) {
        TextButton(onClick = {
            launchApp(result, info = app, privateBrowsingBrowser = supportedBrowser)
        }) {
            Text(
                text = stringResource(id = R.string.request_private_browsing),
                textAlign = TextAlign.Center,
                fontFamily = HkGroteskFontFamily,
                fontWeight = FontWeight.SemiBold
            )
        }
    }

    @Composable
    private fun ButtonRow(
        bottomSheetViewModel: BottomSheetViewModel,
        enabled: Boolean,
        app: DisplayActivityInfo?,
        uri: Uri?,
        onClick: (always: Boolean) -> Unit,
        hideDrawer: () -> Unit
    ) {
        val result = bottomSheetViewModel.resolveResult!!
        if (result !is BottomSheetResult.BottomSheetSuccessResult) return

        val utilButtonWidthSum = utilButtonWidth * listOf(
            bottomSheetViewModel.enableCopyButton.value,
            bottomSheetViewModel.enableSendButton.value,
            bottomSheetViewModel.enableIgnoreLibRedirectButton.value,
            result.downloadable.isDownloadable(),
            bottomSheetViewModel.enableRequestPrivateBrowsingButton.value
        ).count { it }

        val configuration = LocalConfiguration.current
        val landscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        val widthHalf = if (landscape) {
            maxModalBottomSheetWidth
        } else LocalConfiguration.current.screenWidthDp.dp

        val useTwoRows = utilButtonWidthSum > widthHalf / 2
        val padding = PaddingValues(horizontal = 10.dp)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height((buttonRowHeight + if (useTwoRows) buttonRowHeight else 0.dp))
        ) {
            if (useTwoRows) {
                OpenButtons(
                    bottomSheetViewModel = bottomSheetViewModel,
                    enabled = enabled,
                    app = app,
                    arrangeEnd = false,
                    padding = padding,
                    onClick = onClick
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(buttonRowHeight)
                    .padding(horizontal = buttonPadding)
            ) {
                if (bottomSheetViewModel.enableCopyButton.value) {
                    CopyButton(result, hideDrawer, padding)
                }

                if (bottomSheetViewModel.enableSendButton.value) {
                    Spacer(modifier = Modifier.width(2.dp))
                    ShareToButton(result, padding)
                }

                if (result.downloadable.isDownloadable()) {
                    Spacer(modifier = Modifier.width(2.dp))
                    OutlinedOrTextButton(
                        textButton = bottomSheetViewModel.useTextShareCopyButtons.value,
                        contentPadding = padding,
                        onClick = {
                            bottomSheetViewModel.startDownload(
                                resources, result.uri,
                                result.downloadable as Downloader.DownloadCheckResult.Downloadable
                            )

                            if (!bottomSheetViewModel.downloadStartedToast.value) {
                                showToast(R.string.download_started)
                            }

                            hideDrawer()
                        },
                        buttonText = R.string.download
                    )
                }

                val libRedirectResult = result.libRedirectResult
                if (bottomSheetViewModel.enableIgnoreLibRedirectButton.value && libRedirectResult is LibRedirectResolver.LibRedirectResult.Redirected) {
                    Spacer(modifier = Modifier.width(2.dp))
                    OutlinedOrTextButton(
                        textButton = bottomSheetViewModel.useTextShareCopyButtons.value,
                        contentPadding = padding,
                        onClick = {
                            finish()
                            startActivity(
                                selfIntent(
                                    libRedirectResult.originalUri,
                                    bundleOf(LibRedirectDefault.libRedirectIgnore to true)
                                )
                            )
                        },
                        buttonText = R.string.ignore_libredirect
                    )
                }

                if (app != null) {
                    val privateBrowser = shouldShowRequestPrivate(app)
                    if (!useTwoRows && privateBrowser != null) {
                        Spacer(modifier = Modifier.width(2.dp))
                        RequestPrivateBrowsingButton(
                            wrapInRow = false,
                            supportedBrowser = privateBrowser,
                            app = app,
                            result = result
                        )
                    }
                }


                if (!useTwoRows) {
                    OpenButtons(
                        bottomSheetViewModel = bottomSheetViewModel,
                        enabled = enabled,
                        app = null,
                        arrangeEnd = true,
                        padding = padding,
                        onClick = onClick
                    )
                }
            }
        }
    }

    @Composable
    private fun OpenButtons(
        bottomSheetViewModel: BottomSheetViewModel,
        enabled: Boolean,
        app: DisplayActivityInfo? = null,
        arrangeEnd: Boolean = false,
        padding: PaddingValues,
        onClick: (always: Boolean) -> Unit,
    ) {
        val activity = LocalContext.currentActivity()
        val result = bottomSheetViewModel.resolveResult!!
        if (result !is BottomSheetResult.BottomSheetSuccessResult) return

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(buttonRowHeight)
                .padding(horizontal = buttonPadding),
            horizontalArrangement = if (arrangeEnd) Arrangement.End else Arrangement.Start
        ) {
            if (!result.isEmpty) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        enabled = enabled,
                        contentPadding = padding,
                        onClick = { onClick(false) }
                    ) {
                        Text(
                            text = stringResource(id = R.string.just_once),
                            fontFamily = HkGroteskFontFamily,
                            maxLines = 1,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.width(5.dp))

                    TextButton(
                        enabled = enabled,
                        contentPadding = padding,
                        onClick = { onClick(true) }
                    ) {
                        Text(
                            text = stringResource(id = R.string.always),
                            fontFamily = HkGroteskFontFamily,
                            maxLines = 1,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                if (app != null) {
                    val privateBrowser = shouldShowRequestPrivate(app)
                    if (privateBrowser != null) {
                        RequestPrivateBrowsingButton(
                            supportedBrowser = privateBrowser,
                            app = app,
                            result = result,
                        )
                    }
                }
            } else {
                TextButton(
                    contentPadding = padding,
                    enabled = true,
                    onClick = {
                        bottomSheetViewModel.startMainActivity(activity)
                    }) {
                    Text(
                        text = stringResource(id = R.string.open_settings),
                        fontFamily = HkGroteskFontFamily,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    @Composable
    private fun OutlinedOrTextButton(
        textButton: Boolean,
        contentPadding: PaddingValues,
        onClick: () -> Unit,
        @StringRes buttonText: Int
    ) {
        if (textButton) TextButton(
            contentPadding = contentPadding,
            onClick = onClick,
            content = {
                Text(
                    text = stringResource(id = buttonText),
                    fontFamily = HkGroteskFontFamily,
                    maxLines = 1,
                    fontWeight = FontWeight.SemiBold
                )
            }
        )
        else OutlinedButton(
            contentPadding = contentPadding,
            onClick = onClick,
            content = { Text(text = stringResource(id = buttonText)) })
    }

    @Composable
    private fun UrlPreview(uri: Uri?) {
        Text(
            text = uri.toString(),
            fontSize = 12.sp,
            lineHeight = 12.sp,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}
