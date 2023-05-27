package fe.linksheet.composable.settings.link.libredirect

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fe.libredirectkt.LibRedirectLoader
import fe.linksheet.LibRedirectServiceRoute
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.ClickableRow
import fe.linksheet.composable.util.Texts
import fe.linksheet.extension.ioState
import fe.linksheet.libRedirectServiceSettingsRoute
import fe.linksheet.module.database.entity.LibRedirectDefault
import fe.linksheet.module.viewmodel.LibRedirectSettingsViewModel
import fe.linksheet.util.navigate
import org.koin.androidx.compose.koinViewModel

@Composable
fun LibRedirectSettingsRoute(
    onBackPressed: () -> Unit,
    navController: NavController,
    viewModel: LibRedirectSettingsViewModel = koinViewModel()
) {
    val services by viewModel.services.ioState()

    SettingsScaffold(R.string.lib_redirect, onBackPressed = onBackPressed) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            if (services != null) {
                items(items = services!!, key = { it.first.key }) { (service, instance) ->
                    ClickableRow(padding = 10.dp, onClick = {
                        navController.navigate(
                            libRedirectServiceSettingsRoute,
                            LibRedirectServiceRoute(service.key)
                        )
                    }) {
                        val url = if(instance == LibRedirectDefault.libRedirectRandomInstanceKey) stringResource(
                            id = R.string.random_instance
                        ) else instance

                        Texts(
                            headline = service.name, subtitle = if (url != null) {
                                stringResource(
                                    id = R.string.libredirect_via,
                                    service.url,
                                    url
                                )
                            } else service.url
                        )
                    }
                }
            }
        }
    }
}