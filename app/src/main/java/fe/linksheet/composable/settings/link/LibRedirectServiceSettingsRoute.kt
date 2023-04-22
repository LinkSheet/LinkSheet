package fe.linksheet.composable.settings.link

import android.util.Log
import android.widget.Space
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.libredirectkt.LibRedirect
import fe.libredirectkt.LibRedirectFrontend
import fe.libredirectkt.LibRedirectLoader
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.settings.SettingsViewModel
import fe.linksheet.composable.util.HeadlineText
import fe.linksheet.composable.util.RadioButtonRow
import fe.linksheet.composable.util.SwitchRow
import fe.linksheet.ui.theme.HkGroteskFontFamily
import kotlinx.coroutines.launch
import timber.log.Timber


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun LibRedirectServiceSettingsRoute(
    onBackPressed: () -> Unit,
    serviceKey: String,
    viewModel: SettingsViewModel
) {
    val context = LocalContext.current

    val builtinInstances = remember { LibRedirectLoader.loadBuiltInInstances() }

    val service = remember { libRedirectBuiltInServices.find { it.key == serviceKey } }

    var expanded by remember { mutableStateOf(false) }
    var selectedFrontend by remember { mutableStateOf<LibRedirectFrontend?>(null) }
    var selectedInstance by remember { mutableStateOf<String?>(null) }

    val instancesForSelectedFrontend = remember { mutableStateListOf<String>() }
//    var selectedInstance = remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadLibRedirectDefault(serviceKey)
        viewModel.loadLibRedirectState(serviceKey)
    }

    LaunchedEffect(viewModel.libRedirectDefault) {
        val default = viewModel.libRedirectDefault
        Timber.tag("LibRedirect").d("Default $default")
        if (default != null) {
            selectedFrontend =
                libRedirectBuiltInServices.find { it.key == serviceKey }?.frontends?.find { it.key == default.frontendKey }
            selectedInstance = default.instanceUrl
        } else {
            selectedFrontend = libRedirectBuiltInServices.find { it.key == serviceKey }?.defaultFrontend
            selectedInstance =
                LibRedirect.getDefaultInstanceForFrontend(selectedFrontend?.key!!)?.firstOrNull()
        }

        instancesForSelectedFrontend.clear()
        builtinInstances.find { it.frontendKey == selectedFrontend?.key }?.let {
            instancesForSelectedFrontend.addAll(it.hosts)
        }
    }

    SettingsScaffold(
        stringResource(id = R.string.lib_redirect_service, service?.name ?: serviceKey),
        onBackPressed = onBackPressed
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            stickyHeader(key = "dropdown") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    SwitchRow(
                        checked = viewModel.libRedirectEnabled ?: false,
                        onChange = {
                            coroutineScope.launch {
                                viewModel.updateLibRedirectState(serviceKey, it)
                            }
                            viewModel.libRedirectEnabled = it
                        },
                        headline = stringResource(id = R.string.enabled),
                        subtitle = null
                    )
                    
                    Spacer(modifier = Modifier.height(5.dp))

                    Column(modifier = Modifier.padding(horizontal = 10.dp).fillMaxWidth()) {
                        HeadlineText(headline = R.string.frontend)

                        Spacer(modifier = Modifier.height(5.dp))

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = {
                                expanded = !expanded
                            }
                        ) {
                            TextField(
                                value = selectedFrontend?.name ?: "Loading..",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = expanded
                                    )
                                },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                service?.frontends?.forEach { frontend ->
                                    DropdownMenuItem(
                                        text = { Text(text = frontend.name) },
                                        onClick = {
                                            selectedFrontend = frontend
                                            instancesForSelectedFrontend.clear()
                                            builtinInstances.find { it.frontendKey == frontend.key }
                                                ?.let {
                                                    instancesForSelectedFrontend.addAll(it.hosts)
                                                }
                                            selectedInstance =
                                                LibRedirect.getDefaultInstanceForFrontend(
                                                    selectedFrontend?.key!!
                                                )?.firstOrNull()

                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(5.dp))

                        HeadlineText(headline = R.string.instance)

                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }

            instancesForSelectedFrontend.forEach { instance ->
                item {
                    RadioButtonRow(
                        onClick = {
                            selectedInstance = instance

                            selectedFrontend?.key?.let { frontendKey ->
                                coroutineScope.launch {
                                    viewModel.saveLibRedirectDefault(
                                        serviceKey,
                                        frontendKey,
                                        instance
                                    )
                                }
                            }
                        },
                        onLongClick = null,
                        selected = instance == selectedInstance
                    ) {
                        Text(text = instance)
                    }
                }
            }
        }
    }
}