package fe.linksheet.activity

import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import fe.linksheet.R
import fe.linksheet.composable.util.BottomDrawer
import fe.linksheet.extension.buildSendTo
import fe.linksheet.extension.currentActivity
import fe.linksheet.extension.runIf
import fe.linksheet.extension.selfIntent
import fe.linksheet.extension.setText
import fe.linksheet.extension.showToast
import fe.linksheet.extension.startPackageInfoActivity
import fe.linksheet.module.database.entity.LibRedirectDefault
import fe.linksheet.module.downloader.Downloader
import fe.linksheet.module.resolver.LibRedirectResolver
import fe.linksheet.module.resolver.RedirectFollower
import fe.linksheet.module.viewmodel.BottomSheetViewModel
import fe.linksheet.resolver.BottomSheetResult
import fe.linksheet.resolver.DisplayActivityInfo
import fe.linksheet.ui.AppTheme
import fe.linksheet.ui.HkGroteskFontFamily
import fe.linksheet.ui.Theme
import fe.linksheet.util.AndroidVersion
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

        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { v, insets ->
            v.setPadding(0, 0, 0, 0)
            insets
        }

        window.setBackgroundDrawable(ColorDrawable(0))
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        val type = if (AndroidVersion.AT_LEAST_API_26_O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else WindowManager.LayoutParams.TYPE_SYSTEM_ALERT

        window.setType(type)

        val deferred = resolveAsync(bottomSheetViewModel)

        if (bottomSheetViewModel.showLoadingBottomSheet()) {
            setContent {
                LaunchedEffect(bottomSheetViewModel.resolveResult) {
                    bottomSheetViewModel.resolveResult?.followRedirect?.let(
                        ::makeResolveToast
                    )
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

            if (completed.hasAutoLaunchApp) {
                if (!bottomSheetViewModel.disableToasts.value) {
                    completed.followRedirect?.let { makeResolveToast(it, true) }

                    showToast(
                        getString(R.string.opening_with_app, completed.app.label),
                        uiThread = true
                    )
                }

                launchApp(
                    completed.app,
                    completed.uri,
                    completed.isRegularPreferredApp
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
        type: RedirectFollower.FollowRedirect,
        uiThread: Boolean = false
    ) {
        if (type !is RedirectFollower.FollowRedirect.NotResolved) {
            showToast(
                getString(R.string.resolved_via, getString(type.stringId)),
                uiThread = uiThread
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
        var gridItemHeightPackage = 80.dp
        var gridItemHeight = 60.dp

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
        BottomDrawer(
            modifier = Modifier
                .runIf(landscape) {
                    it
                        .fillMaxWidth(0.55f)
                        .fillMaxHeight()
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {},
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
        if (result != null && !result.hasAutoLaunchApp) {
            val showPackage = remember {
                result.showExtended || bottomSheetViewModel.alwaysShowPackageName.value
            }

            val maxHeight = (if (landscape) LocalConfiguration.current.screenWidthDp
            else LocalConfiguration.current.screenHeightDp) / 3f

            val itemHeight = if (bottomSheetViewModel.gridLayout.value) {
                val gridItemHeight = if (showPackage) gridItemHeightPackage.value
                else gridItemHeight.value

                gridItemHeight
            } else appListItemHeight.value

            val baseHeight =
                ((ceil((maxHeight / itemHeight).toDouble()) - 1) * itemHeight).dp

            if (result.filteredItem == null) {
                OpenWith(
                    bottomSheetViewModel = bottomSheetViewModel,
                    hideDrawer = hideDrawer,
                    baseHeight = baseHeight,
                    showPackage = showPackage,
                    previewUrl = bottomSheetViewModel.previewUrl.value
                )
            } else {
                OpenWithPreferred(
                    bottomSheetViewModel = bottomSheetViewModel,
                    hideDrawer = hideDrawer,
                    baseHeight = baseHeight,
                    showPackage = showPackage,
                    previewUrl = bottomSheetViewModel.previewUrl.value
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.loading_link),
                    fontFamily = HkGroteskFontFamily,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(10.dp))
                CircularProgressIndicator()
            }
        }
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
        val filteredItem = result.filteredItem!!

        Column(modifier = Modifier
            .fillMaxWidth()
            .clickable {
                launchApp(filteredItem, result.uri, false)
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
                bottomSheetViewModel = bottomSheetViewModel, enabled = true,
                onClick = { launchApp(filteredItem, result.uri, it) },
                hideDrawer = hideDrawer
            )
        }

        Divider(color = MaterialTheme.colorScheme.tertiary, thickness = 0.5.dp)

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(id = R.string.use_a_different_app),
            fontFamily = HkGroteskFontFamily,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 28.dp)
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

        var selectedItem by remember { mutableStateOf(-1) }
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
                onClick = { always ->
                    launchApp(
                        result.resolved[selectedItem],
                        result.uri,
                        always
                    )
                },
                hideDrawer = hideDrawer
            )
        }
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
        if (result.isEmpty) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
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
                .clip(RoundedCornerShape(6.dp))
                .combinedClickable(
                    onClick = {
                        if (bottomSheetViewModel.singleTap.value) {
                            launchApp(info, result.uri)
                        } else {
                            if (selectedItem == index) launchApp(
                                info,
                                result.uri
                            )
                            else onSelectedItemChange(index)
                        }
                    },
                    onDoubleClick = {
                        if (!bottomSheetViewModel.singleTap.value) {
                            launchApp(info, result.uri)
                        } else {
                            this@BottomSheetActivity.startPackageInfoActivity(info)
                        }
                    },
                    onLongClick = {
                        if (bottomSheetViewModel.singleTap.value) {
                            onSelectedItemChange(index)
                        } else {
                            this@BottomSheetActivity.startPackageInfoActivity(info)
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
                        Column(
                            modifier = modifier(
                                index,
                                info
                            ).height(if (showPackage) gridItemHeightPackage else gridItemHeight),
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = modifier(index, info).height(appListItemHeight)
                        ) {
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
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                            }
                        }
                    }
                }
            )
        }

    }

    @Composable
    private fun ButtonRow(
        bottomSheetViewModel: BottomSheetViewModel,
        enabled: Boolean,
        onClick: (always: Boolean) -> Unit,
        hideDrawer: () -> Unit
    ) {
        val context = LocalContext.current
        val result = bottomSheetViewModel.resolveResult!!

        val utilButtonWidthSum = utilButtonWidth * listOf(
            bottomSheetViewModel.enableCopyButton.value,
            bottomSheetViewModel.enableSendButton.value,
            bottomSheetViewModel.enableIgnoreLibRedirectButton.value,
            result.downloadable.isDownloadable()
        ).count { it }

        val configuration = LocalConfiguration.current
        val landscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        val widthHalf =
            if (landscape) maxModalBottomSheetWidth else LocalConfiguration.current.screenWidthDp.dp

        val useTwoRows = utilButtonWidthSum > widthHalf / 2
        val padding = PaddingValues(horizontal = 10.dp)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height((buttonRowHeight + if (useTwoRows) buttonRowHeight else 0.dp))
        ) {
            if (useTwoRows) {
                OpenButtons(bottomSheetViewModel, enabled, false, padding, onClick)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(buttonRowHeight)
                    .padding(horizontal = buttonPadding)
            ) {

                if (bottomSheetViewModel.enableCopyButton.value) {
                    OutlinedOrTextButton(
                        textButton = bottomSheetViewModel.useTextShareCopyButtons.value,
                        contentPadding = padding,
                        onClick = {
                            bottomSheetViewModel.clipboardManager.setText(
                                "URL",
                                result.uri.toString()
                            )

                            if (!bottomSheetViewModel.disableToasts.value) {
                                showToast(R.string.url_copied)
                            }

                            if (bottomSheetViewModel.hideAfterCopying.value) {
                                hideDrawer()
                            }
                        },
                        buttonText = R.string.copy
                    )
                }

                if (bottomSheetViewModel.enableSendButton.value) {
                    Spacer(modifier = Modifier.width(2.dp))
                    OutlinedOrTextButton(
                        textButton = bottomSheetViewModel.useTextShareCopyButtons.value,
                        contentPadding = padding,
                        onClick = {
                            startActivity(Intent().buildSendTo(result.uri))
                            finish()
                        },
                        buttonText = R.string.send_to
                    )
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

                            if (!bottomSheetViewModel.disableToasts.value) {
                                showToast(R.string.download_started)
                            }

                            hideDrawer()
                        },
                        buttonText = R.string.download
                    )
                }

                val libRedirectResult = bottomSheetViewModel.resolveResult!!.libRedirectResult
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

                if (!useTwoRows) {
                    OpenButtons(bottomSheetViewModel, enabled, true, padding, onClick)
                }
            }
        }
    }

    @Composable
    private fun OpenButtons(
        bottomSheetViewModel: BottomSheetViewModel,
        enabled: Boolean,
        arrangeEnd: Boolean = false,
        padding: PaddingValues,
        onClick: (always: Boolean) -> Unit,
    ) {
        val activity = LocalContext.currentActivity()
        val result = bottomSheetViewModel.resolveResult!!

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(buttonRowHeight)
                .runIf(arrangeEnd,
                    { it.padding(end = buttonPadding) },
                    { it.padding(start = buttonPadding) }
                ),
            horizontalArrangement = if (arrangeEnd) Arrangement.End else Arrangement.Start
        ) {
            if (!result.isEmpty) {
                TextButton(
                    enabled = enabled,
                    contentPadding = padding,
                    onClick = { onClick(false) }) {
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
                    onClick = { onClick(true) }) {
                    Text(
                        text = stringResource(id = R.string.always),
                        fontFamily = HkGroteskFontFamily,
                        maxLines = 1,
                        fontWeight = FontWeight.SemiBold
                    )
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
                    text = stringResource(id = buttonText), fontFamily = HkGroteskFontFamily,
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

    @OptIn(ExperimentalCoroutinesApi::class)
    fun launchApp(
        info: DisplayActivityInfo,
        uri: Uri?,
        always: Boolean = false
    ) {
        val deferred = bottomSheetViewModel.launchApp(info, intent, uri, always)
        deferred.invokeOnCompletion {
            startActivity(deferred.getCompleted())
            finish()
        }
    }
}

typealias AppListModifier = @Composable (index: Int, info: DisplayActivityInfo) -> Modifier