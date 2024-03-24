package fe.linksheet.composable.settings.apps.preferred

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.*
import fe.linksheet.extension.compose.currentActivity
import fe.linksheet.extension.kotlin.collectOnIO
import fe.linksheet.extension.compose.mapHelper
import fe.linksheet.extension.compose.searchHeader
import fe.linksheet.module.viewmodel.PreferredAppSettingsViewModel
import fe.linksheet.util.AndroidVersion
import org.koin.androidx.compose.koinViewModel


@Composable
fun PreferredAppSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: PreferredAppSettingsViewModel = koinViewModel()
) {
    val activity = LocalContext.currentActivity()

    val preferredApps by viewModel.preferredAppsFiltered.collectOnIO()
    val filter by viewModel.searchFilter.collectOnIO()
    val mapState = remember(preferredApps?.size, filter) {
        mapState(preferredApps, filter)
    }

    val appsExceptPreferred = if (AndroidVersion.AT_LEAST_API_31_S) {
        viewModel.appsExceptPreferred.collectOnIO()
    } else remember { mutableStateOf(listOf()) }

    val hostDialog = hostDialog(
        activity,
        fetch = { (displayActivityInfo, hosts) ->
            viewModel.getHostStateAsync(displayActivityInfo, hosts).await()
        },
        onClose = { closeState ->
            val (type, displayActivityInfo, hostState) = closeState!!
            when (type) {
                HostDialogCloseState.Type.Confirm -> viewModel.updateHostState(
                    displayActivityInfo, hostState
                )

                HostDialogCloseState.Type.DeleteAll -> viewModel.deletePreferredAppWherePackage(
                    displayActivityInfo
                )

                HostDialogCloseState.Type.AddAll -> viewModel.insertHostState(
                    displayActivityInfo, hostState
                )
            }
        }
    )

    val appsDialog = appsDialog(appsExceptPreferred = appsExceptPreferred.value, alwaysShowPackageName = viewModel.alwaysShowPackageName(), onClose = { closeState ->
            hostDialog.open(HostDialogState(closeState!!.displayActivityInfo))
        })

    val context = LocalContext.current

    SettingsScaffold(
        R.string.preferred_apps,
        onBackPressed = onBackPressed,
        floatingActionButton = {
            if (AndroidVersion.AT_LEAST_API_31_S) {
                FloatingActionButton(onClick = {
                    appsDialog.open()
                }) {
                    ColoredIcon(icon = Icons.Default.Add, descriptionId = R.string.add)
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 15.dp)
        ) {
            searchHeader(
                subtitleId = R.string.preferred_apps_explainer,
                filter = filter,
                searchFilter = viewModel.searchFilter
            )

            mapHelper(
                noItems = R.string.no_preferred_apps_set_yet,
                notFound = R.string.no_such_app_found,
                mapState = mapState,
                map = preferredApps,
                listKey = { it.hashCode() },
            ) { app, hosts ->
                ClickableRow(onClick = {
                    hostDialog.open(HostDialogState(app, hosts))
                }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            bitmap = app.getIcon(context),
                            contentDescription = app.label,
                            modifier = Modifier.size(42.dp)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            HeadlineText(headline = app.label)

                            Text(
                                text = hosts.joinToString(", ", limit = 20),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            if (viewModel.alwaysShowPackageName()) {
                                Text(
                                    text = app.packageName,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(5.dp))
            }
        }
    }
}



