package fe.linksheet.composable.apps

import android.content.pm.verify.domain.DomainVerificationManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import fe.linksheet.R
import fe.linksheet.composable.ClickableRow
import fe.linksheet.composable.settings.SettingsViewModel
import fe.linksheet.extension.observeAsState
import fe.linksheet.ui.theme.HkGroteskFontFamily
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun AppsWhichCanOpenLinksSettingsRoute(
    navController: NavHostController,
    viewModel: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current
    val manager = remember { context.getSystemService(DomainVerificationManager::class.java) }

    var refreshing by remember { mutableStateOf(false) }
    var filter by remember { mutableStateOf("") }

    val fetch: suspend CoroutineScope.(Boolean) -> Unit = { fetchRefresh ->
        if (fetchRefresh) {
            refreshing = true
        }

        viewModel.loadAppsWhichCanHandleLinksAsync(context, manager).await()
        viewModel.filterWhichAppsCanHandleLinksAsync(filter).await()

        if (fetchRefresh) {
            delay(100)
            refreshing = false
        }
    }

    val lifecycleState = LocalLifecycleOwner.current.lifecycle.observeAsState(Lifecycle.Event.ON_RESUME)

    LaunchedEffect(lifecycleState.first) {
        if (lifecycleState.first == Lifecycle.Event.ON_RESUME) {
            fetch(false)
        }
    }

    LaunchedEffect(Unit) {
        fetch(false)
    }

    val refreshScope = rememberCoroutineScope()

    val state = rememberPullRefreshState(refreshing, onRefresh = {
        refreshScope.launch(block = { fetch(true) })
    })

    Box(modifier = Modifier.pullRefresh(state)) {
        PullRefreshIndicator(refreshing, state, Modifier.align(Alignment.TopCenter))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 15.dp)
        ) {
            Text(
                text = stringResource(id = R.string.apps_which_can_open_links),
                fontFamily = HkGroteskFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = stringResource(id = R.string.apps_which_can_open_links_explainer),
                fontFamily = HkGroteskFontFamily,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text(text = stringResource(id = R.string.search)) },
                colors = TextFieldDefaults.outlinedTextFieldColors(focusedBorderColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(size = 32.dp),
                value = filter,
                trailingIcon = {
                    if (filter.isNotEmpty()) {
                        IconButton(onClick = {
                            filter = ""
                            viewModel.filterWhichAppsCanHandleLinksAsync(filter)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(id = R.string.clear),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                onValueChange = { value ->
                    filter = value
                    viewModel.filterWhichAppsCanHandleLinksAsync(value)
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (viewModel.whichAppsCanHandleLinksFiltered.isNotEmpty()) {
                LazyColumn(modifier = Modifier.fillMaxSize(), content = {
                    if (!refreshing) {
                        items(
                            viewModel.whichAppsCanHandleLinksFiltered,
                            key = { it.flatComponentName }
                        ) { info ->
                            ClickableRow(
                                padding = 5.dp,
                                onClick = {
                                    viewModel.openOpenByDefaultSettings(
                                        context,
                                        info.packageName
                                    )
                                },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    bitmap = info.getBitmap(context),
                                    contentDescription = info.displayLabel,
                                    modifier = Modifier.size(42.dp)
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(
                                        text = info.displayLabel, fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                })
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(0.3f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (viewModel.whichAppsCanHandleLinks.isEmpty()) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    } else {
                        Text(
                            text = stringResource(id = R.string.no_such_app_found),
                        )
                    }
                }
            }
        }
    }
}