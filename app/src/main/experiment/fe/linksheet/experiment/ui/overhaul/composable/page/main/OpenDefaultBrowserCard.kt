package fe.linksheet.experiment.ui.overhaul.composable.page.main

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.zwander.shared.ShizukuUtil
import fe.linksheet.R
import fe.linksheet.composable.util.ColoredIcon
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.ShapeListItemDefaults
import fe.linksheet.experiment.ui.overhaul.composable.component.list.item.type.ClickableAlertListItem
import fe.linksheet.module.shizuku.ShizukuStatus
import fe.linksheet.module.viewmodel.MainViewModel
import fe.linksheet.shizukuDownload
import fe.linksheet.ui.Typography
import fe.linksheet.util.AndroidVersion
import fe.linksheet.util.Results
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun OpenDefaultBrowserCard(
    activity: Activity,
    defaultBrowserEnabled: Results<Unit>,
    defaultBrowserChanged: (Results<Unit>) -> Unit,
    viewModel: MainViewModel,
) {
    val browserLauncherAndroidQPlus = if (AndroidVersion.AT_LEAST_API_29_Q) {
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = {
                defaultBrowserChanged(
                    if (it.resultCode == Activity.RESULT_OK) Results.success()
                    else Results.error()
                )
            }
        )
    } else null

    val shouldUsePrimaryColor = defaultBrowserEnabled.isSuccess || defaultBrowserEnabled.isLoading

    ClickableAlertListItem(
        onClick = {
            if (defaultBrowserEnabled.isLoading) {
                return@ClickableAlertListItem
            }

            if (AndroidVersion.AT_LEAST_API_29_Q && !defaultBrowserEnabled.isSuccess) {
                val intent = viewModel.getRequestRoleBrowserIntent()
                browserLauncherAndroidQPlus!!.launch(intent)
            } else {
                viewModel.openDefaultBrowserSettings(activity)
            }
        },
        colors = ShapeListItemDefaults.colors(
            containerColor = if (shouldUsePrimaryColor) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.error
        ),
        imageVector = if (defaultBrowserEnabled.isSuccess) Icons.Default.RocketLaunch else Icons.Default.Error,
        contentDescriptionTextId = if (defaultBrowserEnabled.isSuccess) R.string.checkmark else R.string.error,
        headlineContentTextId = if (defaultBrowserEnabled.isSuccess) R.string.browser_status else R.string.set_as_browser,
        supportingContentTextId = if (defaultBrowserEnabled.isSuccess) R.string.set_as_browser_done else R.string.set_as_browser_explainer
    )
}
