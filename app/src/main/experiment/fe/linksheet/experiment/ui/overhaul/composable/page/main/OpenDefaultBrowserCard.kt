package fe.linksheet.experiment.ui.overhaul.composable.page.main

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import fe.linksheet.R
import fe.linksheet.experiment.ui.overhaul.composable.component.card.ClickableAlertCard
import fe.linksheet.module.viewmodel.MainViewModel
import fe.linksheet.util.AndroidVersion
import fe.linksheet.util.Results

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

    ClickableAlertCard(
        onClick = {
            if (defaultBrowserEnabled.isLoading) {
                return@ClickableAlertCard
            }

            if (AndroidVersion.AT_LEAST_API_29_Q && !defaultBrowserEnabled.isSuccess) {
                val intent = viewModel.getRequestRoleBrowserIntent()
                browserLauncherAndroidQPlus!!.launch(intent)
            } else {
                viewModel.openDefaultBrowserSettings(activity)
            }
        },
        colors = CardDefaults.cardColors(
            containerColor = if (shouldUsePrimaryColor) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.error
        ),
        imageVector = if (defaultBrowserEnabled.isSuccess) Icons.Default.RocketLaunch else Icons.Default.Error,
        contentDescriptionId = if (defaultBrowserEnabled.isSuccess) R.string.checkmark else R.string.error,
        headlineId = if (defaultBrowserEnabled.isSuccess) R.string.browser_status else R.string.set_as_browser,
        subtitleId = if (defaultBrowserEnabled.isSuccess) R.string.set_as_browser_done else R.string.set_as_browser_explainer
    )
}
