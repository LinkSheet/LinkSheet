package fe.linksheet.composable.settings.link.libredirect

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import fe.linksheet.LibRedirectServiceRoute
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.ClickableRow
import fe.linksheet.composable.util.LinkableTextView
import fe.linksheet.composable.util.SettingEnabledCardColumn
import fe.linksheet.composable.util.SubtitleText
import fe.linksheet.composable.util.Texts
import fe.linksheet.composable.util.listState
import fe.linksheet.extension.ioState
import fe.linksheet.extension.listHelper
import fe.linksheet.libRedirectServiceSettingsRoute
import fe.linksheet.libRedirectSettingsRoute
import fe.linksheet.module.database.entity.LibRedirectDefault
import fe.linksheet.module.viewmodel.LibRedirectSettingsViewModel
import fe.linksheet.util.cleanHttpsScheme
import fe.linksheet.util.navigate
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LibRedirectSettingsRoute(
    onBackPressed: () -> Unit,
    navController: NavController,
    viewModel: LibRedirectSettingsViewModel = koinViewModel()
) {
    val services by viewModel.services.ioState()

    val listState = remember(services?.size) {
        listState(services)
    }

    SettingsScaffold(R.string.lib_redirect, onBackPressed = onBackPressed) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            stickyHeader(key = "enabled") {
                SettingEnabledCardColumn(
                    state = viewModel.enableLibRedirect,
                    viewModel = viewModel,
                    headline = stringResource(id = R.string.enable_libredirect),
                    subtitle = null,
                    subtitleBuilder = {
                        LinkableTextView(
                            id = R.string.enable_libredirect_explainer,
                            style = LocalTextStyle.current.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp,
                            )
                        )
                    },
                    contentTitle = stringResource(id = R.string.services)
                )
            }

            listHelper(
                noItems = R.string.no_libredirect_services,
                notFound = R.string.no_such_service_found,
                listState = listState,
                list = services,
                listKey = { it.service.key },
            ) { (service, enabled, instance) ->
                ClickableRow(
                    padding = 10.dp,
                    onClick = {
                        navController.navigate(
                            libRedirectServiceSettingsRoute,
                            LibRedirectServiceRoute(service.key)
                        )
                    }
                ) {
                    Texts(
                        headline = service.name,
                        subtitle = cleanHttpsScheme(service.url),
                    ) {
                        if (enabled) {
                            SubtitleText(
                                fontStyle = FontStyle.Italic,
                                subtitle = stringResource(
                                    id = R.string.libredirect_via,
                                    instanceUrl(instance = instance!!)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun instanceUrl(
    instance: String
) = if (instance == LibRedirectDefault.libRedirectRandomInstanceKey) stringResource(
    id = R.string.random_instance
) else instance