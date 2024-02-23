package fe.linksheet.composable.settings.advanced

//import fe.linksheet.module.viewmodel.FeatureFlagViewModel
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import fe.linksheet.composable.util.PreferenceSubtitle
import dev.zwander.shared.ShizukuUtil.rememberHasShizukuPermissionAsState
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.SwitchRow
import fe.linksheet.module.viewmodel.FeatureFlagViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeatureFlagSettingsRoute(
    navController: NavHostController,
    onBackPressed: () -> Unit,
    viewModel: FeatureFlagViewModel = koinViewModel(),
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

//    var shizukuInstalled by remember { mutableStateOf(ShizukuUtil.isShizukuInstalled(activity)) }
//    var shizukuRunning by remember { mutableStateOf(isShizukuRunning()) }

    val shizukuPermission by rememberHasShizukuPermissionAsState()

    SettingsScaffold(R.string.feature_flags, onBackPressed = onBackPressed) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight(),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            stickyHeader(key = "header") {
                Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                    PreferenceSubtitle(
                        text = stringResource(id = R.string.feature_flags_explainer),
                        paddingHorizontal = 10.dp
                    )
                }
            }

//            item(key = "enable_shizuku_feature_flag") {
//                SwitchRow(
//                    state = viewModel.featureFlagShizuku,
//                    viewModel = viewModel,
//                    headlineId = R.string.enable_shizuku,
//                    subtitleId = R.string.enable_shizuku_explainer
//                )
//            }

            item(key = "enable_linksheet_compat_menu") {
                SwitchRow(
                    state = viewModel.featureFlagLinkSheetCompat,
                    headlineId = R.string.enable_linksheet_compat,
                    subtitleId = R.string.enable_linksheet_compat_explainer
                )
            }

//            item(key = "enable_new_bottom_sheet") {
//                SwitchRow(
//                    state = viewModel.featureFlagNewBottomSheet,
//                    viewModel = viewModel,
//                    headlineId = R.string.enable_new_bottom_sheet,
//                    subtitleId = R.string.enable_new_bottom_sheet_explainer
//                )
//            }
        }
    }
}
