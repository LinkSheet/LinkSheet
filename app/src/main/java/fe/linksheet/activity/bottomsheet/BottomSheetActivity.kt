package fe.linksheet.activity.bottomsheet

import android.app.DownloadManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.junkfood.seal.ui.component.BottomDrawer
import com.tasomaniac.openwith.resolver.DisplayActivityInfo
import com.tasomaniac.openwith.resolver.BottomSheetResult
import fe.linksheet.R
import fe.linksheet.extension.currentActivity
import fe.linksheet.extension.buildSendTo
import fe.linksheet.extension.runIf
import fe.linksheet.extension.showToast
import fe.linksheet.extension.newIntent
import fe.linksheet.extension.startPackageInfoActivity
import fe.linksheet.module.downloader.Downloader
import fe.linksheet.ui.theme.AppTheme
import fe.linksheet.ui.theme.HkGroteskFontFamily
import fe.linksheet.ui.theme.Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.math.ceil


class BottomSheetActivity : ComponentActivity() {
    private lateinit var bottomSheetViewModel: BottomSheetViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bottomSheetViewModel = viewModels<BottomSheetViewModel>().value

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

        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else WindowManager.LayoutParams.TYPE_SYSTEM_ALERT

        window.setType(type)

        val deferred = resolveAsync()

