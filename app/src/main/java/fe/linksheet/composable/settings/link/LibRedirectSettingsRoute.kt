package fe.linksheet.composable.settings.link

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fe.libredirectkt.LibRedirectLoader
import fe.linksheet.LibRedirectServiceRoute
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.ClickableRow
import fe.linksheet.composable.util.Texts
import fe.linksheet.libRedirectServiceSettingsRoute
import fe.linksheet.util.navigate

val libRedirectBuiltInServices by lazy { LibRedirectLoader.loadBuiltInServices() }

@Composable
fun LibRedirectSettingsRoute(
    onBackPressed: () -> Unit,
    navController: NavController,
) {
    SettingsScaffold(R.string.lib_redirect, onBackPressed = onBackPressed) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            items(items = libRedirectBuiltInServices, key = { it.key }) { service ->
                ClickableRow(padding = 10.dp, onClick = {
                    navController.navigate(
                        libRedirectServiceSettingsRoute,
                        LibRedirectServiceRoute(service.key)
                    )
                }) {
                    Texts(headline = service.name, subtitle = service.url)
                }
            }
        }
    }
}