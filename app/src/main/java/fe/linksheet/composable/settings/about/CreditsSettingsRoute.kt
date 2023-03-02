package fe.linksheet.composable.settings.about

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import fe.linksheet.R
import fe.linksheet.composable.SettingsItemRow
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.settings.SettingsViewModel
import fe.linksheet.extension.openLink


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditsSettingsRoute(
    navController: NavHostController,
    onBackPressed: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current

    SettingsScaffold(R.string.credits, onBackPressed = onBackPressed) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            item("openlinkwith") {
                SettingsItemRow(
                    headline = R.string.open_link_with,
                    subtitle = R.string.license_apache_2,
                    onClick = {
                        context.openLink("https://github.com/tasomaniac/OpenLinkWith")
                    })
            }

            item("seal") {
                SettingsItemRow(
                    headline = R.string.seal,
                    subtitle = R.string.license_gpl_3,
                    onClick = {
                        context.openLink("https://github.com/JunkFood02/Seal")
                    }
                )
            }
        }
    }
}

