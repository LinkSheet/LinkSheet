package fe.linksheet.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalContentColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import fe.kotlin.util.runIf
import fe.linksheet.R
import fe.linksheet.composable.util.BottomDrawer
import fe.linksheet.composable.util.defaultRoundedCornerShape
import fe.linksheet.extension.android.buildSendTo
import fe.linksheet.extension.android.setText
import fe.linksheet.extension.android.showToast
import fe.linksheet.extension.android.startPackageInfoActivity
import fe.linksheet.extension.compose.currentActivity
import fe.linksheet.extension.compose.nullClickable
import fe.linksheet.extension.compose.runIf
import fe.linksheet.interconnect.LinkSheetConnector
import fe.linksheet.module.database.entity.LibRedirectDefault
import fe.linksheet.module.downloader.Downloader
import fe.linksheet.module.resolver.LibRedirectResolver
import fe.linksheet.module.resolver.urlresolver.ResolveType
import fe.linksheet.module.viewmodel.BottomSheetViewModel
import fe.linksheet.resolver.BottomSheetResult
import fe.linksheet.resolver.DisplayActivityInfo
import fe.linksheet.ui.AppTheme
import fe.linksheet.ui.HkGroteskFontFamily
import fe.linksheet.ui.Theme
import fe.linksheet.util.PrivateBrowsingBrowser
import fe.linksheet.util.selfIntent
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.ceil

