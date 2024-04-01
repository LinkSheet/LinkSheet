package fe.linksheet.composable.settings.dev

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsScaffold
import fe.linksheet.composable.util.LinkableTextView
import fe.linksheet.module.viewmodel.DevSettingsViewModel
import fe.linksheet.ui.Typography
import org.koin.androidx.compose.koinViewModel


@Composable
fun DevBottomSheetSettingsRoute(
//    navController: NavHostController,
    onBackPressed: () -> Unit,
    viewModel: DevSettingsViewModel = koinViewModel()
) {
    val context = LocalContext.current

    SettingsScaffold(R.string.new_dev_bottom_sheet, onBackPressed = onBackPressed) { padding ->
//        LazyColumn(
//            modifier = Modifier
//                .padding(padding)
//                .fillMaxHeight(),
//            contentPadding = PaddingValues(horizontal = 5.dp)
//        ) {
//            item(key = "dev_bottomsheet") {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 80.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = stringResource(id = R.string.information),
                        style = Typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    LinkableTextView(
                        id = R.string.new_dev_bottom_sheet_feature_explainer,
                        style = LocalTextStyle.current.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 16.sp
                        )
                    )
                }
            }
        }
    }
}
