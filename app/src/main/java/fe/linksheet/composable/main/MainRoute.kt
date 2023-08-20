package fe.linksheet.composable.main

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Patterns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavHostController
import dev.zwander.shared.ShizukuUtil
import fe.linksheet.R
import fe.linksheet.composable.util.ColoredIcon
import fe.linksheet.composable.util.annotatedStringResource
import fe.linksheet.developmentTimeHours
import fe.linksheet.developmentTimeMonths
import fe.linksheet.extension.compose.currentActivity
import fe.linksheet.extension.compose.observeAsState
import fe.linksheet.module.viewmodel.MainViewModel
import fe.linksheet.settingsRoute
import fe.linksheet.shizukuDownload
import fe.linksheet.ui.HkGroteskFontFamily
import fe.linksheet.ui.Typography
import fe.linksheet.util.AndroidVersion
import fe.linksheet.util.Results
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import rikka.shizuku.Shizuku
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainRoute(
    navController: NavHostController,
    viewModel: MainViewModel = koinViewModel()
) {
    val activity = LocalContext.currentActivity()
    val clipboardManager = LocalClipboardManager.current
    val uriHandler = LocalUriHandler.current

    var shizukuInstalled by remember { mutableStateOf(ShizukuUtil.isShizukuInstalled(activity)) }
    var shizukuRunning by remember { mutableStateOf(ShizukuUtil.isShizukuRunning()) }

    var defaultBrowserEnabled by remember { mutableStateOf(Results.loading()) }
    var sheetOpen by remember { mutableStateOf<String?>(null) }
    val useTime = viewModel.formatUseTime()

    LaunchedEffect(Unit) {
        delay(200)
        defaultBrowserEnabled = Results.result(viewModel.checkDefaultBrowser())
    }

    val lifecycleState = LocalLifecycleOwner.current.lifecycle
        .observeAsState(ignoreFirst = Lifecycle.Event.ON_RESUME)

    LaunchedEffect(lifecycleState.first) {
        if (lifecycleState.first == Lifecycle.Event.ON_RESUME) {
            defaultBrowserEnabled = Results.loading()
            defaultBrowserEnabled = Results.result(viewModel.checkDefaultBrowser())

            shizukuInstalled = ShizukuUtil.isShizukuInstalled(activity)
            shizukuRunning = ShizukuUtil.isShizukuRunning()

            sheetOpen = null
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(title = {}, modifier = Modifier.padding(horizontal = 8.dp), navigationIcon = {
            IconButton(onClick = { navController.navigate(settingsRoute) }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(id = R.string.settings)
                )
            }
        })
    }) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            item(key = "title") {
                Row(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.extraLarge)
                        .padding(horizontal = 12.dp)
                        .padding(top = 12.dp, bottom = 3.dp)
                ) {
                    Text(
                        modifier = Modifier,
                        text = stringResource(R.string.app_name),
                        fontFamily = HkGroteskFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 30.sp,
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))
            }

            if (useTime != null) {
                item(key = "donate") {
                    DonateCard(viewModel = viewModel, useTime = useTime)
                }

                item(key = "spacer_0") {
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            item(key = "open_default_browser") {
                OpenDefaultBrowserCard(
                    activity = activity,
                    defaultBrowserEnabled = defaultBrowserEnabled,
                    defaultBrowserChanged = { defaultBrowserEnabled = it },
                    viewModel = viewModel
                )
            }

            item(key = "spacer_1") {
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (viewModel.featureFlagShizuku.value) {
                item(key = "shizuku_card") {
                    ShizukuCard(
                        activity = activity,
                        uriHandler = uriHandler,
                        shizukuInstalled = shizukuInstalled,
                        shizukuRunning = shizukuRunning,

                        viewModel = viewModel
                    )
                }

                item(key = "spacer_2") {
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }


            item(key = "browser_card") {
                BrowserCard(viewModel = viewModel)
            }

            item(key = "spacer_3") {
                Spacer(modifier = Modifier.height(10.dp))
            }

            // sheetOpen is used to avoid the card flickering since clipboardManager.hasText() returns null once the activity looses focus
            if (clipboardManager.hasText() || sheetOpen != null) {
                val item = clipboardManager.getText()?.text

                if ((item != null && Patterns.WEB_URL.matcher(item)
                        .matches()) || sheetOpen != null
                ) {
                    item(key = "open_copied_link") {
                        OpenCopiedLink(
                            uriHandler = uriHandler,
                            item = item ?: sheetOpen!!,
                            sheetOpen = {
                                sheetOpen = item
                            }
                        )
                    }
                }
            }

        }
    }
}

@Composable
fun DonateCard(viewModel: MainViewModel, useTime: Pair<Int?, Int?>) {
    val (hours, minutes) = useTime
    val timeString = if (hours != null) {
        pluralStringResource(id = R.plurals.hours, hours, hours)
    } else pluralStringResource(id = R.plurals.minutes, minutes!!, minutes)

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(10.dp))
            ColoredIcon(
                icon = Icons.Default.Info,
                descriptionId = R.string.donate,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = stringResource(id = R.string.donate_card_headline),
                    style = Typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = annotatedStringResource(
                        id = R.string.donate_card_subtitle,
                        timeString,
                        developmentTimeHours,
                        developmentTimeMonths
                    ),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = annotatedStringResource(id = R.string.donate_card_subtitle_2),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun OpenDefaultBrowserCard(
    activity: Activity,
    defaultBrowserEnabled: Results<Unit>,
    defaultBrowserChanged: (Results<Unit>) -> Unit,
    viewModel: MainViewModel
) {
    val browserLauncherAndroidQPlus = if (AndroidVersion.AT_LEAST_API_29_Q) {
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = {
                defaultBrowserChanged(
                    if (it.resultCode == Activity.RESULT_OK) Results.success()
                    else Results.error()
                )
            }
        )
    } else null

    val shouldUsePrimaryColor = defaultBrowserEnabled.isSuccess || defaultBrowserEnabled.isLoading
    Card(
        colors = CardDefaults.cardColors(containerColor = if (shouldUsePrimaryColor) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.error),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .clickable {
                if (defaultBrowserEnabled.isLoading) {
                    return@clickable
                }

                if (AndroidVersion.AT_LEAST_API_29_Q && !defaultBrowserEnabled.isSuccess) {
                    val intent = viewModel.getRequestRoleBrowserIntent()
                    browserLauncherAndroidQPlus!!.launch(intent)
                } else {
                    viewModel.openDefaultBrowserSettings(activity)
                }
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            val color =
                if (shouldUsePrimaryColor) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onError

            if (defaultBrowserEnabled.isLoading) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = color)
                }
            } else {
                Spacer(modifier = Modifier.width(10.dp))
                ColoredIcon(
                    icon = if (defaultBrowserEnabled.isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                    descriptionId = if (defaultBrowserEnabled.isSuccess) R.string.checkmark else R.string.error,
                    color = color
                )

                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = stringResource(id = if (defaultBrowserEnabled.isSuccess) R.string.browser_status else R.string.set_as_browser),
                        style = Typography.titleLarge,
                        color = color
                    )
                    Text(
                        text = stringResource(id = if (defaultBrowserEnabled.isSuccess) R.string.set_as_browser_done else R.string.set_as_browser_explainer),
                        color = if (defaultBrowserEnabled.isSuccess) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onError
                    )
                }
            }
        }
    }
}

