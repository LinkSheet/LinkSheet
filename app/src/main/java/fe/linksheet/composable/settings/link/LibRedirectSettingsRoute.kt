package fe.linksheet.composable.settings.link

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fe.libredirectkt.LibRedirectLoader
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.settings.SettingsViewModel
import fe.linksheet.composable.util.ClickableRow
import fe.linksheet.composable.util.Texts
import fe.linksheet.libRedirectServiceSettingsRoute


@Composable
fun LibRedirectSettingsRoute(
    onBackPressed: () -> Unit,
    navController: NavController,
    viewModel: SettingsViewModel,
) {
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