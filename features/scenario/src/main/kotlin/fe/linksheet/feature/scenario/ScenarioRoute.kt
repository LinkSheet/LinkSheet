package fe.linksheet.feature.scenario

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.linksheet.compose.page.SaneScaffoldSettingsPage
import fe.composekit.route.Route

@Composable
private fun ScenarioRoute(
    navigate: (Route) -> Unit,
) {
    SaneScaffoldSettingsPage(
        headline = "",
//        headline = stringResource(id = R.string.experiments),
        onBackPressed = {

        },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(paddingValues = WindowInsets.navigationBars.asPaddingValues()),
                onClick = {  }
            ) {
                Icon(
                    imageVector = Icons.Default.Restore,
                    contentDescription = ""
//                        stringResource(id = R.string.reset)
                )
            }
        }
    ) {
    }
}