enum class ShizukuStatus(val headline: Int, val subtitle: Int, val usePrimaryColor: Boolean) {
    Enabled(
        R.string.shizuku_integration,
        R.string.shizuku_integration_enabled_explainer,
        true
    ),
    NotRunning(R.string.shizuku_integration, R.string.shizuku_not_running_explainer, false),
    NoPermission(
        R.string.shizuku_integration,
        R.string.shizuku_integration_no_permission_explainer,
        false
    ),
    NotInstalled(
        R.string.shizuku_integration,
        R.string.shizuku_integration_not_setup_explainer,
        false
    );

    companion object {
        fun findStatus(installed: Boolean, running: Boolean, permission: Boolean): ShizukuStatus {
            if (installed && running && permission) return Enabled
            if (!installed) return NotInstalled
            if (!running) return NotRunning

            return NoPermission
        }
    }
}

@Composable
fun ShizukuCard(
    activity: Activity,
    uriHandler: UriHandler,
    shizukuInstalled: Boolean,
    shizukuRunning: Boolean,
    viewModel: MainViewModel
) {
    val shizukuPermission by ShizukuUtil.rememberHasShizukuPermissionAsState()

    var status = ShizukuStatus.findStatus(shizukuInstalled, shizukuRunning, shizukuPermission)
    val scope = rememberCoroutineScope()

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (status.usePrimaryColor) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.tertiaryContainer
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .clickable {
                when (status) {
                    ShizukuStatus.NoPermission -> {
                        scope.launch(Dispatchers.IO) {
                            val granted = suspendCoroutine { cont ->
                                val listener = object : Shizuku.OnRequestPermissionResultListener {
                                    override fun onRequestPermissionResult(
                                        requestCode: Int,
                                        grantResult: Int
                                    ) {
                                        Shizuku.removeRequestPermissionResultListener(
                                            this
                                        )
                                        cont.resume(grantResult == PackageManager.PERMISSION_GRANTED)
                                    }
                                }
                                Shizuku.addRequestPermissionResultListener(listener)
                                Shizuku.requestPermission(100)
                            }

                            if (granted) {
                                status = ShizukuStatus.findStatus(
                                    shizukuInstalled,
                                    shizukuRunning,
                                    shizukuPermission
                                )
                            }
                        }
                    }

                    ShizukuStatus.NotInstalled -> {
                        uriHandler.openUri(shizukuDownload)
                    }

                    else -> {
                        activity.startActivity(
                            Intent(Intent.ACTION_VIEW)
                                .setComponent(
                                    ComponentName(
                                        "moe.shizuku.privileged.api",
                                        "moe.shizuku.manager.MainActivity"
                                    )
                                )
                        )
                    }
                }
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            val color = if (status.usePrimaryColor) MaterialTheme.colorScheme.onSurface
            else MaterialTheme.colorScheme.onTertiaryContainer

            Spacer(modifier = Modifier.width(10.dp))
            ColoredIcon(
                icon = if (status == ShizukuStatus.Enabled) Icons.Default.CheckCircle else Icons.Default.Warning,
                descriptionId = if (status == ShizukuStatus.Enabled) R.string.checkmark else R.string.error,
                color = color
            )

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = stringResource(id = status.headline),
                    style = Typography.titleLarge,
                    color = color
                )
                Text(
                    text = stringResource(id = status.subtitle),
                    color = color
                )
            }
        }
    }
}


@Composable
fun BrowserCard(viewModel: MainViewModel) {
    val browserStatus = viewModel.hasBrowser()

    Card(
        colors = CardDefaults.cardColors(containerColor = browserStatus.containerColor()),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(10.dp))
            ColoredIcon(
                icon = browserStatus.icon,
                descriptionId = browserStatus.iconDescription,
                color = browserStatus.color()
            )

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = stringResource(id = browserStatus.headline),
                    style = Typography.titleLarge,
                    color = browserStatus.color()
                )
                Text(
                    text = stringResource(id = browserStatus.subtitle),
                    color = browserStatus.color()
                )
            }
        }
    }
}

@Composable
fun OpenCopiedLink(uriHandler: UriHandler, item: String, sheetOpen: () -> Unit) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .clickable {
                sheetOpen()
                uriHandler.openUri(item)
            },
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.width(10.dp))

            ColoredIcon(icon = Icons.Default.ContentPaste, descriptionId = R.string.paste)

            Column(modifier = Modifier.padding(15.dp)) {
                Text(
                    text = stringResource(id = R.string.open_copied_link),
                    style = Typography.titleLarge,
                )
                Text(text = item)
            }
        }
    }
}