class BottomSheetActivity : ComponentActivity() {
    private val bottomSheetViewModel by viewModel<BottomSheetViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val deferred = resolveAsync(bottomSheetViewModel)
        if (bottomSheetViewModel.showLoadingBottomSheet()) {
            setContent {
                LaunchedEffect(bottomSheetViewModel.resolveResult) {
                    (bottomSheetViewModel.resolveResult as? BottomSheetResult.BottomSheetSuccessResult)?.resolveResults?.forEach { (resolveType, result) ->
                        if (result != null) makeResolveToast(
                            bottomSheetViewModel.resolveViaToast.value,
                            bottomSheetViewModel.resolveViaFailedToast.value,
                            result,
                            resolveType.stringResId
                        )
                    }

                }

                AppThemeBottomSheet(bottomSheetViewModel)
            }
        } else {
            deferred.invokeOnCompletion {
                setContent { AppThemeBottomSheet(bottomSheetViewModel) }
            }
        }
    }

    private fun resolveAsync(bottomSheetViewModel: BottomSheetViewModel): Deferred<Unit> {
        return lifecycleScope.async {
            val completed = bottomSheetViewModel.resolveAsync(intent, referrer).await()

            if (completed is BottomSheetResult.BottomSheetSuccessResult && completed.hasAutoLaunchApp) {
                completed.resolveResults.forEach { (resolveType, result) ->
                    if (result != null) makeResolveToast(
                        bottomSheetViewModel.resolveViaToast.value,
                        bottomSheetViewModel.resolveViaFailedToast.value,
                        result,
                        resolveType.stringResId,
                        true
                    )
                }

                if (bottomSheetViewModel.openingWithAppToast.value) {
                    showToast(
                        getString(R.string.opening_with_app, completed.app.label), uiThread = true
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

    @Composable
    private fun AppThemeBottomSheet(
        bottomSheetViewModel: BottomSheetViewModel,
    ) {
        AppTheme {
            BottomSheet(bottomSheetViewModel)
        }
    }

    private fun makeResolveToast(
        showResolveViaToast: Boolean,
        showResolveFailedToast: Boolean,
        result: Result<ResolveType>,
        @StringRes resolveTypeId: Int,
        uiThread: Boolean = false
    ) {
        result.getOrNull()?.let { type ->
            if (type !is ResolveType.NotResolved && showResolveViaToast) {
                showToast(
                    getString(
                        R.string.resolved_via, getString(resolveTypeId), getString(type.stringId)
                    ), uiThread = uiThread
                )
            }
        } ?: runIf(showResolveFailedToast) {
            showToast(
                getString(
                    R.string.resolve_failed, getString(resolveTypeId), result.exceptionOrNull()
                ), uiThread = uiThread
            )
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


    @OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
    @Composable
    private fun BottomSheet(
        bottomSheetViewModel: BottomSheetViewModel,
    ) {
        val isBlackTheme = bottomSheetViewModel.theme.value == Theme.AmoledBlack
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
            sheetContent = {
                SheetContent(result = result, landscape = landscape, hideDrawer = {
                    coroutineScope.launch { drawerState.hide() }
                })
            })
    }

    @Composable
    private fun SheetContent(
        result: BottomSheetResult?, landscape: Boolean, hideDrawer: () -> Unit
    ) {
        if (result != null && result is BottomSheetResult.BottomSheetSuccessResult && !result.hasAutoLaunchApp) {
            val showPackage = remember {
                result.showExtended || bottomSheetViewModel.alwaysShowPackageName.value
            }


            val maxHeight = (if (landscape) LocalConfiguration.current.screenWidthDp
            else LocalConfiguration.current.screenHeightDp) / 3f

            val itemHeight = if (bottomSheetViewModel.gridLayout.value) {
                val gridItemHeight =
                    gridItemHeight.value + if (showPackage) 10f else 0.0f

                gridItemHeight
            } else appListItemHeight.value

            val baseHeight = ((ceil((maxHeight / itemHeight).toDouble()) - 1) * itemHeight).dp

            if (result.filteredItem == null) {
                OpenWith(
                    bottomSheetViewModel = bottomSheetViewModel,
                    hideDrawer = hideDrawer,
                    showPackage = showPackage,
                    previewUrl = bottomSheetViewModel.previewUrl.value
                )
            } else {
                OpenWithPreferred(
                    bottomSheetViewModel = bottomSheetViewModel,
                    hideDrawer = hideDrawer,
                    showPackage = showPackage,
                    previewUrl = bottomSheetViewModel.previewUrl.value
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
                    CopyButton(
                        result,
                        hideDrawer,
                        isTextBasedButton = bottomSheetViewModel.useTextShareCopyButtons.value
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    ShareToButton(
                        result,
                        isTextBasedButton = bottomSheetViewModel.useTextShareCopyButtons.value
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun CopyButton(
        result: BottomSheetResult?,
        hideDrawer: () -> Unit,
        modifier: Modifier = Modifier,
        isTextBasedButton: Boolean
    ) {
        if (!isTextBasedButton) {
            ElevatedButton(
                modifier = modifier,
                onClick = {
                    bottomSheetViewModel.clipboardManager.setText(
                        "URL", result?.uri.toString()
                    )

                    if (!bottomSheetViewModel.urlCopiedToast.value) {
                        showToast(R.string.url_copied)
                    }

                    if (bottomSheetViewModel.hideAfterCopying.value) {
                        hideDrawer()
                    }
                },
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.ContentCopy, contentDescription = null
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = stringResource(id = R.string.copy_url),
                    fontFamily = HkGroteskFontFamily,
                    maxLines = 1,
                    fontWeight = FontWeight.SemiBold
                )
            }
        } else {
            TextButton(
                modifier = modifier,
                onClick = {
                    bottomSheetViewModel.clipboardManager.setText(
                        "URL", result?.uri.toString()
                    )

                    if (!bottomSheetViewModel.urlCopiedToast.value) {
                        showToast(R.string.url_copied)
                    }

                    if (bottomSheetViewModel.hideAfterCopying.value) {
                        hideDrawer()
                    }
                },
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.ContentCopy, contentDescription = null
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = stringResource(id = R.string.copy_url),
                    fontFamily = HkGroteskFontFamily,
                    maxLines = 1,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }

    @Composable
    private fun ShareToButton(
        result: BottomSheetResult?,
        modifier: Modifier = Modifier,
        isTextBasedButton: Boolean
    ) {
        if (!isTextBasedButton) {
            ElevatedButton(modifier = modifier,
                onClick = {
                    startActivity(Intent().buildSendTo(result?.uri))
                    finish()
                }
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.Share, contentDescription = null
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = stringResource(id = R.string.send_to),
                    fontFamily = HkGroteskFontFamily,
                    maxLines = 1,
                    fontWeight = FontWeight.SemiBold
                )
            }
        } else {
            TextButton(modifier = modifier,
                onClick = {
                    startActivity(Intent().buildSendTo(result?.uri))
                    finish()
                }
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.Share, contentDescription = null
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = stringResource(id = R.string.send_to),
                    fontFamily = HkGroteskFontFamily,
                    maxLines = 1,
                    fontWeight = FontWeight.SemiBold
                )
            }
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
            BtmSheetGridUI(
                bottomSheetViewModel = bottomSheetViewModel,
                hideDrawer = hideDrawer,
                showPackage = showPackage,
                previewUrl = previewUrl,
                forPreferred = true
            )
        } else {
            BtmSheetNonGridUI(
                bottomSheetViewModel = bottomSheetViewModel,
                hideDrawer = hideDrawer,
                showPackage = showPackage,
                previewUrl = previewUrl,
                forPreferred = true
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
            BtmSheetGridUI(
                bottomSheetViewModel = bottomSheetViewModel,
                hideDrawer = hideDrawer,
                showPackage = showPackage,
                previewUrl = previewUrl,
                forPreferred = false
            )
        } else {
            BtmSheetNonGridUI(
                bottomSheetViewModel = bottomSheetViewModel,
                hideDrawer = hideDrawer,
                showPackage = showPackage,
                previewUrl = previewUrl,
                forPreferred = false
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
        forPreferred: Boolean
    ) {
        val result = bottomSheetViewModel.resolveResult!!
        if (result !is BottomSheetResult.BottomSheetSuccessResult) return
        if (result.isEmpty) {
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.no_app_to_handle_link_found)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            return
        }
        if (forPreferred) {
            val filteredItem = result.filteredItem!!
            LaunchedEffect(key1 = filteredItem) {
                bottomSheetViewModel.appInfo.value = filteredItem
                bottomSheetViewModel.privateBrowser.value = shouldShowRequestPrivate(filteredItem)
            }
        }

        var selectedItem by remember { mutableIntStateOf(-1) }
        val modifier: AppListModifier = @Composable { index, info ->
            Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp)
                .clip(defaultRoundedCornerShape)
                .combinedClickable(onClick = {
                    bottomSheetViewModel.privateBrowser.value = shouldShowRequestPrivate(info)
                    bottomSheetViewModel.appInfo.value = info
                    if (forPreferred) {
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
                        this@BottomSheetActivity.startPackageInfoActivity(info)
                    }
                }, onLongClick = {
                    if (bottomSheetViewModel.singleTap.value) {
                        selectedItem = index
                    } else {
                        this@BottomSheetActivity.startPackageInfoActivity(info)
                    }
                })
                .background(
                    if (selectedItem == index) androidx.compose.material3.LocalContentColor.current.copy(
                        0.1f
                    ) else Color.Transparent
                )
                .padding(appListItemPadding)
        }
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            item(key = "previewUrl") {
                if (previewUrl) {
                    UrlPreview(uri = result.uri)
                }
            }
            if (forPreferred) {
                val filteredItem = result.filteredItem!!
                item {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            launchApp(result, filteredItem, always = false)
                        }) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 15.dp, end = 15.dp)
                                .heightIn(min = preferredAppItemHeight),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                bitmap = filteredItem.iconBitmap,
                                contentDescription = filteredItem.label,
                                modifier = Modifier.size(32.dp)
                            )

                            Spacer(modifier = Modifier.width(10.dp))

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
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    ButtonColumn(
                        bottomSheetViewModel = bottomSheetViewModel,
                        enabled = true,
                        uri = result.uri,
                        onClick = { launchApp(result, filteredItem, always = it) },
                        hideDrawer = hideDrawer
                    )
                }
                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(
                            start = 25.dp,
                            end = 25.dp,
                            top = 5.dp,
                            bottom = 5.dp
                        ),
                        color = MaterialTheme.colorScheme.outline.copy(0.25f)
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(5.dp))
                }
                item {
                    Text(
                        modifier = Modifier.padding(start = 15.dp),
                        text = stringResource(id = R.string.use_a_different_app),
                        fontFamily = HkGroteskFontFamily,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(5.dp))
                }
            } else {
                item(key = R.string.open_with) {
                    Text(
                        text = stringResource(id = R.string.open_with),
                        fontFamily = HkGroteskFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(
                            start = 15.dp,
                            top = if (previewUrl) 10.dp else 0.dp
                        )
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(5.dp))
                }
            }
            // App List Function:
            itemsIndexed(items = result.resolved,
                key = { _, item -> item.flatComponentName }) { index, info ->
                Row(
                    modifier = modifier(index, info).wrapContentHeight(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            bitmap = info.iconBitmap,
                            contentDescription = info.label,
                            modifier = Modifier.size(32.dp)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(text = info.label)
                            if (showPackage) {
                                Text(
                                    text = info.packageName,
                                    fontSize = 12.sp,
                                    lineHeight = 12.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            if (selectedItem == index) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.align(Alignment.CenterEnd)
                                )
                            }
                        }
                    }
                }
            }
            if (!forPreferred) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }
                item {
                    ButtonColumn(
                        bottomSheetViewModel = bottomSheetViewModel,
                        enabled = selectedItem != -1,
                        uri = null,
                        onClick = { always ->
                            launchApp(
                                result, result.resolved[selectedItem], always
                            )
                        },
                        hideDrawer = hideDrawer
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun BtmSheetGridUI(
        bottomSheetViewModel: BottomSheetViewModel,
        hideDrawer: () -> Unit,
        showPackage: Boolean,
        previewUrl: Boolean,
        forPreferred: Boolean
    ) {
        val result = bottomSheetViewModel.resolveResult!!
        if (result !is BottomSheetResult.BottomSheetSuccessResult) return
        if (result.isEmpty) {
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.no_app_to_handle_link_found)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            return
        }
        var selectedItem by remember { mutableIntStateOf(-1) }
        val modifier: AppListModifier = @Composable { index, info ->
            Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp)
                .clip(defaultRoundedCornerShape)
                .combinedClickable(onClick = {
                    bottomSheetViewModel.privateBrowser.value = shouldShowRequestPrivate(info)
                    bottomSheetViewModel.appInfo.value = info
                    if (forPreferred) {
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
                        this@BottomSheetActivity.startPackageInfoActivity(info)
                    }
                }, onLongClick = {
                    if (bottomSheetViewModel.singleTap.value) {
                        selectedItem = index
                    } else {
                        this@BottomSheetActivity.startPackageInfoActivity(info)
                    }
                })
                .background(
                    if (selectedItem == index) androidx.compose.material3.LocalContentColor.current.copy(
                        0.1f
                    ) else Color.Transparent
                )
                .padding(appListItemPadding)
        }
        if (forPreferred) {
            val filteredItem = result.filteredItem!!
            LaunchedEffect(key1 = filteredItem) {
                bottomSheetViewModel.appInfo.value = filteredItem
                bottomSheetViewModel.privateBrowser.value = shouldShowRequestPrivate(filteredItem)
            }
        }

        // https://stackoverflow.com/questions/69382494/jetpack-compose-vertical-grid-single-item-span-size
        LazyVerticalGrid(columns = GridCells.Adaptive(85.dp),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 4.dp),
            content = {
                item(key = "previewUrl", span = { GridItemSpan(maxCurrentLineSpan) }) {
                    if (previewUrl) {
                        UrlPreview(uri = result.uri)
                    }
                }
                if (forPreferred) {
                    val filteredItem = result.filteredItem!!
                    item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                launchApp(result, filteredItem, always = false)
                            }) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 15.dp, end = 15.dp)
                                    .heightIn(min = preferredAppItemHeight),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    bitmap = filteredItem.iconBitmap,
                                    contentDescription = filteredItem.label,
                                    modifier = Modifier.size(32.dp)
                                )

                                Spacer(modifier = Modifier.width(10.dp))

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
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                    item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                        ButtonColumn(
                            bottomSheetViewModel = bottomSheetViewModel,
                            enabled = true,
                            uri = result.uri,
                            onClick = { launchApp(result, filteredItem, always = it) },
                            hideDrawer = hideDrawer
                        )
                    }
                    item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                        HorizontalDivider(
                            modifier = Modifier.padding(
                                start = 25.dp,
                                end = 25.dp,
                                top = 5.dp,
                                bottom = 5.dp
                            ),
                            color = MaterialTheme.colorScheme.outline.copy(0.25f)
                        )
                    }
                    item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                        Text(
                            modifier = Modifier.padding(start = 15.dp),
                            text = stringResource(id = R.string.use_a_different_app),
                            fontFamily = HkGroteskFontFamily,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                        Spacer(modifier = Modifier.height(5.dp))
                    }
                } else {
                    item(key = R.string.open_with, span = { GridItemSpan(maxCurrentLineSpan) }) {
                        Text(
                            text = stringResource(id = R.string.open_with),
                            fontFamily = HkGroteskFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(
                                start = 15.dp,
                                top = if (previewUrl) 10.dp else 0.dp
                            )
                        )
                    }
                    item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                        Spacer(modifier = Modifier.height(5.dp))
                    }
                }
                itemsIndexed(items = result.resolved,
                    key = { _, item -> item.flatComponentName }) { index, info ->
                    Column(
                        modifier = modifier(
                            index, info
                        ), horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            bitmap = info.iconBitmap,
                            contentDescription = info.label,
                            modifier = Modifier.size(32.dp)
                        )

                        Text(
                            text = info.label,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 3.dp)
                        )

                        if (showPackage) {
                            Text(
                                text = info.packageName,
                                fontSize = 12.sp,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        }
                    }
                }
                if (!forPreferred) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        ButtonColumn(
                            bottomSheetViewModel = bottomSheetViewModel,
                            enabled = selectedItem != -1,
                            uri = null,
                            onClick = { always ->
                                launchApp(
                                    result, result.resolved[selectedItem], always
                                )
                            },
                            hideDrawer = hideDrawer
                        )
                    }
                }
            })
    }

    private fun shouldShowRequestPrivate(info: DisplayActivityInfo): PrivateBrowsingBrowser.Firefox? {
        if (!bottomSheetViewModel.enableRequestPrivateBrowsingButton.value) return null
        return PrivateBrowsingBrowser.getSupportedBrowser(info.packageName)
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    private fun ButtonColumn(
        bottomSheetViewModel: BottomSheetViewModel,
        enabled: Boolean,
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
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth()) {
                if (bottomSheetViewModel.enableCopyButton.value) {
                    CopyButton(
                        modifier = Modifier
                            .fillMaxWidth(if (bottomSheetViewModel.enableSendButton.value) 0.5f else 1f)
                            .padding(start = 15.dp, end = 15.dp),
                        result = result,
                        hideDrawer = hideDrawer,
                        isTextBasedButton = bottomSheetViewModel.useTextShareCopyButtons.value
                    )
                }
                if (bottomSheetViewModel.enableSendButton.value) {
                    ShareToButton(
                        result = result,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                start = if (bottomSheetViewModel.enableCopyButton.value) 0.dp else 15.dp,
                                end = 15.dp
                            ),
                        isTextBasedButton = bottomSheetViewModel.useTextShareCopyButtons.value
                    )
                }
            }
            if (result.downloadable.isDownloadable()) {
                Spacer(modifier = Modifier.height(5.dp))
                if (!bottomSheetViewModel.useTextShareCopyButtons.value) {
                    ElevatedButton(modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 15.dp, end = 15.dp), onClick = {
                        bottomSheetViewModel.startDownload(
                            resources,
                            result.uri,
                            result.downloadable as Downloader.DownloadCheckResult.Downloadable
                        )

                        if (!bottomSheetViewModel.downloadStartedToast.value) {
                            showToast(R.string.download_started)
                        }

                        hideDrawer()
                    }) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = stringResource(id = R.string.download),
                            fontFamily = HkGroteskFontFamily,
                            maxLines = 1,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    TextButton(modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 15.dp, end = 15.dp), onClick = {
                        bottomSheetViewModel.startDownload(
                            resources,
                            result.uri,
                            result.downloadable as Downloader.DownloadCheckResult.Downloadable
                        )

                        if (!bottomSheetViewModel.downloadStartedToast.value) {
                            showToast(R.string.download_started)
                        }

                        hideDrawer()
                    }) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = stringResource(id = R.string.download),
                            fontFamily = HkGroteskFontFamily,
                            maxLines = 1,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

            }
            if (useTwoRows) {
                OpenButtons(
                    bottomSheetViewModel = bottomSheetViewModel,
                    enabled = enabled,
                    onClick = onClick
                )
            }
            val libRedirectResult = result.libRedirectResult
            if (bottomSheetViewModel.enableIgnoreLibRedirectButton.value && libRedirectResult is LibRedirectResolver.LibRedirectResult.Redirected) {
                ElevatedOrTextButton(
                    textButton = bottomSheetViewModel.useTextShareCopyButtons.value,
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


            if (!useTwoRows && bottomSheetViewModel.appInfo.value != null) {
                OpenButtons(
                    bottomSheetViewModel = bottomSheetViewModel,
                    enabled = enabled,
                    onClick = onClick
                )
            }
        }
    }

    @Composable
    private fun OpenButtons(
        bottomSheetViewModel: BottomSheetViewModel,
        enabled: Boolean,
        onClick: (always: Boolean) -> Unit
    ) {
        val activity = LocalContext.currentActivity()
        val result = bottomSheetViewModel.resolveResult!!
        if (result !is BottomSheetResult.BottomSheetSuccessResult) return
        if (!result.isEmpty) {
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .animateContentSize()
            ) {
                HorizontalDivider(
                    modifier = Modifier.padding(
                        start = 25.dp,
                        end = 25.dp,
                        top = 5.dp,
                        bottom = 5.dp
                    ),
                    color = MaterialTheme.colorScheme.outline.copy(0.25f)
                )
                if (bottomSheetViewModel.privateBrowser.value != null) {
                    Button(
                        enabled = enabled,
                        onClick = {
                            bottomSheetViewModel.appInfo.value?.let {
                                launchApp(
                                    result,
                                    info = it,
                                    privateBrowsingBrowser = bottomSheetViewModel.privateBrowser.value
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 15.dp, end = 15.dp)
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = stringResource(id = R.string.request_private_browsing),
                            textAlign = TextAlign.Center,
                            fontFamily = HkGroteskFontFamily,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Button(
                    enabled = enabled,
                    onClick = { onClick(false) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 15.dp, end = 15.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.just_once),
                        fontFamily = HkGroteskFontFamily,
                        maxLines = 1,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Button(
                    enabled = enabled,
                    onClick = { onClick(true) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 15.dp, end = 15.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.always),
                        fontFamily = HkGroteskFontFamily,
                        maxLines = 1,
                        fontWeight = FontWeight.SemiBold
                    )
                }

            }
        } else {
            ElevatedOrTextButton(
                onClick = {
                    bottomSheetViewModel.startMainActivity(activity)
                },
                textButton = bottomSheetViewModel.useTextShareCopyButtons.value,
                buttonText = R.string.open_settings
            )
        }
    }

    @Composable
    private fun ElevatedOrTextButton(
        textButton: Boolean,
        onClick: () -> Unit,
        @StringRes buttonText: Int
    ) {
        if (textButton) TextButton(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp),
            content = {
                Text(
                    text = stringResource(id = buttonText),
                    fontFamily = HkGroteskFontFamily,
                    maxLines = 1,
                    fontWeight = FontWeight.SemiBold
                )
            })
        else ElevatedButton(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, end = 15.dp),
            onClick = onClick,
            content = { Text(text = stringResource(id = buttonText)) })
    }

    @Composable
    private fun UrlPreview(uri: Uri?) {
        Card(
            border = BorderStroke(
                1.dp,
                contentColorFor(LocalContentColor.current)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(
                        top = 10.dp, bottom = 10.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.CenterStart
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Outlined.Link,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(
                                start = 10.dp, end = 10.dp
                            )
                    )
                }
                Text(
                    text = uri.toString(),
                    fontSize = 12.sp,
                    lineHeight = 12.sp,
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp)
                )
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun launchApp(
        result: BottomSheetResult.BottomSheetSuccessResult,
        info: DisplayActivityInfo,
        always: Boolean = false,
        privateBrowsingBrowser: PrivateBrowsingBrowser? = null,
        persist: Boolean = true,
    ) {
        val deferred = bottomSheetViewModel.launchAppAsync(
            info, result.intent, always, privateBrowsingBrowser,
            persist,
        )

        deferred.invokeOnCompletion {
            val showAsReferrer = bottomSheetViewModel.showAsReferrer.value
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
}

typealias AppListModifier = @Composable (index: Int, info: DisplayActivityInfo) -> Modifier