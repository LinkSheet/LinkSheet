package fe.linksheet.activity.bottomsheet

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.junkfood.seal.ui.component.BottomDrawer
import com.tasomaniac.openwith.resolver.DisplayActivityInfo
import com.tasomaniac.openwith.resolver.IntentResolverResult
import fe.linksheet.R
import fe.linksheet.activity.MainActivity
import fe.linksheet.extension.buildSendTo
import fe.linksheet.extension.getUri
import fe.linksheet.extension.sourceIntent
import fe.linksheet.ui.theme.AppTheme
import fe.linksheet.ui.theme.HkGroteskFontFamily
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class BottomSheetActivity : ComponentActivity() {
    private val bottomSheetViewModel: BottomSheetViewModel by viewModels()

    @OptIn(
        ExperimentalMaterialApi::class, ExperimentalMaterialApi::class,
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { v, insets ->
            v.setPadding(0, 0, 0, 0)
            insets
        }

        window.run {
            setBackgroundDrawable(ColorDrawable(0))
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            } else {
                setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
            }
        }

        val deferred = lifecycleScope.async {
            val completed = bottomSheetViewModel.resolveAsync(
                this@BottomSheetActivity, intent
            ).await()

            if (completed != null && completed.alwaysPreferred == true || completed!!.isSingleBrowserOnlyResolvedItem) {
                val app = completed.filteredItem ?: completed.resolved[0]
                if (!bottomSheetViewModel.disableToasts) {
                    runOnUiThread {
                        Toast.makeText(
                            this@BottomSheetActivity,
                            getString(R.string.opening_with_app, app.displayLabel),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                launchApp(app, true)
            }
        }

        deferred.invokeOnCompletion {
            setContent {
                AppTheme {
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
                    val uri = remember { intent.getUri() }

                    BottomDrawer(
                        modifier = if (landscape) Modifier
                            .fillMaxWidth(0.55f)
                            .fillMaxHeight() else Modifier,
                        drawerState = drawerState,
                        sheetContent = {
                            if (bottomSheetViewModel.result != null && uri != null) {
                                if (bottomSheetViewModel.result?.filteredItem == null) {
                                    OpenWith(
                                        result = bottomSheetViewModel.result!!,
                                        uri = uri,
                                        launchScope = launchScope,
                                        drawerState = drawerState
                                    )
                                } else {
                                    OpenWithPreferred(
                                        result = bottomSheetViewModel.result!!,
                                        uri = uri,
                                        launchScope = launchScope,
                                        drawerState = drawerState
                                    )
                                }
                            }
                        })
                }
            }
        }
    }

    companion object {
        val buttonRowHeight = 50.dp
        val appListItemHeight = 60.dp
        val preferredAppItemHeight = 60.dp
        val maxAppListButtonRowHeight = 350.dp
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun OpenWithPreferred(
        result: IntentResolverResult,
        uri: Uri,
        launchScope: CoroutineScope,
        drawerState: ModalBottomSheetState
    ) {
        val filteredItem = result.filteredItem!!

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
                .height(preferredAppItemHeight),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                bitmap = filteredItem.getBitmap(this@BottomSheetActivity),
                contentDescription = filteredItem.displayLabel,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(5.dp))

            Column {
                Text(
                    text = stringResource(
                        id = R.string.open_with_app,
                        filteredItem.displayLabel,
                    ),
                    fontFamily = HkGroteskFontFamily,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        ButtonRow(
            result = result,
            uri = uri,
            enabled = true,
            onClick = { launchScope.launch { launchApp(filteredItem, it) } },
            drawerState = drawerState
        )

        Divider(color = MaterialTheme.colorScheme.tertiary, thickness = 0.5.dp)

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(id = R.string.use_a_different_app),
            fontFamily = HkGroteskFontFamily,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 28.dp)
        )

        Spacer(modifier = Modifier.height(5.dp))

        Column(
            modifier = Modifier.nestedScroll(rememberNestedScrollInteropConnection())
        ) {
            AppList(
                result = result,
                selectedItem = -1,
                onSelectedItemChange = { }, launchScope = launchScope
            )
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun OpenWith(
        result: IntentResolverResult,
        uri: Uri,
        launchScope: CoroutineScope,
        drawerState: ModalBottomSheetState
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(id = R.string.open_with),
            fontFamily = HkGroteskFontFamily,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 28.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        var selectedItem by remember { mutableStateOf(-1) }

        Column(
            modifier = Modifier
                .heightIn(
                    (result.resolved.size * appListItemHeight.value).dp + buttonRowHeight,
                    maxAppListButtonRowHeight
                )
                .nestedScroll(rememberNestedScrollInteropConnection())
        ) {
            AppList(
                result = result,
                selectedItem = selectedItem,
                onSelectedItemChange = { selectedItem = it }, launchScope = launchScope
            )

            ButtonRow(
                result = result,
                uri = uri,
                enabled = selectedItem != -1,
                onClick = { always ->
                    launchScope.launch {
                        launchApp(result.resolved[selectedItem], always)
                    }
                },
                drawerState = drawerState
            )
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun AppList(
        result: IntentResolverResult,
        selectedItem: Int,
        onSelectedItemChange: (Int) -> Unit,
        launchScope: CoroutineScope
    ) {
        if (result.totalCount() > 0) {
            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .heightIn(
                        (result.resolved.size * appListItemHeight.value).dp,
                        maxAppListButtonRowHeight - buttonRowHeight
                    ),
                content = {
                    itemsIndexed(
                        items = result.resolved,
                        key = { _, item -> item.flatComponentName }
                    ) { index, info ->
                        Row(verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .height(appListItemHeight)
                                .fillMaxWidth()
                                .clip(
                                    RoundedCornerShape(6.dp)
                                )
                                .combinedClickable(
                                    onClick = {
                                        if (bottomSheetViewModel.singleTap) {
                                            launchScope.launch {
                                                launchApp(info)
                                            }
                                        } else {
                                            if (selectedItem == index) launchScope.launch {
                                                launchApp(info)
                                            }
                                            else onSelectedItemChange(index)
                                        }
                                    },
                                    onDoubleClick = {
                                        launchScope.launch {
                                            launchApp(info)
                                        }
                                    },
                                    onLongClick = {
                                        if (bottomSheetViewModel.singleTap) {
                                            onSelectedItemChange(index)
                                        }
                                    }
                                )
                                .background(if (selectedItem == index) MaterialTheme.colorScheme.secondaryContainer else androidx.compose.ui.graphics.Color.Transparent)
                                .padding(10.dp)

                        ) {
                            Image(
                                bitmap = info.getBitmap(this@BottomSheetActivity),
                                contentDescription = info.displayLabel,
                                modifier = Modifier.size(32.dp)
                            )

                            Spacer(modifier = Modifier.width(5.dp))

                            Column {
                                Text(text = info.displayLabel)
                                if (result.showExtended || bottomSheetViewModel.alwaysShowPackageName) {
                                    Text(
                                        text = info.packageName,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                            }
                        }
                    }
                })
        } else {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(
                    text = stringResource(id = R.string.no_app_to_handle_link_found)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun ButtonRow(
        result: IntentResolverResult,
        uri: Uri,
        enabled: Boolean,
        onClick: (always: Boolean) -> Unit,
        drawerState: ModalBottomSheetState
    ) {
        val coroutineScope = rememberCoroutineScope()
        val clipboard = remember { getSystemService(ClipboardManager::class.java) }
        val context = LocalContext.current

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(buttonRowHeight)
                .padding(horizontal = 15.dp),
        ) {
            if (bottomSheetViewModel.enableCopyButton) {
                OutlinedButton(contentPadding = PaddingValues(horizontal = 18.dp), onClick = {
                    clipboard.setPrimaryClip(ClipData.newPlainText("URL", uri.toString()))
                    if (!bottomSheetViewModel.disableToasts) {
                        Toast.makeText(context, R.string.url_copied, Toast.LENGTH_SHORT).show()
                    }

                    if (bottomSheetViewModel.hideAfterCopying) {
                        coroutineScope.launch { drawerState.hide() }
                    }
                }) {
                    Text(text = stringResource(id = R.string.copy))
                }

                Spacer(modifier = Modifier.width(2.dp))
            }

            if (bottomSheetViewModel.enableSendButton) {
                OutlinedButton(contentPadding = PaddingValues(horizontal = 18.dp), onClick = {
                    startActivity(Intent().buildSendTo(uri))
                    finish()
                }) {
                    Text(text = stringResource(id = R.string.send_to))
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.End
            ) {
                if (result.totalCount() > 0) {
                    TextButton(
                        enabled = enabled,
                        onClick = { onClick(false) }) {
                        Text(
                            text = stringResource(id = R.string.just_once),
                            fontFamily = HkGroteskFontFamily,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.width(if (bottomSheetViewModel.enableCopyButton && bottomSheetViewModel.enableSendButton) 2.dp else 5.dp))

                    TextButton(
                        enabled = enabled,
                        onClick = { onClick(true) }) {
                        Text(
                            text = stringResource(id = R.string.always),
                            fontFamily = HkGroteskFontFamily,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    TextButton(
                        enabled = true,
                        onClick = {
                            context.startActivity(Intent(context, MainActivity::class.java))
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
    }


    private suspend fun launchApp(info: DisplayActivityInfo, always: Boolean = false) {
        val intentFrom = info.intentFrom(intent.sourceIntent())
        bottomSheetViewModel.persistSelectedIntentAsync(intentFrom, always)

        this.startActivity(intentFrom)
        this.finish()
    }
}