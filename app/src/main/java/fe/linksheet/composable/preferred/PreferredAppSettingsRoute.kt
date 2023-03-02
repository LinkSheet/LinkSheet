package fe.linksheet.composable.preferred

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.junkfood.seal.ui.component.BackButton
import com.junkfood.seal.ui.component.PreferenceSubtitle
import com.tasomaniac.openwith.resolver.DisplayActivityInfo
import fe.linksheet.R
import fe.linksheet.composable.ClickableRow
import fe.linksheet.composable.Searchbar
import fe.linksheet.composable.settings.SettingsViewModel
import fe.linksheet.extension.getAppHosts
import fe.linksheet.extension.startPackageInfoActivity
import fe.linksheet.ui.theme.HkGroteskFontFamily
import kotlinx.coroutines.launch

enum class ButtonType {
    Confirm, DeleteAll, AddAll
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PreferredAppSettingsRoute(
    navController: NavHostController,
    onBackPressed: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
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
    }

    var openDialog by remember { mutableStateOf(false) }
    var buttonType by remember { mutableStateOf<ButtonType?>(null) }
    val hostMap = remember { mutableStateMapOf<String, Boolean>() }
    var hasAppHosts by remember { mutableStateOf(false) }
    var displayActivityInfo by remember { mutableStateOf<DisplayActivityInfo?>(null) }

    LaunchedEffect(openDialog) {
        if (!openDialog) {
            Log.d("Closed dialog", "$displayActivityInfo")
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

    if (openDialog) {
        AlertDialog(onDismissRequest = { openDialog = false }) {
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

                    Box{
                        LazyColumn(modifier = Modifier.padding(bottom = 40.dp), content = {
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
                                        openDialog = false
                                        buttonType = ButtonType.DeleteAll
                                    }) {
                                    Text(text = stringResource(id = R.string.remove_all))
                                }

                                if (hasAppHosts) {
                                    Spacer(modifier = Modifier.width(5.dp))

                                    OutlinedButton(
                                        contentPadding = PaddingValues(horizontal = 18.dp),
                                        onClick = {
                                            openDialog = false
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
                                    openDialog = false
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


    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(),
        canScroll = { true }
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        modifier = Modifier,
                        text = stringResource(id = R.string.preferred_apps),
                        fontFamily = HkGroteskFontFamily,
                        fontWeight = FontWeight.SemiBold
                    )
                }, navigationIcon = {
                    BackButton {
                        onBackPressed()
                    }
                }, scrollBehavior = scrollBehavior
            )
        }, content = { padding ->
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
                    viewModel.preferredAppsFiltered.toSortedMap().forEach { (app, hosts) ->
                        item(key = app.flatComponentName) {
                            ClickableRow(padding = 5.dp, onClick = {
                                openDialog = true
                                hostMap.clear()

                                if (manager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                    manager.getAppHosts(app.packageName).also { hasAppHosts = it.isNotEmpty() }.forEach {
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
                                        Text(
                                            text = app.displayLabel, fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontFamily = HkGroteskFontFamily,
                                            fontWeight = FontWeight.SemiBold
                                        )

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
        })

}