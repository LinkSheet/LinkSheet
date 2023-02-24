package fe.linksheet.composable.preferred

import android.content.pm.verify.domain.DomainVerificationManager
import android.os.Build
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialog

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.tasomaniac.openwith.data.PreferredApp
import com.tasomaniac.openwith.resolver.DisplayActivityInfo
import fe.linksheet.R
import fe.linksheet.composable.ClickableRow
import fe.linksheet.composable.settings.SettingsViewModel
import fe.linksheet.extension.getAppHosts
import fe.linksheet.ui.theme.HkGroteskFontFamily
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferredSettingsRoute(
    navController: NavHostController,
    viewModel: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current

    val manager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        context.getSystemService(DomainVerificationManager::class.java)
    } else null

    LaunchedEffect(Unit) {
        viewModel.loadPreferredApps(context)
    }

    var openDialog by remember { mutableStateOf(false) }
    val hostMap = remember { mutableStateMapOf<String, Boolean>() }
    var displayActivityInfo by remember { mutableStateOf<DisplayActivityInfo?>(null) }

    LaunchedEffect(openDialog) {
        if (!openDialog) {
            runBlocking {
                val tasks = hostMap.map { (host, enabled) ->
                    if (enabled) viewModel.insertPreferredAppAsync(displayActivityInfo!!.toPreferredApp(host, true))
                    else viewModel.deletePreferredAppAsync(host)
                }

                tasks.awaitAll()
                viewModel.loadPreferredApps(context)
            }
        }
    }

    if (openDialog) {
        AlertDialog(onDismissRequest = { openDialog = false }) {
            Surface(shape = MaterialTheme.shapes.large) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
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

                    LazyColumn(content = {
                        hostMap.forEach { (host, enabled) ->
                            item(key = host) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    var state by remember { mutableStateOf(enabled) }
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
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { openDialog = false }) {
                            Text(text = stringResource(id = R.string.confirm))
                        }
                    }
                }
            }
        }
    }

    Column(modifier = Modifier.padding(horizontal = 15.dp)) {
        Text(
            text = stringResource(id = R.string.preferred_apps),
            fontFamily = HkGroteskFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (viewModel.preferredApps.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            LazyColumn(content = {
                viewModel.preferredApps.forEach { (app, hosts) ->
                    item(key = app.packageName) {
                        ClickableRow(padding = 5.dp, onClick = {
                            openDialog = true
                            hostMap.clear()

                            if (manager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                manager.getAppHosts(app.packageName).forEach {
                                    hostMap[it] = hosts.contains(it)
                                }
                            } else {
                                hosts.forEach {
                                    hostMap[it] = true
                                }
                            }

                            displayActivityInfo = app
                        }) {
                            Row {
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
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            })
        } else {
            Text(text = stringResource(id = R.string.no_preferred_apps_set_yet))
        }
    }
}