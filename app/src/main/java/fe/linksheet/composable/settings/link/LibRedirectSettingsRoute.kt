package fe.linksheet.composable.settings.link

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tasomaniac.openwith.resolver.DisplayActivityInfo
import fe.libredirectkt.LibRedirect
import fe.libredirectkt.LibRedirectLoader
import fe.libredirectkt.LibRedirectService
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.settings.SettingsViewModel
import fe.linksheet.composable.settings.apps.preferred.ButtonType
import fe.linksheet.composable.util.ClickableRow
import fe.linksheet.composable.util.Texts
import fe.linksheet.extension.startPackageInfoActivity
import fe.linksheet.libRedirectServiceSettingsRoute
import fe.linksheet.ui.theme.HkGroteskFontFamily
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibRedirectSettingsRoute(
    onBackPressed: () -> Unit,
    navController: NavController,
    viewModel: SettingsViewModel,
) {
    val context = LocalContext.current

    val builtinInstances = remember { LibRedirectLoader.loadBuiltInInstances() }

    var openDialog by remember { mutableStateOf(false) }
    var libRedirectService by remember { mutableStateOf<LibRedirectService?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var selectedFrontend by remember { mutableStateOf("") }
    val frontendHosts = remember { mutableStateListOf<String>() }

    val coroutineScope = rememberCoroutineScope()

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
                            .clip(RoundedCornerShape(6.dp)),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            text = libRedirectService!!.name,
                            fontFamily = HkGroteskFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(text = stringResource(id = R.string.frontend))

                    Spacer(modifier = Modifier.height(5.dp))


                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = {
                            expanded = !expanded
                        }
                    ) {
                        TextField(
                            value = libRedirectService!!.frontends.find { it.key == selectedFrontend }?.name
                                ?: "",
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
                            libRedirectService?.frontends?.forEach { frontend ->
                                DropdownMenuItem(
                                    text = { Text(text = frontend.name) },
                                    onClick = {
                                        selectedFrontend = frontend.key
                                        frontendHosts.clear()
                                        builtinInstances.find { it.frontendKey == selectedFrontend }?.hosts?.let {
                                            frontendHosts.addAll(it)
                                        }
                                        Log.d("FrontendHosts", "${frontendHosts.toList()}")
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }



                    Box {
                        LazyColumn(modifier = Modifier.padding(bottom = 40.dp), content = {
                            frontendHosts.forEach {
                                Log.d("FrontendHost", it)
                                item(key = it) {
                                    Text(text = it)
                                }
                            }
                        })


                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp),
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = {
                                openDialog = false
                            }) {
                                Text(text = stringResource(id = R.string.confirm))
                            }
                        }
                    }
                }
            }
        }
    }

    SettingsScaffold(R.string.lib_redirect, onBackPressed = onBackPressed) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            LibRedirectLoader.loadBuiltInServices().forEach {
                item(key = it.key) {
                    ClickableRow(padding = 10.dp, onClick = {
                        navController.navigate(
                            libRedirectServiceSettingsRoute.replace(
                                "{service}",
                                it.key
                            )
                        )
                    }) {
                        Texts(headline = it.name, subtitle = it.url)
                    }
                }
            }
        }
    }
}