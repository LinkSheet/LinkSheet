package fe.linksheet.composable.apps

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsViewModel
import fe.linksheet.ui.theme.HkGroteskFontFamily
import fe.linksheet.util.getBitmapFromImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun AppsWhichCanOpenLinks(
    navController: NavHostController,
    viewModel: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current
    var refreshing by remember { mutableStateOf(false) }

    var filter by remember { mutableStateOf("") }
    val fetch: suspend CoroutineScope.() -> Unit = {
        refreshing = true
        viewModel.loadAppsWhichCanHandleLinks(context, filter)
        delay(100)
        refreshing = false
    }

    LaunchedEffect(Unit, fetch)

    val refreshScope = rememberCoroutineScope()

    val state = rememberPullRefreshState(refreshing, onRefresh = {
        refreshScope.launch(block = fetch)
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
                onValueChange = { value ->
                    filter = value
                    if (value.isNotEmpty()) {
                        viewModel.loadAppsWhichCanHandleLinks(context, value)
                    }
                })

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(modifier = Modifier.fillMaxSize(), content = {
                if (!refreshing) {
                    items(viewModel.whichAppsCanHandleLinks) { info ->
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .clickable {
                                viewModel.openOpenByDefaultSettings(context, info.packageName)
                            }
                            .padding(5.dp), verticalAlignment = Alignment.CenterVertically
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
        }
    }
}