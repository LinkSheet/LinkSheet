package fe.linksheet.composable.settings.link

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fe.libredirectkt.LibRedirect
import fe.libredirectkt.LibRedirectFrontend
import fe.libredirectkt.LibRedirectLoader
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.settings.SettingsViewModel
import fe.linksheet.composable.util.RadioButtonRow
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun LibRedirectServiceSettingsRoute(
    onBackPressed: () -> Unit,
    serviceKey: String,
    viewModel: SettingsViewModel
) {
    val context = LocalContext.current

    val builtinInstances = remember { LibRedirectLoader.loadBuiltInInstances() }
    val builtinServices = remember { LibRedirectLoader.loadBuiltInServices() }

    val service = remember { builtinServices.find { it.key == serviceKey } }

    var expanded by remember { mutableStateOf(false) }
    var selectedFrontend by remember { mutableStateOf<LibRedirectFrontend?>(null) }
    var selectedInstance by remember { mutableStateOf<String?>(null) }

    var instancesForSelectedFrontend = remember { mutableStateListOf<String>() }
//    var selectedInstance = remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadLibRedirectDefault(serviceKey)
    }

    LaunchedEffect(viewModel.libRedirectDefault) {
        val default = viewModel.libRedirectDefault
        Log.d("LibRedirect", "Default $default")
        if (default != null) {
            selectedFrontend =
                builtinServices.find { it.key == serviceKey }?.frontends?.find { it.key == default.frontendKey }
            selectedInstance = default.instanceUrl
        } else {
            selectedFrontend = builtinServices.find { it.key == serviceKey }?.defaultFrontend
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
            contentPadding = PaddingValues(horizontal = 15.dp)
        ) {
            stickyHeader(key = "dropdown") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Text(text = stringResource(id = R.string.frontend))

                    Spacer(modifier = Modifier.height(5.dp))

                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = {
                            expanded = !expanded
                        }
                    ) {
                        TextField(
                            value = selectedFrontend?.name ?: "Loading..",
                            onValueChange = {

                            },
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = expanded
                                )
                            },
                            modifier = Modifier.menuAnchor()
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
                                        selectedInstance = LibRedirect.getDefaultInstanceForFrontend(
                                            selectedFrontend?.key!!
                                        )?.firstOrNull()

                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(text = stringResource(id = R.string.instance))

                    Spacer(modifier = Modifier.height(10.dp))
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