package fe.linksheet.composable.apps

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import fe.linksheet.R
import fe.linksheet.composable.settings.SettingsViewModel
import fe.linksheet.ui.theme.HkGroteskFontFamily
import fe.linksheet.util.getBitmapFromImage

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun AppsWhichCanOpenLinks(
    navController: NavHostController,
    viewModel: SettingsViewModel = viewModel()
) {
    val context = LocalContext.current

    Column(modifier = Modifier.padding(horizontal = 15.dp)) {
        Text(
            text = stringResource(id = R.string.apps_which_can_open_links),
            fontFamily = HkGroteskFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = stringResource(id = R.string.apps_which_can_open_links_explainer),
            fontFamily = HkGroteskFontFamily,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(10.dp))

        LazyColumn(content = {
            items(viewModel.loadAppsWhichCanHandleLinks(context)) { info ->
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.openOpenByDefaultSettings(context, info.packageName())
                    }, verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        bitmap = getBitmapFromImage(
                            context,
                            info.displayIcon!!
                        ).asImageBitmap(),
                        contentDescription = info.displayLabel,
                        modifier = Modifier.size(42.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = info.displayLabel, fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        })
    }
}