package fe.linksheet.composable.settings.advanced

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.junkfood.seal.ui.component.PreferenceSubtitle
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.SettingEnabledCardColumn
import fe.linksheet.module.viewmodel.ShizukuViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShizukuSettingsRoute(
    navController: NavHostController,
    onBackPressed: () -> Unit,
    viewModel: ShizukuViewModel = koinViewModel(),
) {
    SettingsScaffold(R.string.shizuku, onBackPressed = onBackPressed) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            stickyHeader(key = "header") {
                Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                    PreferenceSubtitle(
                        text = stringResource(id = R.string.shizuku_explainer_short),
                        paddingStart = 10.dp
                    )

                    SettingEnabledCardColumn(
                        checked = false,
                        onChange = {

                        },
                        headline = stringResource(id = R.string.enable_shizuku),
                        subtitle = stringResource(id = R.string.enable_shizuku_explainer)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}