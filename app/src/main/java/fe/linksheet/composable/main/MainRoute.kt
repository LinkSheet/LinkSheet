package fe.linksheet.composable.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BadgedBox
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
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
import fe.linksheet.composable.settings.SettingsViewModel
import fe.linksheet.extension.observeAsState
import fe.linksheet.settingsRoute
import fe.linksheet.ui.theme.HkGroteskFontFamily
import fe.linksheet.util.Results
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainRoute(
    navController: NavHostController, viewModel: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current
    var defaultBrowserEnabled: Results<Boolean> by remember { mutableStateOf(Results.loading(false)) }

    LaunchedEffect(Unit) {
        delay(200)
        defaultBrowserEnabled = Results.booleanResult(viewModel.checkDefaultBrowser(context))
    }

    val lifecycleState = LocalLifecycleOwner.current.lifecycle
        .observeAsState(ignoreFirst = Lifecycle.Event.ON_RESUME)

    LaunchedEffect(lifecycleState.first) {
        if (lifecycleState.first == Lifecycle.Event.ON_RESUME) {
            defaultBrowserEnabled = Results.loading(false)
            defaultBrowserEnabled = Results.booleanResult(viewModel.checkDefaultBrowser(context))
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(title = {}, modifier = Modifier.padding(horizontal = 8.dp), navigationIcon = {
            IconButton(onClick = {
                navController.navigate(settingsRoute)
            }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(id = R.string.settings)
                )
            }
        })
    }) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
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

            item(key = "open_default_browser") {
                val shouldUsePrimaryColor =
                    defaultBrowserEnabled.isSuccess || defaultBrowserEnabled.isLoading
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor =
                        if (shouldUsePrimaryColor) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            viewModel.openDefaultBrowserSettings(context)
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
                            Image(
                                imageVector = if (defaultBrowserEnabled.isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                                contentDescription = if (defaultBrowserEnabled.isSuccess) "Checkmark" else "Error",
                                colorFilter = if (defaultBrowserEnabled.isSuccess) ColorFilter.tint(
                                    color
                                ) else ColorFilter.tint(
                                    color
                                )
                            )

                            Column(modifier = Modifier.padding(15.dp)) {
                                Text(
                                    text = stringResource(id = if (defaultBrowserEnabled.isSuccess) R.string.browser_status else R.string.set_as_browser),
                                    fontFamily = HkGroteskFontFamily,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 18.sp,
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
        }
    }
}