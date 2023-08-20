package fe.linksheet.composable.settings.apps

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.linksheet.composable.util.PreferenceSubtitle
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.ClickableRow
import fe.linksheet.composable.util.HeadlineText
import fe.linksheet.extension.compose.currentActivity
import fe.linksheet.linkSheetCompatGithubReleases
import fe.linksheet.module.viewmodel.PretendToBeAppSettingsViewModel
import org.koin.androidx.compose.koinViewModel


@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PretendToBeAppSettingsRoute(
    onBackPressed: () -> Unit,
    viewModel: PretendToBeAppSettingsViewModel = koinViewModel()
) {
    val activity = LocalContext.currentActivity()
    val installed = viewModel.checkIsCompatInstalled()
    val uriHandler = LocalUriHandler.current

    SettingsScaffold(
        R.string.pretend_to_be_app,
        onBackPressed = onBackPressed,
        floatingActionButton = {
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 15.dp)
        ) {
            stickyHeader(key = "header") {
                Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                    PreferenceSubtitle(
                        text = stringResource(id = R.string.pretend_to_be_app_explainer_2),
                        paddingHorizontal = 0.dp
                    )
                }

                ClickableRow(
                    enabled = !installed,
                    onClick = {
                        uriHandler.openUri(linkSheetCompatGithubReleases)
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        imageVector = Icons.Default.Download,
                        contentDescription = stringResource(id = R.string.download),
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        HeadlineText(headline = stringResource(id = R.string.download))

                        if (installed) {
                            Text(
                                text = stringResource(id = R.string.compat_installed),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }

                ClickableRow(
                    enabled = installed,
                    onClick = { activity.startActivity(viewModel.configureCompatIntent()) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        imageVector = Icons.Default.Tune,
                        contentDescription = stringResource(id = R.string.configure),
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        HeadlineText(headline = stringResource(id = R.string.configure))

                        Text(
                            text = stringResource(id = R.string.configure_explainer),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }

            item(key = "configure") {

            }

//            listHelper(
//                noItems = R.string.no_preferred_apps_set_yet,
//                notFound = R.string.no_such_app_found,
//                listState = listState,
//                list = preferredApps,
//                listKey = { it.first.flatComponentName },
//            ) { (app, hosts) ->
//                ClickableRow(onClick = {
//                    hostDialog.open(HostDialogState(app, hosts))
//                }) {
//                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        Image(
//                            bitmap = app.iconBitmap,
//                            contentDescription = app.label,
//                            modifier = Modifier.size(42.dp)
//                        )
//
//                        Spacer(modifier = Modifier.width(10.dp))
//
//                        Column {
//                            HeadlineText(headline = app.label)
//
//                            Text(
//                                text = hosts.joinToString(", "),
//                                maxLines = 1,
//                                overflow = TextOverflow.Ellipsis,
//                                color = MaterialTheme.colorScheme.onSurface
//                            )
//
//                            if (viewModel.alwaysShowPackageName.value) {
//                                Text(
//                                    text = app.packageName,
//                                    fontSize = 12.sp,
//                                    color = MaterialTheme.colorScheme.tertiary
//                                )
//                            }
//                        }
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(5.dp))
//            }
        }
    }
}