        if (bottomSheetViewModel.showLoadingBottomSheet()) {
            setContent {
                LaunchedEffect(bottomSheetViewModel.resolveResult) {
                    bottomSheetViewModel.resolveResult?.followRedirect?.resolveType?.let(
                        ::makeResolveToast
                    )
                }

                AppThemeBottomSheet(bottomSheetViewModel.resolveResult)
            }
        } else {
            deferred.invokeOnCompletion {
                setContent { AppThemeBottomSheet(bottomSheetViewModel.resolveResult) }
            }
        }
    }

    private fun resolveAsync(): Deferred<Unit> {
        return lifecycleScope.async {
            val completed = bottomSheetViewModel.resolveAsync(
                this@BottomSheetActivity, intent, referrer
            ).await()

            if (completed.hasAutoLaunchApp) {
                if (!bottomSheetViewModel.disableToasts) {
                    completed.followRedirect?.resolveType?.let { makeResolveToast(it, true) }

                    showToast(
                        getString(R.string.opening_with_app, completed.app.label),
                        uiThread = true
                    )
                }

                launchApp(completed.app, completed.uri, completed.isRegularPreferredApp)
            }
        }
    }

    @Composable
    private fun AppThemeBottomSheet(result: BottomSheetResult?) {
        AppTheme(bottomSheetViewModel.theme) {
            BottomSheet(result, bottomSheetViewModel.theme == Theme.AmoledBlack)
        }
    }

    private fun makeResolveToast(
        type: BottomSheetViewModel.FollowRedirectResolveType,
        uiThread: Boolean = false
    ) {
        if (!type.isNotResolved()) {
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

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun BottomSheet(result: BottomSheetResult?, isBlackTheme: Boolean) {
        val configuration = LocalConfiguration.current
        val landscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        val drawerState = rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Expanded,
            skipHalfExpanded = false
        )

        LaunchedEffect(drawerState.currentValue) {
            if (drawerState.currentValue == ModalBottomSheetValue.Hidden) {
                this@BottomSheetActivity.finish()
            }
        }

        val launchScope = rememberCoroutineScope()
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
                if (result != null && !result.hasAutoLaunchApp) {
                    val showPackage = remember {
                        result.showExtended || bottomSheetViewModel.alwaysShowPackageName
                    }

                    val maxHeight = (if (landscape) LocalConfiguration.current.screenWidthDp
                    else LocalConfiguration.current.screenHeightDp) / 3f

                    val itemHeight = if (bottomSheetViewModel.gridLayout) {
                        val gridItemHeight = if (showPackage) gridItemHeightPackage.value
                        else gridItemHeight.value

                        gridItemHeight
                    } else appListItemHeight.value

                    val baseHeight =
                        ((ceil((maxHeight / itemHeight).toDouble()) - 1) * itemHeight).dp

                    if (result.filteredItem == null) {
                        OpenWith(
                            result = result,
                            launchScope = launchScope,
                            drawerState = drawerState,
                            baseHeight = baseHeight,
                            showPackage = showPackage,
                            previewUrl = bottomSheetViewModel.previewUrl
                        )
                    } else {
                        OpenWithPreferred(
                            result = result,
                            launchScope = launchScope,
                            drawerState = drawerState,
                            baseHeight = baseHeight,
                            showPackage = showPackage,
                            previewUrl = bottomSheetViewModel.previewUrl
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
            })
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun OpenWithPreferred(
        result: BottomSheetResult,
        launchScope: CoroutineScope,
        drawerState: ModalBottomSheetState,
        baseHeight: Dp,
        showPackage: Boolean,
        previewUrl: Boolean
    ) {
        val filteredItem = result.filteredItem!!

        Column(modifier = Modifier
            .fillMaxWidth()
            .clickable {
                launchScope.launch { launchApp(filteredItem, result.uri, false) }
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
                result = result,
                enabled = true,
                onClick = { launchScope.launch { launchApp(filteredItem, result.uri, it) } },
                drawerState = drawerState
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
                result = result,
                selectedItem = -1,
                onSelectedItemChange = { },
                launchScope = launchScope,
                baseHeight = baseHeight,
                showPackage = showPackage
            )
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun OpenWith(
        result: BottomSheetResult,
        launchScope: CoroutineScope,
        drawerState: ModalBottomSheetState,
        baseHeight: Dp,
        showPackage: Boolean,
        previewUrl: Boolean,
    ) {
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
                result = result,
                selectedItem = selectedItem,
                onSelectedItemChange = { selectedItem = it },
                launchScope = launchScope,
                baseHeight = baseHeight,
                showPackage = showPackage
            )

            ButtonRow(
                result = result,
                enabled = selectedItem != -1,
                onClick = { always ->
                    launchScope.launch {
                        launchApp(result.resolved[selectedItem], result.uri, always)
                    }
                },
                drawerState = drawerState
            )
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun AppList(
        result: BottomSheetResult,
        selectedItem: Int,
        onSelectedItemChange: (Int) -> Unit,
        launchScope: CoroutineScope,
        baseHeight: Dp,
        showPackage: Boolean
    ) {
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
                        if (bottomSheetViewModel.singleTap) {
                            launchScope.launch {
                                launchApp(info, result.uri)
                            }
                        } else {
                            if (selectedItem == index) launchScope.launch {
                                launchApp(info, result.uri)
                            }
                            else onSelectedItemChange(index)
                        }
                    },
                    onDoubleClick = {
                        if (!bottomSheetViewModel.singleTap) {
                            launchScope.launch {
                                launchApp(info, result.uri)
                            }
                        } else {
                            this@BottomSheetActivity.startPackageInfoActivity(info)
                        }
                    },
                    onLongClick = {
                        if (bottomSheetViewModel.singleTap) {
                            onSelectedItemChange(index)
                        } else {
                            this@BottomSheetActivity.startPackageInfoActivity(info)
                        }
                    }
                )
                .background(if (selectedItem == index) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
                .padding(appListItemPadding)
        }

        if (bottomSheetViewModel.gridLayout) {
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

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun ButtonRow(
        result: BottomSheetResult,
        enabled: Boolean,
        onClick: (always: Boolean) -> Unit,
        drawerState: ModalBottomSheetState
    ) {
        val coroutineScope = rememberCoroutineScope()
        val clipboard = remember { getSystemService(ClipboardManager::class.java) }
        val downloadManager = remember { getSystemService(DownloadManager::class.java) }


        val context = LocalContext.current

        val utilButtonWidthSum = utilButtonWidth * listOf(
            bottomSheetViewModel.enableCopyButton,
            bottomSheetViewModel.enableSendButton,
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
                OpenButtons(result, enabled, false, padding, onClick)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(buttonRowHeight)
                    .padding(horizontal = buttonPadding)
            ) {

                if (bottomSheetViewModel.enableCopyButton) {
                    OutlinedOrTextButton(
                        textButton = bottomSheetViewModel.useTextShareCopyButtons,
                        contentPadding = padding,
                        onClick = {
                            clipboard.setPrimaryClip(
                                ClipData.newPlainText(
                                    "URL",
                                    result.uri.toString()
                                )
                            )
                            if (!bottomSheetViewModel.disableToasts) {
                                showToast(R.string.url_copied)
                            }

                            if (bottomSheetViewModel.hideAfterCopying) {
                                coroutineScope.launch { drawerState.hide() }
                            }
                        },
                        buttonText = R.string.copy
                    )
                }

                if (bottomSheetViewModel.enableSendButton) {
                    Spacer(modifier = Modifier.width(2.dp))
                    OutlinedOrTextButton(
                        textButton = bottomSheetViewModel.useTextShareCopyButtons,
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
                        textButton = bottomSheetViewModel.useTextShareCopyButtons,
                        contentPadding = padding,
                        onClick = {
                            startDownload(
                                context.resources, downloadManager, result.uri,
                                result.downloadable as Downloader.DownloadCheckResult.Downloadable
                            )
                        },
                        buttonText = R.string.download
                    )
                }

                if (!useTwoRows) {
                    OpenButtons(result, enabled, true, padding, onClick)
                }
            }
        }
    }

    @Composable
    private fun OpenButtons(
        result: BottomSheetResult,
        enabled: Boolean,
        arrangeEnd: Boolean = false,
        padding: PaddingValues,
        onClick: (always: Boolean) -> Unit,
    ) {
        val activity = LocalContext.currentActivity()

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
            content = { Text(text = stringResource(id = buttonText)) }
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

    private fun startDownload(
        resources: Resources,
        downloadManager: DownloadManager,
        uri: Uri?,
        downloadable: Downloader.DownloadCheckResult.Downloadable
    ) {
        bottomSheetViewModel.startDownload(resources, downloadManager, uri, downloadable)
    }

    private suspend fun launchApp(info: DisplayActivityInfo, uri: Uri?, always: Boolean = false) {
        val intentFrom = info.intentFrom(intent.newIntent(uri))
        bottomSheetViewModel.persistSelectedIntentAsync(intentFrom, always)

        this.startActivity(intentFrom)
        this.finish()
    }
}

typealias AppListModifier = @Composable (index: Int, info: DisplayActivityInfo) -> Modifier