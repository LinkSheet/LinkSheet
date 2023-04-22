package fe.linksheet.composable.settings.apps.preferred

import android.content.pm.verify.domain.DomainVerificationManager
import android.os.Build
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.junkfood.seal.ui.component.PreferenceSubtitle
import com.tasomaniac.openwith.resolver.DisplayActivityInfo
import fe.linksheet.R
import fe.linksheet.composable.util.ClickableRow
import fe.linksheet.composable.util.Searchbar
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.settings.SettingsViewModel
import fe.linksheet.composable.util.ColoredIcon
import fe.linksheet.composable.util.HeadlineText
import fe.linksheet.extension.getAppHosts
import fe.linksheet.extension.startPackageInfoActivity
import fe.linksheet.ui.theme.HkGroteskFontFamily
import kotlinx.coroutines.launch
import timber.log.Timber

enum class ButtonType {
    Confirm, DeleteAll, AddAll
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PreferredAppSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: SettingsViewModel
) {
    val context = LocalContext.current

    val manager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        remember { context.getSystemService(DomainVerificationManager::class.java) }
    } else null


    var filter by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadPreferredApps(context)
        viewModel.filterPreferredAppsAsync(filter)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            viewModel.loadAppsExceptPreferred(context, manager!!)
        }
    }

    var openHostDialog by remember { mutableStateOf(false) }
    var buttonType by remember { mutableStateOf<ButtonType?>(null) }
    val hostMap = remember { mutableStateMapOf<String, Boolean>() }
    var hasAppHosts by remember { mutableStateOf(false) }
    var displayActivityInfo by remember { mutableStateOf<DisplayActivityInfo?>(null) }

    LaunchedEffect(openHostDialog) {
        if (!openHostDialog) {
            Timber.tag("Closed dialog").d("$displayActivityInfo")
            when (buttonType) {
                ButtonType.DeleteAll -> {
                    viewModel.deletePreferredAppWherePackageAsync(displayActivityInfo!!.packageName)
                }
                ButtonType.AddAll -> {
                    viewModel.insertPreferredAppsAsync(
                        hostMap.map { (host, _) ->
                            displayActivityInfo!!.toPreferredApp(
                                host,
                                true
                            )
                        }
                    )
                }
                ButtonType.Confirm -> {
                    hostMap.forEach { (host, enabled) ->
                        if (enabled) viewModel.insertPreferredAppAsync(
                            displayActivityInfo!!.toPreferredApp(
                                host,
                                true
                            )
                        )
                        else viewModel.deletePreferredAppAsync(
                            host,
                            displayActivityInfo!!.packageName
                        )
                    }
                }
                else -> {}
            }

            buttonType = null
            viewModel.loadPreferredApps(context)
            viewModel.filterPreferredAppsAsync(filter)
        }
    }

    if (openHostDialog) {
        AlertDialog(onDismissRequest = { openHostDialog = false }) {
            Surface(shape = MaterialTheme.shapes.large) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .clickable {
                                context.startPackageInfoActivity(displayActivityInfo!!)
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(5.dp))
                        Image(
                            bitmap = displayActivityInfo!!.getBitmap(context),
                            contentDescription = displayActivityInfo!!.displayLabel,
                            modifier = Modifier.size(42.dp)
                        )

                        Text(
                            text = displayActivityInfo!!.displayLabel,
                            fontFamily = HkGroteskFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Box {
                        LazyColumn(modifier = Modifier.padding(bottom = 50.dp), content = {
                            hostMap.toSortedMap().forEach { (host, enabled) ->
                                item(key = host) {
                                    var state by remember { mutableStateOf(enabled) }

                                    ClickableRow(
                                        verticalAlignment = Alignment.CenterVertically,
                                        padding = 2.dp,
                                        onClick = {
                                            hostMap[host] = !state
                                            state = !state
                                        }
                                    ) {
                                        Checkbox(checked = state, onCheckedChange = {
                                            hostMap[host] = it
                                            state = it
                                        })

                                        Spacer(modifier = Modifier.width(5.dp))

                                        Text(text = host)
                                    }
                                }
                            }
                        })

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .height(40.dp)
                        ) {
                            Row(modifier = Modifier.height(40.dp)) {
                                OutlinedButton(
                                    contentPadding = PaddingValues(horizontal = 18.dp),
                                    onClick = {
                                        openHostDialog = false
                                        buttonType = ButtonType.DeleteAll
                                    }) {
                                    Text(text = stringResource(id = R.string.remove_all))
                                }

                                if (hasAppHosts) {
                                    Spacer(modifier = Modifier.width(5.dp))

                                    OutlinedButton(
                                        contentPadding = PaddingValues(horizontal = 18.dp),
                                        onClick = {
                                            openHostDialog = false
                                            buttonType = ButtonType.AddAll
                                        }) {
                                        Text(text = stringResource(id = R.string.add_all))
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = {
                                    openHostDialog = false
                                    buttonType = ButtonType.Confirm
                                }) {
                                    Text(text = stringResource(id = R.string.confirm))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    var openAppsDialog by remember { mutableStateOf(false) }

    if (openAppsDialog) {
        AlertDialog(onDismissRequest = { openAppsDialog = false }) {
            Surface(shape = MaterialTheme.shapes.large) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(color = MaterialTheme.colorScheme.surface)
                    ) {
                        HeadlineText(headline = R.string.select_an_app)
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Box {
                        LazyColumn(modifier = Modifier.padding(bottom = 50.dp), content = {
                            viewModel.appsExceptPreferred.forEach { info ->
                                item(key = info.packageName) {
                                    ClickableRow(
                                        verticalAlignment = Alignment.CenterVertically,
                                        padding = 5.dp,
                                        onClick = {
                                            openAppsDialog = false
                                            openHostDialog = true
                                            hostMap.clear()

                                            if (manager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                                manager.getAppHosts(info.packageName)
                                                    .also { hasAppHosts = it.isNotEmpty() }
                                                    .forEach {
                                                        hostMap[it] = false
                                                    }
                                            }

                                            displayActivityInfo = info

                                        }
                                    ) {
                                        Image(
                                            bitmap = info.getBitmap(context),
                                            contentDescription = info.displayLabel,
                                            modifier = Modifier.size(32.dp)
                                        )

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column {
                                            HeadlineText(headline = info.displayLabel)

                                            if (viewModel.alwaysShowPackageName) {
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
                        })
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .height(40.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = {
                                openAppsDialog = false
                            }) {
                                Text(text = stringResource(id = R.string.close))
                            }
                        }
                    }
                }
            }
        }
    }

    SettingsScaffold(
        R.string.preferred_apps,
        onBackPressed = onBackPressed,
        floatingActionButton = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                FloatingActionButton(onClick = {
                    openAppsDialog = true
                }) {
                    ColoredIcon(icon = Icons.Default.Add, description = R.string.add)
                }
            }
        }) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 15.dp)
        ) {
            stickyHeader(key = "header") {
                Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                    PreferenceSubtitle(
                        text = stringResource(R.string.preferred_apps_explainer),
                        paddingStart = 0.dp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Searchbar(filter = filter, onValueChange = {
                        filter = it
                        coroutineScope.launch { viewModel.filterPreferredAppsAsync(it) }
                    }, onClearClick = {
                        filter = ""
                        coroutineScope.launch { viewModel.filterPreferredAppsAsync(filter) }
                    })

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            if (viewModel.preferredAppsFiltered.isNotEmpty()) {
                viewModel.preferredAppsFiltered.keys.sortedBy { it.displayLabel.lowercase() }.forEach { app ->
                    val hosts = viewModel.preferredAppsFiltered[app]!!
                    item(key = app.flatComponentName) {
                        ClickableRow(padding = 5.dp, onClick = {
                            openHostDialog = true
                            hostMap.clear()

                            if (manager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                manager.getAppHosts(app.packageName)
                                    .also { hasAppHosts = it.isNotEmpty() }.forEach {
                                        hostMap[it] = hosts.contains(it)
                                    }
                            }

                            hosts.forEach { hostMap[it] = true }
                            displayActivityInfo = app
                        }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    bitmap = app.getBitmap(context),
                                    contentDescription = app.displayLabel,
                                    modifier = Modifier.size(42.dp)
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    HeadlineText(headline = app.displayLabel)

                                    Text(
                                        text = hosts.joinToString(", "),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    if (viewModel.alwaysShowPackageName) {
                                        Text(
                                            text = app.packageName,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.tertiary
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            } else {
                item {
                    Column(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .fillParentMaxHeight(0.4f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (viewModel.preferredApps.isEmpty()) {
                            Text(text = stringResource(id = R.string.no_preferred_apps_set_yet))
                        } else {
                            Text(text = stringResource(id = R.string.no_such_app_found))
                        }
                    }
                }
            }
        }
    }
}